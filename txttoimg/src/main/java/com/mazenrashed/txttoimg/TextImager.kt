package com.mazenrashed.txttoimg

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import io.reactivex.Single

class TextImager(context: Context, resources: Resources) {

    val imageHelper = ImageHelper(resources, context)

//    fun textToImage(text: String, aligment: ImageHelper.Aligment) : Single<Bitmap>{
//        return imageHelper.textToBitmap(text, aligment)
//    }
}