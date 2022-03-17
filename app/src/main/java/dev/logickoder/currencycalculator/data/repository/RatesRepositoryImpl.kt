package dev.logickoder.currencycalculator.data.repository

import android.content.Context
import com.google.gson.JsonObject
import dev.logickoder.currencycalculator.data.api.DAYS_CAP
import dev.logickoder.currencycalculator.data.api.RatesApi
import dev.logickoder.currencycalculator.data.model.HistoricalRates
import dev.logickoder.currencycalculator.data.store.DataStoreManager
import dev.logickoder.currencycalculator.ui.screens.main.Amount
import dev.logickoder.currencycalculator.ui.screens.main.ChartData
import dev.logickoder.currencycalculator.ui.screens.main.CurrencyCode
import dev.logickoder.currencycalculator.ui.screens.main.Date
import dev.logickoder.currencycalculator.utils.ResultWrapper
import dev.logickoder.currencycalculator.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *
 */
class RatesRepositoryImpl(
    private val localSource: DataStoreManager<HistoricalRates>,
    private val remoteSource: RatesApi,
) : Repository<HistoricalRates>() {

    /**
     * Converts the amount from [from] currency to [to] currency based on the rates at [rateAtDay]
     */
    private fun convert(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: Amount,
        rateAtDay: JsonObject
    ): Amount {
        val fromRate = rateAtDay.getAsJsonPrimitive(from).asDouble
        val toRate = rateAtDay.getAsJsonPrimitive(to).asDouble
        return (toRate / fromRate) * (amount ?: 0.0)
    }

    override suspend fun Context.retrieve(source: DataSource): Flow<ResultWrapper<HistoricalRates>> {
        return when (source) {
            DataSource.LOCAL -> localSource.get(0L, this)
            DataSource.REMOTE -> {
                val now = LocalDate.now()
                val result = safeApiCall(Dispatchers.IO) {
                    remoteSource.retrieveRates(
                        startDate = now.minusDays(DAYS_CAP.toLong())
                            .format(DateTimeFormatter.ISO_LOCAL_DATE),
                        endDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    )
                }
                return when (result) {
                    is ResultWrapper.Success -> {
                        localSource.save(result.data, this)
                        flowOf(result)
                    }
                    is ResultWrapper.Failure -> retrieve(DataSource.LOCAL)
                    else -> flowOf(result)
                }
            }
        }
    }

    /**
     * Converts the amount from [from] to [to]
     */
    suspend fun convert(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: Amount,
        context: Context,
    ): Flow<ResultWrapper<Amount>> = with(context) {
        retrieve(DataSource.REMOTE).map { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    // get the rates for today and convert them
                    val todayRate = result.data.rates.getAsJsonObject(result.data.endDate)
                    ResultWrapper.Success(convert(from, to, amount, todayRate))
                }
                is ResultWrapper.Failure -> ResultWrapper.Failure(result.error)
                is ResultWrapper.Loading -> result
            }
        }
    }

    /**
     * Returns the date of the latest rates available
     */
    suspend fun getRateDate(context: Context): Flow<ResultWrapper<Date>> = with(context) {
        return retrieve(DataSource.LOCAL).map { result ->
            when (result) {
                is ResultWrapper.Success -> ResultWrapper.Success(result.data.date)
                is ResultWrapper.Failure -> ResultWrapper.Failure(result.error)
                is ResultWrapper.Loading -> result
            }
        }
    }

    /**
     * Generates the data used to render the chart
     */
    suspend fun generateChartData(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: Amount,
        context: Context,
    ): Flow<ResultWrapper<List<ChartData>>> = with(context) {
        retrieve(DataSource.LOCAL).map { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    val rates = result.data
                    val startDate = LocalDate.parse(rates.startDate)
                    val daysBetween = Duration.between(
                        startDate.atStartOfDay(), LocalDate.parse(rates.endDate).atStartOfDay()
                    ).toDays()
                    // generate the chart data
                    ResultWrapper.Success(
                        (1..daysBetween).map { day ->
                            val date = startDate.plusDays(day)
                            date.format(DateTimeFormatter.ofPattern("d MMM")) to
                                    convert(
                                        from, to, amount,
                                        rates.rates.getAsJsonObject(
                                            date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                        )
                                    )
                        }
                    )
                }
                is ResultWrapper.Failure -> ResultWrapper.Failure(result.error)
                is ResultWrapper.Loading -> result
            }
        }
    }
}