package com.wojbeg.jetmlimageclassifier.data

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

class BitmapHolder() {

    /*
    This class is my solution for sharing image when passing between screens.
    It wouldn't be a problem if the photo was only selected from the gallery
    as we could send a URI and download it again.
    However, the matter is more complicated with camera photos:

    ->Bitmap can not be passed easily between fragments using arguments.
        It has to be converted to byte array, but it's not so effective.
        Bundle can hold max 1MB of data, some pictures probably will be bigger than that.
        During compression quality will be worse.

    ->Another solution could be to save image temporary in app private folder
        and delete it afterward. (Very good idea)

    ->Next idea could rely on creating shared viewModel between ImageUploader Screen
        and Results Screen. Then it would not be necessary to pass any arguments.
        (I think this might be best solution)

    ->This is another idea. This class can work as some kind of shared repository/wrapper
        specifically for bitmap. We don't have to worry about passing argument, image compression,
        asking user for file permissions to save files and others.
        (That's why i think it might be worth to at least try it)
    */

    var imageBitmap by mutableStateOf<Bitmap?>(null)

    fun calculateColorFromBitmap(onFinish: (Color) -> Unit) {

        imageBitmap?.let { bitmap ->
            val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            Palette.from(bitmapCopy).generate { palette ->
                println(palette==null)
                println(palette)
                println(palette?.vibrantSwatch)
                println(palette?.darkMutedSwatch)
                println(palette?.dominantSwatch)
                println(palette?.swatches)

                palette?.vibrantSwatch?.rgb?.let { colorValue ->
                    onFinish(Color(colorValue))
                } ?: palette?.dominantSwatch?.rgb?.let { colorValue ->
                    onFinish(Color(colorValue))
                }
            }
        } ?: onFinish(Color.White)
    }

}