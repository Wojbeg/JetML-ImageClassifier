package com.wojbeg.jetmlimageclassifier.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PieChartTypes{ PIE, DONUT }

/*
This small chart library is not ready yet to be used.
In some conditions legend is not showing properly and
it has to be fixed.
*/

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    values: List<Float> = listOf(80f, 35f, 26f, 40f, 33f),
    colours: List<Color> = listOf(
        Color(0xFF003f5c),
        Color(0xFF58508d),
        Color(0xFFbc5090),
        Color(0xFFff6361),
        Color(0xFFffa600),
    ),
    showLegend: Boolean = false,
    legend: List<String> = listOf(
        "Blue",
        "Violet",
        "Purple",
        "Orange",
        "Yellow"
    ),
    types: PieChartTypes = PieChartTypes.PIE,
    thickness: Dp = 20.dp,
    showPercent: Boolean = false,
    defaultStartAngle: Float = -90f,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {

    val sum = remember {
        values.sum()
    }

    val percentage = remember {
        values.map {
            it * 100 / sum
        }
    }

    val angles = remember {
        percentage.map {
            it * 3.6f
        }
    }

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curAngle = animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Canvas(modifier = modifier
        .fillMaxSize()) {
        var startAngle = defaultStartAngle

        angles.forEachIndexed { index, value ->
            val untilAnglesSum = angles.subList(0, index).sum()

            drawArc(
                color = colours[index],
                startAngle = startAngle,
                sweepAngle = if (curAngle.value >= untilAnglesSum) curAngle.value - untilAnglesSum else 0f,

                useCenter = when(types) {
                    PieChartTypes.PIE -> true
                    PieChartTypes.DONUT -> false
                },
                style = when(types) {
                    PieChartTypes.PIE -> Fill
                    PieChartTypes.DONUT -> Stroke(
                        thickness.toPx(),
                        cap = StrokeCap.Butt
                    )
                }
            )
            startAngle += value
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (showLegend){
        Legend(percentage = percentage, legend = legend, colours = colours, showPercent = showPercent)
    }
}

@Composable
fun Legend(
    modifier: Modifier = Modifier,
    percentage: List<Float>,
    legend: List<String>,
    colours: List<Color>,
    showPercent: Boolean = false,
){

    Column(modifier = modifier) {
        percentage.forEachIndexed { index, value ->
            Row(
                modifier = Modifier.fillMaxWidth(0.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LegendItem(
                    color = colours[index],
                    text = legend[index],
                )

                if (showPercent){

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${value}%",
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    color: Color,
    textColor: Color = Color.Black,
    text: String,
    colorBoxSize: Dp = 10.dp,
    colorBoxType: Shape = RectangleShape,
    spaceBetweenItems: Dp = 6.dp,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Box(modifier = Modifier
            .size(colorBoxSize)
            .background(
                color = color,
                shape = colorBoxType
            )
        )

        Spacer(modifier = Modifier.width(spaceBetweenItems))

        Text(
            text = text,
            color = textColor,
            modifier = textModifier
        )
    }
}
