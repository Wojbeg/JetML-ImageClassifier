package com.wojbeg.jetmlimageclassifier.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wojbeg.jetmlimageclassifier.ImageUploader.ImageUploaderViewModel
import com.wojbeg.jetmlimageclassifier.R
import com.wojbeg.jetmlimageclassifier.data.ImagePickEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageUploaderScreen(
    navController: NavController,
    viewModel: ImageUploaderViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val rememberCoroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val imageModifier = Modifier
        .height(300.dp)
        .fillMaxWidth()
        .clickable {
            rememberCoroutineScope.launch {
                if (!bottomSheetState.isVisible) {
                    bottomSheetState.show()
                } else {
                    bottomSheetState.hide()
                }
            }
        }

    ModalBottomSheetLayout(
        sheetContent = {
            ModalBottomSheetContent(context, rememberCoroutineScope, bottomSheetState)
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        modifier = Modifier
            .background(MaterialTheme.colors.background)
    ) {

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.upload_photo),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier
                    .height(10.dp))

                //Loading image from gallery Uri to bitmap
                viewModel.imageURI?.let {
                        viewModel.bitmapHolder.imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                }

                //Creating image from bitmap if is not null
                viewModel.bitmapHolder.imageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.image_to_classify),
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                } ?: apply {
                    //if bitmap is null we create image with placeholder

                    Image(
                        painterResource(R.drawable.ic_image_placeholder),
                        contentDescription = stringResource(R.string.image_to_classify),
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                }

                Spacer(modifier = Modifier
                    .height(16.dp))

                Button(onClick = {
                    rememberCoroutineScope.launch {
                        if(!bottomSheetState.isVisible) {
                            bottomSheetState.show()
                        } else {
                            bottomSheetState.hide()
                        }
                    }
                },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)

                ) {

                    Text(
                        text = stringResource(R.string.take_picture),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier
                    .height(16.dp))

                Button(onClick = {
                    if (viewModel.bitmapHolder.imageBitmap != null){

                        navController.navigate(
                            "jetml_results/${Color.Red.toArgb()}"
                        )
                    }
                },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)

                ) {

                    Text(
                        text = stringResource(R.string.classify_image),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheetContent(
    context: Context,
    rememberCoroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    viewModel: ImageUploaderViewModel = hiltViewModel()
){

    //launcher picking image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()){ uri: Uri? ->
        viewModel.imagePick = ImagePickEnum.GALLERY
        viewModel.imageURI = uri
    }

    //launcher for picking image from camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        viewModel.imagePick = ImagePickEnum.CAMERA
        //resetting state of image from gallery even if it does not exist
        viewModel.imageURI = null
        viewModel.bitmapHolder.imageBitmap = bitmap
    }

    val toastText = stringResource(R.string.permission_denied)

    //launcher for runtime permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { isPermissionGranted ->

        if(isPermissionGranted) {
            when (viewModel.imagePick) {
                ImagePickEnum.CAMERA -> {
                    cameraLauncher.launch()
                }
                ImagePickEnum.GALLERY -> {
                    galleryLauncher.launch("image/*")
                }
            }
            rememberCoroutineScope.launch {
                bottomSheetState.hide()
            }
        } else {

            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.add_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                color = MaterialTheme.colors.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .background(MaterialTheme.colors.primary)
            )

            Text(
                text = stringResource(R.string.take_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) -> {
                                cameraLauncher.launch()
                                rememberCoroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }

                    }

                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Divider(
                modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            Text(
                text = stringResource(R.string.choose_file_gallery),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (PackageManager.PERMISSION_GRANTED) {

                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.READ_EXTERNAL_STORAGE
                            ) -> {
                                galleryLauncher.launch("image/*")
                                rememberCoroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        }
                    }
                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )

            Divider(
                modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            Text(
                text = stringResource(R.string.cancel),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        rememberCoroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )

        }
    }

}
