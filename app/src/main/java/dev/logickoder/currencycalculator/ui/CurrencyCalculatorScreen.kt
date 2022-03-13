package dev.logickoder.currencycalculator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.currencycalculator.ui.components.CurrencyInputSection
import dev.logickoder.currencycalculator.ui.components.GraphSection

@Composable
fun CurrencyCalculatorScreen(viewModel: CurrencyCalculatorViewModel = viewModel()) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        CurrencyInputSection(viewModel = viewModel)
        Spacer(modifier = Modifier.height(32.dp))
        GraphSection(viewModel = viewModel)
    }
}