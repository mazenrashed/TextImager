package com.mazenrashed.txttoimg

import android.graphics.Bitmap

/**
 * Created by mazen on 12/30/17.
 */

data class TextLine (
    var height: Int = 0,
    var width: Int = 0,
    var bitmap: Bitmap? = null,
    var text: String? = null
)