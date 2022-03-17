package dev.logickoder.currencycalculator.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import dev.logickoder.currencycalculator.data.model.HistoricalRates
import dev.logickoder.currencycalculator.di.ratesStore
import dev.logickoder.currencycalculator.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val RATES = stringPreferencesKey("historical_rates")

/**
 * Stores the historical rate on the device
 */
class RatesStoreImpl : DataStoreManager<HistoricalRates> {
    override suspend fun save(data: HistoricalRates, context: Context) {
        context.ratesStore.edit { preferences ->
            preferences[RATES] = Gson().toJson(data)
        }
    }

    override fun get(id: Long, context: Context): Flow<ResultWrapper<HistoricalRates>> {
        return context.ratesStore.data.map { preferences ->
            if (preferences[RATES] == null)
                ResultWrapper.Failure("No data available offline\nConnect to the internet and retry")
            else
                ResultWrapper.Success(
                    Gson().fromJson(
                        preferences[RATES],
                        HistoricalRates::class.java
                    )
                )
        }
    }
}