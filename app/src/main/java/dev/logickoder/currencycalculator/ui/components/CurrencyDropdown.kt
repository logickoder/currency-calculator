package dev.logickoder.currencycalculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.logickoder.currencycalculator.R
import dev.logickoder.currencycalculator.ui.CurrencyCode
import dev.logickoder.currencycalculator.ui.CurrencyFlag
import dev.logickoder.currencycalculator.ui.theme.ConversionIconColor
import androidx.compose.material.MaterialTheme as Theme

/**
 * A dropdown field for selecting currencies
 *
 * @param currencyCode the currently selected currency
 * @param countryFlag the flag of the country the currency is used in
 * @param currencies the list of selectable currencies
 * @param onCurrencySelected lambda to be executed when the user selects a currency
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CurrencyDropdownField(
    currencyCode: CurrencyCode,
    countryFlag: CurrencyFlag,
    currencies: List<CurrencyCode>,
    modifier: Modifier = Modifier,
    onCurrencySelected: ((CurrencyCode) -> Unit),
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currencyCode,
            onValueChange = {},
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium),
            leadingIcon = {
                Text(
                    modifier = Modifier.clip(CircleShape),
                    text = countryFlag,
                )
            },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                textColor = ConversionIconColor,
                trailingIconColor = ConversionIconColor,
                focusedBorderColor = Theme.colors.surface,
                unfocusedBorderColor = Theme.colors.surface,
            ),
            shape = Theme.shapes.medium,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencies.forEach { currencyCode ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onCurrencySelected(currencyCode)
                    }
                ) {
                    Text(text = currencyCode, color = ConversionIconColor)
                }
            }
        }
    }
}

/**
 * A container with two currency drop down fields
 *
 * @param currencies the list of selectable currencies
 * @param firstCurrency a triple containing the currency, flag, and callback
 * @param secondCurrency a triple containing the currency, flag, and callback
 */
@Composable
fun CurrencyConversionContainer(
    modifier: Modifier = Modifier,
    currencies: List<CurrencyCode>,
    firstCurrency: Triple<CurrencyCode, CurrencyFlag, (CurrencyCode) -> Unit>,
    secondCurrency: Triple<CurrencyCode, CurrencyFlag, (CurrencyCode) -> Unit>,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        with(firstCurrency) {
            CurrencyDropdownField(
                modifier = Modifier.weight(1f),
                currencyCode = first,
                countryFlag = second,
                currencies = currencies,
                onCurrencySelected = third
            )
        }
        Icon(
            modifier = Modifier.padding(horizontal = 8.dp),
            painter = painterResource(id = R.drawable.ic_swap),
            contentDescription = null,
            tint = ConversionIconColor,
        )
        with(secondCurrency) {
            CurrencyDropdownField(
                modifier = Modifier.weight(1f),
                currencyCode = first,
                countryFlag = second,
                currencies = currencies,
                onCurrencySelected = third
            )
        }
    }
}