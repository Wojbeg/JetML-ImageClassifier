package com.wojbeg.jetmlimageclassifier.di

import android.content.Context
import com.wojbeg.jetmlimageclassifier.data.BitmapHolder
import com.wojbeg.jetmlimageclassifier.ml.MobilenetV110224Quant
import com.wojbeg.jetmlimageclassifier.utils.Constants.FILENAME
import com.wojbeg.jetmlimageclassifier.utils.Constants.MODEL_TEXT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBitmapHolder() = BitmapHolder()

    @Provides
    fun provideMLModel(
        @ApplicationContext context: Context
    ) = MobilenetV110224Quant.newInstance(context)

    /*
    It doesn't necessairly need to be named, but if app grows
    it can inject many list of strings, so for common variables
    i think that is a good practice
    */
    @Provides
    @Singleton
    @Named(MODEL_TEXT)
    fun provideLabels(
        @ApplicationContext context: Context
    ): List<String> =
        context.assets.open(FILENAME).bufferedReader().use { it.readText() }.split("\n")

}