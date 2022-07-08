package com.wojbeg.jetmlimageclassifier.JetMLResults

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wojbeg.jetmlimageclassifier.R
import com.wojbeg.jetmlimageclassifier.ui.PieChart
import com.wojbeg.jetmlimageclassifier.ui.PieChartTypes
import com.wojbeg.jetmlimageclassifier.ui.theme.LightBlue
import com.wojbeg.jetmlimageclassifier.ui.theme.darkGrey
import com.wojbeg.jetmlimageclassifier.utils.Constants.PIE_COLORS
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ResultsScreen(
    navController: NavController,
    topPadding: Dp = 50.dp,
    imageSize: Dp = 200.dp,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    val resultsInfo = viewModel.resultList

    Box(Modifier.fillMaxSize()) {

        Column( horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(viewModel.dominantColor)
                .verticalScroll(scrollState)
        ) {

            Box {

                ResultsSectionWrapper(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = topPadding + imageSize / 2f,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colors.surface)
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    resultsInfo= resultsInfo,
                    loadingModifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .padding(
                            top = topPadding + imageSize / 2f,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )

                Box(contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    viewModel.bitmapHolder.imageBitmap?.let { bitmap ->

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.image_to_classify),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(imageSize)
                                .offset(y = topPadding)
                        )
                    }
                }

            }

        }
        ResultsTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.1f)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
fun ResultsTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isSystemInDarkTheme()) {
                            darkGrey
                        } else LightBlue,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.arrow_back),
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun ResultsSectionWrapper(
    resultsInfo:  List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 80.dp)
            .padding(bottom = 80.dp)
    ) {

        if (resultsInfo.isEmpty()) {

            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )

            Spacer(modifier = Modifier.height(64.dp))
        } else {

            Text(
                text = resultsInfo[0].first.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            PieChart(modifier = Modifier.size(100.dp),
                showPercent = true,
                colours = PIE_COLORS,
                values = resultsInfo.map { pair -> pair.second },
                legend = resultsInfo.map { pair -> pair.first },
                showLegend = false,
                types = PieChartTypes.DONUT)

            Spacer(modifier = Modifier.height(16.dp))

            ClassifyStats(
                resultsInfo = resultsInfo,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PercentageBar(
    name: String,
    value: Float,
    percentColor: Color,
    sum: Float,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0,
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val currentPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            value / sum
        } else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(height),
        contentAlignment = Alignment.Center
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            ),
        ) {

            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currentPercent.value)
                .clip(CircleShape)
                .background(percentColor)
                .padding(horizontal = 8.dp))
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${(currentPercent.value * 10000.0).roundToInt() / 100.0}%",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = MaterialTheme.colors.onSurface
            )
        }

        Text(
            text = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun ClassifyStats(
    resultsInfo:  List<Pair<String, Float>>,
    animDelayPerItem: Int = 100
) {
    val sum = remember {
        resultsInfo.fold(0.0f){ acc, pair -> acc + pair.second }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.classifier_status),
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        for (i in resultsInfo.indices) {
            val info = resultsInfo[i]
            PercentageBar(
                name = info.first,
                value = info.second,
                sum = sum,
                percentColor = PIE_COLORS[i],
                animDelay = i * animDelayPerItem
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
