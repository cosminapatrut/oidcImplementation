package com.orange.volunteers.campaigns.overview.carouselTransformer

import android.animation.ArgbEvaluator
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.orange.volunteers.R
import kotlin.math.abs
import kotlin.math.min

public class CampaignPageTransformer (private val viewPager: ViewPager) : ViewPager.PageTransformer {

    val minScale = 0.7f
    val paddingLeft = 30f
    var offset = -1f
    var offsetCorection = -1f


    private val activeColor = "#FFFFFFFF"
    private val inactiveColor= "#262323"
    private var viewsToChangeColor: List<Int> = listOf()

    override fun transformPage(page: View, position: Float) {

        if (offset == -1f) {
            val clientWidth = viewPager.measuredWidth - viewPager.paddingLeft - viewPager.paddingRight
            offset = viewPager.paddingLeft.toFloat() / clientWidth
            offsetCorection = viewPager.paddingLeft.toFloat() / 2f
        }
        //offset = paddingLeft + (gap / 2f) / page.measuredWidth

        val fixedPosition = position - offset

        if (fixedPosition <= 1.0) {
            val lambda = min(1f, 1f - abs(fixedPosition))

            page.scaleY = minScale + lambda * (1.0f - minScale)
            page.scaleX = minScale + lambda * (1.0f - minScale)
            scale(page, lambda)
        } else {
            val lambda = 1.0f - min(1f, abs(position))

            page.scaleY = minScale + lambda * (1.0f - minScale)
            page.scaleX = minScale + lambda * (1.0f - minScale)
            scale(page, lambda)

        }
    }

    fun scale(view: View, position: Float) {
        //check if view is scalable
        if(view is ScalableStateView) {
            view.scale(position)
        }
    }

    private fun colorView(child: View, scaleValue: Float) {
        val saturationPercent = (scaleValue - 1) / 1f
        val alphaPercent = scaleValue / 2f
        val matrix = ColorMatrix()
        matrix.setSaturation(saturationPercent)
            val viewToChangeColor = child.findViewById<View>(R.id.campaigns_images_viewpager)
            when (viewToChangeColor) {
                is ImageView -> {
                    viewToChangeColor.colorFilter = ColorMatrixColorFilter(matrix)
                    viewToChangeColor.imageAlpha = (255 * alphaPercent).toInt()
                }
                is TextView -> {
                    val textColor = ArgbEvaluator().evaluate(saturationPercent, inactiveColor, activeColor) as Int
                    viewToChangeColor.setTextColor(textColor)
                }
            }

    }
}