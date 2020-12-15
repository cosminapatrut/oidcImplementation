package com.orange.volunteers.history

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.orange.volunteers.R
import com.orange.volunteers.util.interpolateColor
import kotlinx.android.synthetic.main.fragment_history_tab_item.view.*

class HistoryTabsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val optionTabViews = arrayListOf<View>()
    private var xStart: Float = 0f
    private var xEnd: Float = 0f
    private var yLine: Float = 0f
    private var xNextStart: Float = 0f
    private var xNextEnd: Float = 0f
    private var progress: Float = 0f
    private var selectedIndex: Int = 0

    var isFirstOptionInitialized = false
    var colors = mutableListOf(
        Color.parseColor("#FF7900"),
        Color.parseColor("#FF7900")
//        ,
//        Color.parseColor("#A885D8")
    )

    private val initialColor = colors[0]
    var preselectedOption = 0

    private val themeColor = Color.parseColor("#ff7900")

    private var startColor = initialColor
    private var endColor = initialColor
    private var padding = 0f

    private lateinit var optionsContainer: LinearLayout
    var paint = Paint()
    var onTabSelectedCallback: ((Int) -> Unit)? = null

    init {
        setWillNotDraw(false)
        padding = context.resources.getDimension(R.dimen.tab_padding) * 2
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.HistoryTabsLayout)
            val options = attributes.getTextArray(R.styleable.HistoryTabsLayout_optionsList)
            options?.let {
                addOptions(it.asList().map { entry -> entry.toString() })
            }
            val useThemeColor = attributes.getBoolean(R.styleable.HistoryTabsLayout_useThemeColor, false)
            if (useThemeColor) {
                startColor = themeColor
//                endColor = themeColor
                colors.fill(themeColor)
            }
            attributes.recycle()
        }
    }

    private fun addOptions(options: List<String>) {
        optionsContainer = LinearLayout(context)

        options.forEachIndexed { index, opt ->
            val optionTab = LayoutInflater.from(context).inflate(
                R.layout.fragment_history_tab_item,
                this,
                false
            )

            optionTab.tabTitle.text = opt

            if (index == 0) {
                // hide extra divider
                optionTab.dividerView.visibility = View.GONE
            }

            optionsContainer.addView(optionTab)
            optionTabViews.add(optionTab)

            optionTab.setOnClickListener {
                selectedIndex = index
                onTabSelectedCallback?.invoke(selectedIndex)
                onTabSelected(index)
            }
        }

        addView(optionsContainer)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!isFirstOptionInitialized && optionTabViews.count() > 0) {
            xStart = optionTabViews[preselectedOption].x + padding / 2
            xEnd = xStart + optionTabViews[preselectedOption].width - padding
            xNextStart = xStart
            xNextEnd = xEnd
            yLine = optionsContainer.height.toFloat() + 10
            isFirstOptionInitialized = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = interpolateColor(startColor, endColor, progress)
        paint.strokeWidth = 6f

        val x0 = xStart + ((xNextStart - xStart) * progress)
        val x1 = xEnd + ((xNextEnd - xEnd) * progress)

        canvas?.drawLine(
            x0,
            yLine,
            x1,
            yLine,
            paint
        )

        super.onDraw(canvas)
    }

     fun onTabSelected(selectedPosition: Int) {
        val selectedOption = optionTabViews[selectedPosition]
        xStart += ((xNextStart - xStart) * progress)
        xEnd += ((xNextEnd - xEnd) * progress)

        startColor = interpolateColor(startColor, endColor, progress)
        endColor = colors[selectedPosition]

        xNextStart = selectedOption.x + padding / 2
        xNextEnd = xNextStart + selectedOption.width - padding

        val slideAnimator = ValueAnimator
            .ofFloat(0f, 100f)
            .setDuration(500L)

        slideAnimator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float / 100f
            invalidate()
        }

        slideAnimator.start()
         for(i in 0 until optionTabViews.size) {
             if(i != selectedPosition) {
                 optionTabViews[i].tabTitle.setTextColor(resources.getColor(R.color.home_grey_text))
             } else {
                 optionTabViews[i].tabTitle.setTextColor(resources.getColor(R.color.black))
             }
         }
     }
}