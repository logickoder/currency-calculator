package dev.logickoder.currencycalculator.ui.screens.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import dev.logickoder.currencycalculator.data.api.RatesApiImpl
import dev.logickoder.currencycalculator.data.repository.RatesRepositoryImpl
import dev.logickoder.currencycalculator.data.store.RatesStoreImpl
import dev.logickoder.currencycalculator.utils.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

typealias CurrencyCode = String
typealias CurrencyFlag = String
typealias Amount = Double?
typealias Date = String
typealias ChartData = Pair<Date, Amount>

enum class ChartLoading {
    Empty, Loading, Success
}


/**
 * @property firstCurrencyCode the currency to convert from
 * @property firstCurrencyAmount the amount to convert
 * @property secondCurrencyCode the currency to convert to
 * @property secondCurrencyAmount the amount of the converted currency
 * @property chartData the data used to populate the graph
 * @property isConverting boolean showing if currency conversion is currently taking place
 * @property isChartLoading the state of the graph
 * @property exchangeDate the date of the most currenct exchange data
 */
class CurrencyCalculatorUiState(
    val currencies: List<CurrencyCode>,
    private val repository: RatesRepositoryImpl,
    private val context: Context,
    private val flag: (CurrencyCode) -> CurrencyFlag
) {
    val firstCurrencyCode = mutableStateOf(currencies[0])
    val secondCurrencyCode = mutableStateOf(currencies[1])
    val firstCurrencyAmount: MutableState<Amount> = mutableStateOf(null)
    val secondCurrencyAmount: MutableState<Amount> = mutableStateOf(null)

    val chartData: MutableState<List<ChartData>?> = mutableStateOf(null)

    val isConverting = mutableStateOf(false)
    val isChartLoading = mutableStateOf(ChartLoading.Empty)

    var error: ((String) -> Unit)? = null

    val exchangeDate: MutableState<Date?> = mutableStateOf(null)

    fun getFlag(currency: CurrencyCode) = flag(currency)

    fun convert(scope: CoroutineScope) = scope.launch {
        isConverting.value = true
        isChartLoading.value = ChartLoading.Loading

        val conversion = repository.convert(
            from = firstCurrencyCode.value,
            to = secondCurrencyCode.value,
            amount = firstCurrencyAmount.value,
            context = context,
        )
        launch {
            conversion.collect { result ->
                when (result) {
                    is ResultWrapper.Success -> secondCurrencyAmount.value = result.data
                    is ResultWrapper.Failure -> error?.invoke(result.error.localizedMessage)
                    is ResultWrapper.Loading -> {}
                }
            }
        }

        val date = repository.getRateDate(context)
        launch {
            date.collect { result ->
                if (result is ResultWrapper.Success)
                    exchangeDate.value = result.data
            }
        }

        val genChartData = repository.generateChartData(
            from = firstCurrencyCode.value,
            to = secondCurrencyCode.value,
            amount = firstCurrencyAmount.value,
            context = context,
        )
        launch {
            genChartData.collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        chartData.value = result.data
                        isChartLoading.value = ChartLoading.Success
                    }
                    is ResultWrapper.Failure -> {
                        isChartLoading.value = ChartLoading.Empty
                    }
                    is ResultWrapper.Loading -> {
                        isChartLoading.value = ChartLoading.Empty
                    }
                }
                isConverting.value = false
            }
        }
    }
}


class CurrencyCalculatorViewModel(application: Application) : AndroidViewModel(application) {
    // transforms a locales country to the emoji of its flag
    private val Locale.flag: CurrencyFlag
        get() {
            val firstLetter = Character.codePointAt(country, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(country, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }

    private val currencies = buildMap<CurrencyCode, CurrencyFlag> {
        Locale.getAvailableLocales().forEach { locale ->
            try {
                put(Currency.getInstance(locale).currencyCode, locale.flag)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Build currencies: ${e.localizedMessage}")
            }
        }
    }

    val uiState = CurrencyCalculatorUiState(
        currencies = currencies.keys.sorted().toList(),
        repository = RatesRepositoryImpl(
            RatesStoreImpl(), RatesApiImpl
        ),
        context = application.baseContext,
    ) { currencies[it]!! }

    companion object {
        val TAG = CurrencyCalculatorViewModel::class.simpleName
    }
}