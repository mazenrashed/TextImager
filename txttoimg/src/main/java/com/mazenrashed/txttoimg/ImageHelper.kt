package com.mazenrashed.txttoimg

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import io.reactivex.Single
import kotlin.math.max

class ImageHelper(val resources: Resources, val context: Context) {

    /**
     * Returns a new Bitmap copy with a center-crop effect a la
     * [android.widget.ImageView.ScaleType.CENTER_CROP]. May return the input bitmap if no
     * scaling is necessary.
     *
     * @param src original bitmap of any size
     * @param w   desired width in px
     * @param h   desired height in px
     * @return a copy of src conforming to the given width and height, or src itself if it already
     * matches the given width and height
     */
    fun centerCrop(src: Bitmap, w: Int, h: Int): Bitmap {
        return crop(src, w, h, 0.5f, 0.5f)
    }

    /**
     * Returns a new Bitmap copy with a crop effect depending on the crop anchor given. 0.5f is like
     * [android.widget.ImageView.ScaleType.CENTER_CROP]. The crop anchor will be be nudged
     * so the entire cropped bitmap will fit inside the src. May return the input bitmap if no
     * scaling is necessary.
     *
     *
     *
     *
     * Example of changing verticalCenterPercent:
     * _________            _________
     * |         |          |         |
     * |         |          |_________|
     * |         |          |         |/___0.3f
     * |---------|          |_________|\
     * |         |<---0.5f  |         |
     * |---------|          |         |
     * |         |          |         |
     * |         |          |         |
     * |_________|          |_________|
     *
     * @param src                     original bitmap of any size
     * @param w                       desired width in px
     * @param h                       desired height in px
     * @param horizontalCenterPercent determines which part of the src to crop from. Range from 0
     * .0f to 1.0f. The value determines which part of the src
     * maps to the horizontal center of the resulting bitmap.
     * @param verticalCenterPercent   determines which part of the src to crop from. Range from 0
     * .0f to 1.0f. The value determines which part of the src maps
     * to the vertical center of the resulting bitmap.
     * @return a copy of src conforming to the given width and height, or src itself if it already
     * matches the given width and height
     */
    fun crop(
        src: Bitmap, w: Int, h: Int,
        horizontalCenterPercent: Float, verticalCenterPercent: Float
    ): Bitmap {
        if (horizontalCenterPercent < 0 || horizontalCenterPercent > 1 || verticalCenterPercent < 0
            || verticalCenterPercent > 1
        ) {
            throw IllegalArgumentException(
                "horizontalCenterPercent and verticalCenterPercent must be between 0.0f and " + "1.0f, inclusive."
            ) as Throwable
        }
        val srcWidth = src.width
        val srcHeight = src.height
        // exit early if no resize/crop needed
        if (w == srcWidth && h == srcHeight) {
            return src
        }
        val m = Matrix()
        val scale = Math.max(
            w.toFloat() / srcWidth,
            h.toFloat() / srcHeight
        )
        m.setScale(scale, scale)
        val srcCroppedW: Int
        val srcCroppedH: Int
        var srcX: Int
        var srcY: Int
        srcCroppedW = Math.round(w / scale)
        srcCroppedH = Math.round(h / scale)
        srcX = (srcWidth * horizontalCenterPercent - srcCroppedW / 2).toInt()
        srcY = (srcHeight * verticalCenterPercent - srcCroppedH / 2).toInt()
        // Nudge srcX and srcY to be within the bounds of src
        srcX = Math.max(Math.min(srcX, srcWidth - srcCroppedW), 0)
        srcY = Math.max(Math.min(srcY, srcHeight - srcCroppedH), 0)
        return Bitmap.createBitmap(
            src, srcX, srcY, srcCroppedW, srcCroppedH, m,
            true
        )
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap =
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap!!)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getDrawableFromColor(color: Int): Drawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        return drawable
    }

    fun convertDpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun convertSpToPixels(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun textAsBitmap(
        context: Context,
        text: String,
        font: String,
        textSize: Float,
        textColor: Int,
        aligment: Aligment
    ): TextLine {
        val textLine = TextLine()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val typeface = Typeface.createFromAsset(context.assets, font)
        paint.typeface = typeface
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = when (aligment) {
            Aligment.RIGHT -> Paint.Align.RIGHT
            Aligment.CENTER -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
        //paint.setFakeBoldText(true);
        val baseline = -paint.ascent() // ascent() is negative
        val width = paint.measureText(text).toInt() // round
        val height = (baseline + paint.descent()).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(
            text,
            when (aligment) {
                Aligment.RIGHT -> image.width.toFloat()
                Aligment.CENTER -> (width / 2).toFloat()
                Aligment.LEFT -> 0f
            },
            baseline, paint
        )
        textLine.bitmap = image
        textLine.height = height
        textLine.width = width
        textLine.text = text
        return textLine
    }

    fun textToBitmap(text: Line): Single<Bitmap> {
        val font = "fonts/" + "ElMessiri-SemiBold.ttf"
        val metrics = resources.displayMetrics
        val displayWidth = Math.min(metrics.widthPixels, metrics.heightPixels)
        var textHeight = 1


        val textFromRight = text.rightText?.let {
            getScaledLines(
                displayWidth,
                textHeight,
                it.text.trim().split(" ").toTypedArray(),
                font,
                it.color,
                it.size,
                Aligment.RIGHT
            )
        }
        val textFromLeft = text.leftText?.let {
            getScaledLines(
                displayWidth,
                textHeight,
                it.text.trim().split(" ").toTypedArray(),
                font,
                it.color,
                it.size,
                Aligment.LEFT
            )
        }

        val centerText = text.centerText?.let {
            getScaledLines(
                displayWidth,
                textHeight,
                it.text.trim().split(" ").toTypedArray(),
                font,
                it.color,
                it.size,
                Aligment.CENTER
            )
        }

        return Single.just(text)
            .map { arrayOf(textFromLeft, textFromRight, centerText) }
            .map {
                var textFromLeftHeight = 0
                var textFromRightHeight = 0
                var textFromCenterHeight = 0
                it[0]?.map { textFromLeftHeight += it.height + /*lineSpacing*/0 }
                it[1]?.map { textFromRightHeight += it.height + /*lineSpacing*/0 }
                it[2]?.map { textFromCenterHeight += it.height + /*lineSpacing*/0 }
                max(max(textFromLeftHeight, textFromRightHeight), textFromCenterHeight)
            }
            .flatMap { getFinalBitmap(displayWidth, it, textFromRight, textFromLeft, centerText) }
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                val width = drawingView.width
//                val height: Int = getDrawingViewHeight()
//                drawingView.loadQuot(it, width, height)
//            }, {
//                it.printStackTrace()
//            }).addTo(bag)

    }

    fun textToBitmap(lines: ArrayList<Line>): Single<Bitmap> {

        return Single.create{
            val metrics = resources.displayMetrics
            val displayWidth = Math.min(metrics.widthPixels, metrics.heightPixels)
            val bitmaps = lines.map { textToBitmap(it).blockingGet() }
            val bitmapHeight = bitmaps.map { it.height }.sum()
            var finalBitmap = Bitmap.createBitmap(displayWidth, bitmapHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(finalBitmap)

            var lastY = 0f
            bitmaps.forEach {
                canvas.drawBitmap(it,0f,lastY, null)
                lastY += it.height
            }

            it.onSuccess(finalBitmap)
        }
    }

//    fun textToBitmap(text: ArrayList<Line>, aligment: Aligment): Single<Bitmap> {
//        val font = "fonts/" + "ElMessiri-SemiBold.ttf"
//        val metrics = resources.displayMetrics
//        val displayWidth = Math.min(metrics.widthPixels, metrics.heightPixels)
//        var textHeight = 1
//
////        return Single.just(text.trim().split(" "))
////            .map { getScaledLines(displayWidth, textHeight, it.toTypedArray(), font, aligment) }
////            .map {
////                textHeight = 0
////                it.map { textHeight += it.height + /*lineSpacing*/7 }
////                it
////            }
////            .flatMap { getFinalBitmap(displayWidth, textHeight, it, aligment) }
//
////            .subscribeOn(Schedulers.newThread())
////            .observeOn(AndroidSchedulers.mainThread())
////            .subscribe({
////                val width = drawingView.width
////                val height: Int = getDrawingViewHeight()
////                drawingView.loadQuot(it, width, height)
////            }, {
////                it.printStackTrace()
////            }).addTo(bag)
//
//    }


    private fun getScaledLines(
        displayWidth: Int,
        textHeight: Int,
        words: Array<String>,
        font: String,
        color: Int,
        size: Float,
        aligment: Aligment
    ): ArrayList<TextLine> {
        val textBitmap = Bitmap.createBitmap(displayWidth, textHeight, Bitmap.Config.ARGB_8888)
        var done = false
        var mLines = ArrayList<String>()

        var currentWordIndex = 0
        var currentLineIndex = 0

        while (!done) {
            val newColumnLine = getLineBitmap(
                if (mLines.size > currentLineIndex) mLines[currentLineIndex] + " " else "" + (words[currentWordIndex]),
                font,
                color,
                size,
                aligment
            )

            if (newColumnLine.bitmap?.width!! > textBitmap.width) {
                currentLineIndex++
            } else {
                if (mLines.size <= currentLineIndex) mLines.add("")
                mLines[currentLineIndex] =
                    (mLines[currentLineIndex] + " " + (words[currentWordIndex]))
                currentWordIndex++
            }

            if (currentWordIndex >= words.size)
                done = true
        }


        //
//        while (!done) {
//            var processes = 0
//            wordsBitmaps = getLinesBitmaps(wordsBitmaps.map { it.text!! }.toTypedArray(), font).blockingGet()
//            for (i in wordsBitmaps.indices) {
//                val textLine = wordsBitmaps[i]
//                val b = textLine.bitmap
//
//                if (b != null) {
//                    if (b.width > textBitmap.width) {
//                        processes++
//                        val lastWord = textLine.text?.split(" ").let { it?.get(it.size - 1) }
//                        if ((i + 1) <= wordsBitmaps.size - 1) {
//                            wordsBitmaps[i + 1].text = "$lastWord " + wordsBitmaps[i + 1].text
//                            wordsBitmaps[i].text =
//                                wordsBitmaps[i].text?.substring(0, wordsBitmaps[i].text?.lastIndexOf(" ")!!)
//                            break
//                        } else {
//                            wordsBitmaps[i].text =
//                                wordsBitmaps[i].text?.substring(0, wordsBitmaps[i].text?.lastIndexOf(" ")!!)
//                            wordsBitmaps.add(textLine.copy().apply { text = lastWord })
//                        }
//                    } else {
//
//                    }
//                }
//            }
//            if (processes == 0) done = true
//        }
        return getLinesBitmaps(mLines.toTypedArray(), font, color, size, aligment).blockingGet()
    }

    private fun getLinesBitmaps(
        quotArray: Array<String>,
        font: String,
        color: Int,
        size: Float,
        aligment: Aligment
    ): Single<ArrayList<TextLine>> {
        return Single.create {
            val textLines = ArrayList<TextLine>()
            for (i in quotArray.indices) {
                if (quotArray[i].isNotEmpty())
                    textLines.add(
                        textAsBitmap(
                            context,
                            quotArray[i],
                            font,
                            convertSpToPixels(/*fontSize*/size, context).toFloat(),
                            color,
                            aligment
                        )
                    )
            }
            it.onSuccess(textLines)
        }

    }

    private fun getLineBitmap(
        word: String,
        font: String,
        color: Int,
        size: Float,
        aligment: Aligment
    ): TextLine {
        return textAsBitmap(
            context,
            word,
            font,
            convertSpToPixels(/*fontSize*/size, context).toFloat(),
            color,
            aligment
        )
    }

    private fun getFinalBitmap(
        displayWidth: Int,
        textHeight: Int,
        textFromRight: ArrayList<TextLine>?,
        textFromLeft: ArrayList<TextLine>?,
        centerText: ArrayList<TextLine>?
    ): Single<Bitmap> {
        val textBitmap = Bitmap.createBitmap(displayWidth, textHeight, Bitmap.Config.ARGB_8888)

        var rightTextBitmap = textFromRight?.let {
            getTextBitmap(
                it,
                textBitmap,
                displayWidth,
                textHeight,
                Aligment.RIGHT
            ).blockingGet()
        }
        var leftTextBitmap = textFromLeft?.let {
            getTextBitmap(
                it,
                textBitmap,
                displayWidth,
                textHeight,
                Aligment.LEFT
            ).blockingGet()
        }
        var centerTextBitmap = centerText?.let {
            getTextBitmap(
                it,
                textBitmap,
                displayWidth,
                textHeight,
                Aligment.CENTER
            ).blockingGet()
        }

        return Single.just(arrayOf(rightTextBitmap, leftTextBitmap, centerTextBitmap))
            .map {

                val backgroundBitmap = Bitmap.createBitmap(
                    textBitmap.width + convertDpToPx(10),
                    textHeight + convertDpToPx(10),
                    Bitmap.Config.ARGB_8888
                )
               // val backgroundCanvas = Canvas(backgroundBitmap)
               // backgroundCanvas.drawColor(/*textShadowColor*/ Color.parseColor("#B0FFFFFF"))
                val resultBitmap = Bitmap.createBitmap(backgroundBitmap)
                val resultCanvas = Canvas(resultBitmap)

                resultCanvas.drawBitmap(
                    textBitmap,
                    ((backgroundBitmap.width - textBitmap.width) / 2).toFloat()- convertDpToPx(5),
                    (textHeight / 2 - backgroundBitmap.height / 2 + convertDpToPx(10)).toFloat(),
                    Paint(Paint.ANTI_ALIAS_FLAG)
                )
                resultBitmap
            }
    }

    private fun getTextBitmap(
        textLines: ArrayList<TextLine>,
        bitmap: Bitmap,
        displayWidth: Int,
        textHeight: Int,
        aligment: Aligment
    ): Single<Bitmap> {
        return Single.create {
            val textCanvas = Canvas(bitmap)
            val i = intArrayOf(0)
            textLines.forEach { textLine ->
                var b: Bitmap? = textLine.bitmap
                if (b != null) {
                    //b = Bitmap.createScaledBitmap(b, b.width, b.height, false)
                    textCanvas.drawBitmap(
                        b,
                        when (aligment) {
                            Aligment.RIGHT -> (displayWidth - b.width).toFloat()
                            Aligment.CENTER -> ((displayWidth - b.width) / 2).toFloat()
                            Aligment.LEFT -> 0f
                        },
                        // ((displayWidth - b.width)).toFloat(),
                        (textHeight / textLines.size * i[0]).toFloat(),
                        Paint(Paint.ANTI_ALIAS_FLAG)
                    )
                }
                i[0]++
            }
            it.onSuccess(bitmap)
        }
    }

    enum class Aligment {
        RIGHT,
        CENTER,
        LEFT
    }
}

