package com.wojbeg.jetmlimageclassifier.ImageUploader

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.wojbeg.jetmlimageclassifier.data.BitmapHolder
import com.wojbeg.jetmlimageclassifier.data.ImagePickEnum

@HiltViewModel
class ImageUploaderViewModel @Inject constructor(
    val bitmapHolder: BitmapHolder
) : ViewModel() {

    var imagePick by mutableStateOf<ImagePickEnum?>(null)
    var imageURI by mutableStateOf<Uri?>(null)

}
