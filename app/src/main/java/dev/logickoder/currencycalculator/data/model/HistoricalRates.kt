package dev.logickoder.currencycalculator.data.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import dev.logickoder.currencycalculator.data.Identifiable
import dev.logickoder.currencycalculator.ui.screens.main.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @property base the base currency all rates are in
 * @property rates the amount of each currency at a specified day that reflects the [base]
 * @property startDate the start date of the history
 * @property endDate the end date of the
 * @property success true if the endpoint call was successful
 * @property timeseries true if the endpoint call is a timeseries
 */
data class HistoricalRates(
    @SerializedName("base")
    val base: String, // NGN
    @SerializedName("rates")
    val rates: JsonObject,
    @SerializedName("start_date")
    val startDate: String, // 2021-12-31
    @SerializedName("end_date")
    val endDate: String, // 2022-01-05
    @SerializedName("success")
    val success: Boolean, // true
    @SerializedName("timeseries")
    val timeseries: Boolean, // true

    val date: Date = LocalDateTime.now()
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
) : Identifiable {

    override val id: Long = ID

    companion object {
        const val ID = 0L
    }
}