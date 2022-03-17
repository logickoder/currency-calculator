package dev.logickoder.currencycalculator.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dev.logickoder.currencycalculator.ui.screens.main.Amount
import dev.logickoder.currencycalculator.ui.screens.main.CurrencyCode

/**
 * Displays an input field for entering conversion amount with the currency at the end of it
 *
 * @param currencyCode the currency to display at the end of this input field
 * @param amount the amount to show in this input field, if null, no value will be shown
 * @param onAmountChange the lambda that gets called when the value in the input field changes
 * @param editable true if the contents of the field can be changed
 */
@Composable
fun CurrencyInputField(
    currencyCode: CurrencyCode,
    modifier: Modifier = Modifier,
    amount: Amount = null,
    editable: Boolean = false,
    onAmountChange: (Amount) -> Unit,
    onGo: (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = amount?.toString() ?: "",
        onValueChange = { text -> onAmountChange(text.toDoubleOrNull()) },
        readOnly = !editable,
        singleLine = true,
        trailingIcon = { Text(currencyCode, fontWeight = FontWeight.Medium) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Go,
        ),
        keyboardActions = KeyboardActions(onGo = { onGo?.invoke() }),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            trailingIconColor = MaterialTheme.colors.onSurface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.medium,
    )
}