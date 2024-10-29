package com.example.reel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReelPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val videoUrls: List<String>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = videoUrls.size

    override fun createFragment(position: Int): Fragment {
        return ReelFragment.newInstance(videoUrls[position])
    }
}