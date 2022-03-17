package dev.logickoder.currencycalculator.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun CurrencyCalculatorScreen(
    viewModel: CurrencyCalculatorViewModel = viewModel()
) = with(viewModel.uiState) {

    val scroll = rememberScrollState()
    val scaffold = rememberScaffoldState()
    val coroutine = rememberCoroutineScope()

    error = { coroutine.launch { scaffold.snackbarHostState.showSnackbar(it) } }

    Scaffold(scaffoldState = scaffold) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            CurrencyInputSection(
                currencies = currencies,
                firstCurrencyAmount = firstCurrencyAmount,
                secondCurrencyAmount = secondCurrencyAmount,
                firstCurrencyCode = firstCurrencyCode,
                secondCurrencyCode = secondCurrencyCode,
                isConverting = isConverting.value,
                date = exchangeDate.value ?: "",
                getFlag = { currencyCode -> getFlag(currencyCode) },
                convert = { convert(coroutine) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            GraphSection(
                data = chartData.value,
                currency = firstCurrencyCode.value,
                amount = firstCurrencyAmount.value,
                isChartLoading = isChartLoading.value,
            )
        }
    }
}