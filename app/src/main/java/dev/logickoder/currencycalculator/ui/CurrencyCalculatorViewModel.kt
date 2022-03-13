package dev.logickoder.currencycalculator.ui

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.*

typealias CurrencyCode = String
typealias CurrencyFlag = String
typealias Amount = Double?

/**
 *@property currencies contains a mapping of currencies to their flags
 */
class CurrencyCalculatorViewModel : ViewModel() {
    // transforms a locales country to the emoji of its flag
    private val Locale.flag: CurrencyFlag
        get() {
            val firstLetter = Character.codePointAt(country, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(country, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }

    val currencies = buildMap<CurrencyCode, CurrencyFlag> {
        Locale.getAvailableLocales().forEach { locale ->
            try {
                put(Currency.getInstance(locale).currencyCode, locale.flag)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Build currencies: ${e.localizedMessage}")
            }
        }
    }

    val firstCurrency: MutableState<CurrencyCode>
    val firstCurrencyAmount: MutableState<Amount>
    val secondCurrency: MutableState<CurrencyCode>
    val secondCurrencyAmount: MutableState<Amount>

    init {
        val currencies = currencies.keys.toList()
        firstCurrency = mutableStateOf(currencies[0])
        firstCurrencyAmount = mutableStateOf(null)
        secondCurrency = mutableStateOf(currencies[1])
        secondCurrencyAmount = mutableStateOf(null)
    }

    fun convert() {

    }

    companion object {
        val TAG = CurrencyCalculatorViewModel::class.simpleName
    }
}