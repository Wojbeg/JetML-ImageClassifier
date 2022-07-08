package com.wojbeg.jetmlimageclassifier.JetMLResults

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wojbeg.jetmlimageclassifier.data.BitmapHolder
import com.wojbeg.jetmlimageclassifier.ml.MobilenetV110224Quant
import com.wojbeg.jetmlimageclassifier.utils.Constants.IMAGE_HEIGHT
import com.wojbeg.jetmlimageclassifier.utils.Constants.IMAGE_WIDTH
import com.wojbeg.jetmlimageclassifier.utils.Constants.MODEL_TEXT
import com.wojbeg.jetmlimageclassifier.utils.Constants.TOP_VALUES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ResultsViewModel @Inject constructor(
    val bitmapHolder: BitmapHolder,
    val model: MobilenetV110224Quant,
    @Named(MODEL_TEXT) val modelText: List<String>
) : ViewModel() {

    var dominantColor by mutableStateOf<Color>(Color.White)
    var resultList by mutableStateOf<List<Pair<String, Float>>>(mutableListOf())

    init {
        viewModelScope.launch(Dispatchers.Default) {
            bitmapHolder.calculateColorFromBitmap {
                dominantColor = it
            }
        }

        classifyImage()
    }

    private fun classifyImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmapCopy = bitmapHolder.imageBitmap!!.copy(Bitmap.Config.ARGB_8888, true)

            val resizedBitmap = Bitmap.createScaledBitmap(bitmapCopy, IMAGE_WIDTH, IMAGE_HEIGHT, true)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, IMAGE_WIDTH, IMAGE_HEIGHT, 3), DataType.UINT8)

            inputFeature0.loadBuffer(TensorImage.fromBitmap(resizedBitmap).buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var test = outputFeature0.floatArray.mapIndexed { index, fl -> index to fl  }
            test = test.sortedBy { pair: Pair<Int, Float> ->
                pair.second
            }.reversed().takeWhile { pair: Pair<Int, Float> ->
                pair.second > 0f
            }.take(TOP_VALUES)

            resultList =
                test.map{ pair ->
                    modelText[pair.first] to pair.second
                }

            test.forEach { pair: Pair<Int, Float> ->
                println("${modelText[pair.first]} - ${pair.second}")
            }
            model.close()
        }
    }
}