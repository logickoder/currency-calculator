package dev.logickoder.currencycalculator.data.api

import dev.logickoder.currencycalculator.data.model.HistoricalRates
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {
    @GET("timeseries")
    suspend fun retrieveRates(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): HistoricalRates

    companion object {
        const val BASE_URL = "https://api.exchangerate.host/"
    }
}