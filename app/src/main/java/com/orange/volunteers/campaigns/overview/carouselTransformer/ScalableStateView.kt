package com.orange.volunteers.campaigns.overview.carouselTransformer

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class ScalableStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var onScale: ((Float) -> Unit)? = null

    fun scale(position: Float) {
        onScale?.invoke(position)
    }
}