package dev.logickoder.currencycalculator.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.logickoder.currencycalculator.ui.screens.main.Amount
import dev.logickoder.currencycalculator.ui.screens.main.ChartData
import dev.logickoder.currencycalculator.ui.screens.main.CurrencyCode
import androidx.compose.material.MaterialTheme as Theme

// number of y-ticks
private const val YTicksCount = 7

// number of x-ticks
private const val XTicksCount = 10

// distance between each y-tick
private val Height = 30.dp

@Composable
fun Graph(
    data: List<ChartData>,
    currency: CurrencyCode,
    amount: Amount,
    modifier: Modifier = Modifier,
) {
    val min = data.minOf { it.second!! }
    val max = data.maxOf { it.second!! }

    val color = Theme.colors.primaryVariant
    val colorLight = Theme.colors.primaryVariant.copy(alpha = 0.3f)
    val colorSecondary = Theme.colors.secondary

    var popupPos: Int? by remember { mutableStateOf(null) }
    val popupFontSize = Theme.typography.h6.fontSize.value

    BoxWithConstraints(modifier = modifier) {
        // distance between each x-tick
        val xWidth = maxWidth / (XTicksCount - 1)
        Column(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { tapOffset ->
                                // when the user clicks on the canvas, trace the corresponding
                                // value of x and show a bubble containing the date and conversion
                                val singleX = size.width.toFloat() / data.size
                                data.forEachIndexed { i, _ ->
                                    val x = (i + 1) * singleX
                                    val startX = i * singleX

                                    if (tapOffset.x in startX..x) {
                                        popupPos = i
                                        return@forEachIndexed
                                    }
                                }
                            }
                        )
                    }
                    .fillMaxWidth()
                    .height(Height * YTicksCount)
            ) {
                // distance between each x-value
                val singleX = size.width / data.size
                // distance between each y-value
                val singleY = size.height / 100
                // calculates the x-distance of the specified chart data
                val xDistance: (Int) -> Float = { index -> (index + 1) * singleX }
                // calculates the y-distance of the specified chart data
                val yDistance: (ChartData) -> Float = { data ->
                    size.height - ((data.second!! - min) / (max - min) * 100 * singleY).toFloat()
                }
                // size of a single tick
                val tick = 4.dp.toPx()

                val path = Path()
                // start at the bottom-left of the canvas
                path.moveTo(0f, size.height)
                // link to the starting point
                path.lineTo(x = 0f, y = yDistance(data[0]))
                // draw the data into a line graph format
                data.forEachIndexed { i, data ->
                    path.lineTo(x = xDistance(i), y = yDistance(data))
                }
                // join the last drawn path to the bottom-right
                path.lineTo(xDistance(data.lastIndex), size.height)
                path.close()
                // paint the graph onto the canvas
                drawPath(path, Brush.verticalGradient(listOf(color, colorLight)))
                // draw the popup when an entry on the graph is selected
                popupPos?.let { dataIndex ->
                    val spacing = 4.dp.toPx()
                    // get the x and y position to place the popup
                    val (x, y) = xDistance(dataIndex) to yDistance(data[dataIndex])
                    val topText = data[dataIndex].first
                    val bottomText =
                        "${"%.2f".format(amount!!)} $currency = ${"%.2f".format(data[dataIndex].second)}"
                    val paint = Paint().apply {
                        isAntiAlias = true
                        textAlign = Paint.Align.LEFT
                        textSize = popupFontSize
                        this.color = android.graphics.Color.WHITE
                    }
                    val longestText =
                        if (topText.length > bottomText.length) topText else bottomText
                    // defines the width of the box to be the size of the longest text with padding
                    val width =
                        paint.measureText(longestText, 0, longestText.length) + (spacing * 2)
                    // defines the height of the box to be the combined heights of the text with padding
                    val height = spacing * 3 + popupFontSize * 2
                    // draw the circle showing the highlighted data
                    drawCircle(Color.White, 12f, Offset(x, y))
                    drawCircle(colorSecondary, 10f, Offset(x, y))
                    // if the box exceeds its container, the box should be translated
                    val shouldTranslate = x + width >= size.width
                    translate(
                        left = if (shouldTranslate) -(width + spacing * 5) else 0f,
                        top = if (shouldTranslate) -spacing else 0f,
                    ) {
                        // draw the rectangle to contain the data
                        drawRoundRect(
                            colorSecondary,
                            Offset(x + (spacing * 2), y - (spacing * 2) - height),
                            Size(width, height),
                            CornerRadius(10f, 10f)
                        )
                        drawIntoCanvas {
                            with(it.nativeCanvas) {
                                drawText(
                                    topText,
                                    x + spacing * 2 + spacing, y - height + spacing,
                                    paint,
                                )
                                drawText(
                                    bottomText,
                                    x + spacing * 2 + spacing,
                                    y - height + (spacing * 2) + popupFontSize,
                                    paint,
                                )
                            }
                        }
                    }
                }
                // draw the ticks on the y-axis
                (0 until YTicksCount).forEach {
                    val y = Height.toPx() * it
                    drawLine(color, Offset(-tick, y), Offset(0f, y), strokeWidth = 4f)
                }
                // draw the ticks on the x-axis
                (0 until XTicksCount).forEach {
                    val x = xWidth.toPx() * it
                    drawLine(
                        color,
                        Offset(x, size.height - tick),
                        Offset(x, size.height),
                        strokeWidth = 4f
                    )
                }
            }
            Dates(
                // generates dates 25% apart
                dates = (0..100 step (100 / (XTicksCount / 2 - 1))).map {
                    var dataPos = it.toFloat() / 100 * data.size

                    if (dataPos < 0)
                        dataPos = 0f
                    else if (dataPos > data.lastIndex)
                        dataPos = data.lastIndex.toFloat()

                    data[dataPos.toInt()].first
                },
                dateSize = xWidth,
            )
        }
    }
}

@Composable
private fun Dates(dates: List<String>, dateSize: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        dates.forEach { date ->
            Text(
                modifier = Modifier.widthIn(max = dateSize),
                text = date,
                style = Theme.typography.overline,
                color = Theme.colors.primaryVariant.copy(alpha = 0.8f),
            )
        }
    }
}