package com.orange.volunteers.util

import android.graphics.Color
import android.graphics.Color.*
import java.util.*

fun getRandomColor(): Int {
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}

private fun interpolate(a: Float, b: Float, proportion: Float): Float {
    return a + (b - a) * proportion
}

/** Returns an interpoloated color, between `a` and `b`  */
fun interpolateColor(color1: Int, color2: Int, proportion: Float): Int {
    val step = Math.max(Math.min(proportion, 1.0f), 0.0f)

    // Calculate difference between alpha, red, green and blue channels
    val deltaAlpha: Int = alpha(color2) - alpha(color1)
    val deltaRed: Int = red(color2) - red(color1)
    val deltaGreen: Int = green(color2) - green(color1)
    val deltaBlue: Int = blue(color2) - blue(color1)

    // Result channel lies between first and second colors channel
    var resultAlpha = (alpha(color1) + deltaAlpha * step).toInt()
    var resultRed = (red(color1) + deltaRed * step).toInt()
    var resultGreen = (green(color1) + deltaGreen * step).toInt()
    var resultBlue = (blue(color1) + deltaBlue * step).toInt()

    // Cutoff to ranges between 0 and 255
    resultAlpha = Math.max(Math.min(resultAlpha, 255), 0)
    resultRed = Math.max(Math.min(resultRed, 255), 0)
    resultGreen = Math.max(Math.min(resultGreen, 255), 0)
    resultBlue = Math.max(Math.min(resultBlue, 255), 0)

    // Combine results
    return resultAlpha shl 24 or (resultRed shl 16) or (resultGreen shl 8) or resultBlue
}

fun hueChange(c: Int, deg: Float): Int {
    val hsv = FloatArray(3) //array to store HSV values
    Color.colorToHSV(c, hsv) //get original HSV values of pixel
    hsv[0] = hsv[0] + deg //add the shift to the HUE of HSV array
    hsv[0] = hsv[0] % 360 //confines hue to values:[0,360]
    return Color.HSVToColor(Color.alpha(c), hsv)
}