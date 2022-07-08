package com.wojbeg.jetmlimageclassifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.wojbeg.jetmlimageclassifier.JetMLResults.ResultsScreen
import com.wojbeg.jetmlimageclassifier.ui.ImageUploaderScreen
import com.wojbeg.jetmlimageclassifier.ui.theme.JetMLImageClassifierTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetMLImageClassifierTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "image_uploader") {

                    composable("image_uploader") {
                        ImageUploaderScreen(navController = navController)
                    }

                    composable("jetml_results/{dominantColor}",

                    ) {
                        ResultsScreen(
                            navController = navController,
                        )
                    }

                }
            }
        }
    }
}

