package dev.logickoder.currencycalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.logickoder.currencycalculator.R
import dev.logickoder.currencycalculator.ui.CurrencyCalculatorViewModel

@Composable
internal fun CurrencyInputSection(viewModel: CurrencyCalculatorViewModel) = with(viewModel) {
    val sortedCurrencies = currencies.keys.sorted()
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Black,
                    ).toSpanStyle()
                ) {
                    append(
                        stringResource(id = R.string.app_name).split(Regex("\\s+"))
                            .joinToString("\n")
                    )
                }
                withStyle(
                    MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.Black,
                    ).toSpanStyle()
                ) {
                    append(".")
                }
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
        CurrencyInputField(
            modifier = Modifier.fillMaxWidth(),
            currencyCode = firstCurrency.value,
            amount = firstCurrencyAmount.value
        ) {
            firstCurrencyAmount.value = it
        }
        Spacer(modifier = Modifier.height(16.dp))
        CurrencyInputField(
            modifier = Modifier.fillMaxWidth(),
            currencyCode = secondCurrency.value,
            amount = secondCurrencyAmount.value
        ) {
            secondCurrencyAmount.value = it
        }
        Spacer(modifier = Modifier.height(32.dp))
        CurrencyConversionContainer(
            modifier = Modifier.fillMaxWidth(),
            currencies = sortedCurrencies,
            firstCurrency = Triple(firstCurrency.value, currencies[firstCurrency.value]!!) {
                firstCurrency.value = it
            },
            secondCurrency = Triple(secondCurrency.value, currencies[secondCurrency.value]!!) {
                secondCurrency.value = it
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = ::convert,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(text = stringResource(id = R.string.convert), style = MaterialTheme.typography.h6)
        }
    }
}