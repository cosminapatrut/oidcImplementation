package com.orange.volunteers.campaigns.overview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.orange.domain.model.CampaignModel
import com.orange.volunteers.R
import com.orange.volunteers.campaigns.overview.carouselTransformer.ScalableStateView
import com.orange.volunteers.util.interpolateColor
import kotlinx.android.synthetic.main.campaign_image_viewpager_fragment.*

class CampaignImageViewPagerFragment (
    val data: CampaignModel
) : Fragment() {

    var startColor = 0x00000000.toInt()
    var endColor = 0xD9B8B8B8.toInt()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.campaign_image_viewpager_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(data.pictures.isNotEmpty()) {
            val imageToLoad = data.pictures[0]
            context?.let {
                Glide.with(it)
                    .load(imageToLoad.uri)
                    .into(campaign_image_iv)
            }
        }
        //Transparent white
        val colorSelected =Color.parseColor("#00000000")

        startColor = colorSelected

        setBackgroundImageColor(colorSelected)

        (view as ScalableStateView).onScale = { position ->
            val headerColor = interpolateColor(endColor, startColor, position)

            setBackgroundImageColor(headerColor)
        }
    }

    private fun setBackgroundImageColor(colorSelected: Int) {
        campaign_image_iv.setColorFilter(colorSelected)
    }

}