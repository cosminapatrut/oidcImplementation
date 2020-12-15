package com.orange.volunteers.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orange.volunteers.MainAdapter
import com.orange.volunteers.R
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.startTrustBadge
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        trust_badge.setOnClickListener {
            requireContext().startTrustBadge(activity as HomeActivity)
        }
    }

    companion object {
        fun newInstance(): InfoFragment {
            return InfoFragment()
        }
    }
}