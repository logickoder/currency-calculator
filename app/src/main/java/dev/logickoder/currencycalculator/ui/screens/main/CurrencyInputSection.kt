package dev.logickoder.currencycalculator.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.logickoder.currencycalculator.R
import dev.logickoder.currencycalculator.ui.components.CurrencyConversionContainer
import dev.logickoder.currencycalculator.ui.components.CurrencyInputField
import androidx.compose.material.MaterialTheme as Theme

@Composable
internal fun CurrencyInputSection(
    currencies: List<CurrencyCode>,
    firstCurrencyAmount: MutableState<Amount>,
    secondCurrencyAmount: MutableState<Amount>,
    firstCurrencyCode: MutableState<CurrencyCode>,
    secondCurrencyCode: MutableState<CurrencyCode>,
    isConverting: Boolean,
    date: Date,
    getFlag: (CurrencyCode) -> CurrencyFlag,
    convert: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    Theme.typography.h4.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Black,
                    ).toSpanStyle()
                ) {
                    append(
                        stringResource(id = R.string.app_name).split(Regex("\\s+"))
                            .joinToString("\n")
                    )
                }
                withStyle(
                    Theme.typography.h4.copy(
                        color = Theme.colors.secondary,
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
            currencyCode = firstCurrencyCode.value,
            amount = firstCurrencyAmount.value,
            editable = true,
            onAmountChange = { firstCurrencyAmount.value = it },
            onGo = { convert.invoke() },
        )
        Spacer(modifier = Modifier.height(16.dp))
        CurrencyInputField(
            modifier = Modifier.fillMaxWidth(),
            currencyCode = secondCurrencyCode.value,
            amount = secondCurrencyAmount.value,
            onAmountChange = { secondCurrencyAmount.value = it }
        )
        Spacer(modifier = Modifier.height(32.dp))
        CurrencyConversionContainer(
            modifier = Modifier.fillMaxWidth(),
            currencies = currencies,
            firstCurrency = Triple(firstCurrencyCode.value, getFlag(firstCurrencyCode.value)) {
                firstCurrencyCode.value = it
            },
            secondCurrency = Triple(secondCurrencyCode.value, getFlag(secondCurrencyCode.value)) {
                secondCurrencyCode.value = it
            },
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { convert.invoke() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Theme.colors.secondary,
                disabledBackgroundColor = Theme.colors.secondary,
                contentColor = Color.White,
                disabledContentColor = Color.White,
            ),
            shape = Theme.shapes.medium,
            enabled = !isConverting
        ) {
            if (isConverting) CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 5.dp
            ) else Text(text = stringResource(id = R.string.convert), style = Theme.typography.h6)
        }
        if (date.isNotBlank()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.exchange_rate_date, date),
                color = Theme.colors.primary,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}