package dev.logickoder.currencycalculator.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.logickoder.currencycalculator.R
import dev.logickoder.currencycalculator.data.api.DAYS_CAP
import dev.logickoder.currencycalculator.ui.components.Graph
import androidx.compose.material.MaterialTheme as Theme

@Composable
internal fun GraphSection(
    data: List<ChartData>?,
    currency: CurrencyCode,
    amount: Amount,
    isChartLoading: ChartLoading,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(0) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .background(
                Brush.verticalGradient(listOf(Theme.colors.primary, Color(0xFF0175FE)))
            )
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (isChartLoading) {
            ChartLoading.Success -> {
                GraphSectionTitle(selected) { selected = it }
                Spacer(modifier = Modifier.height(72.dp))
                Graph(
                    data = data!!.subList(
                        data.size - if (selected == 0) 30 else DAYS_CAP,
                        data.size
                    ),
                    currency = currency,
                    amount = amount,
                )
            }
            ChartLoading.Loading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 10.dp
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun GraphSectionTitle(selected: Int, onSelected: (Int) -> Unit) {

    @Composable
    fun TitleText(text: String, selected: Boolean, onSelected: () -> Unit) {
        Column(
            modifier = Modifier.clickable { onSelected() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Theme.colors.primaryVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (selected) Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Theme.colors.secondary)
                    .size(12.dp)
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TitleText(
            text = stringResource(id = R.string.past_30_days),
            selected = selected == 0,
            onSelected = { onSelected(0) }
        )
        TitleText(
            text = stringResource(id = R.string.past_90_days),
            selected = selected == 1,
            onSelected = { onSelected(1) }
        )
    }
}