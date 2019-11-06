package com.mazenrashed.txttoimg

import android.graphics.Color

class Line {
    var rightText: TextInLine? = null
    var leftText: TextInLine? = null
    var centerText: TextInLine? = null

}

class TextInLine {
    var text : String = ""
    var size : Float = 12f
    var color : Int = Color.BLACK

}

