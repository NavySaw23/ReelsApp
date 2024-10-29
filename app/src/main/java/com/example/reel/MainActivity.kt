package com.example.reel

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.reel.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reelPagerAdapter: ReelPagerAdapter

    private val videoUrls = listOf(
        "https://videos.pexels.com/video-files/4167404/4167404-uhd_1440_1920_24fps.mp4",
        "https://videos.pexels.com/video-files/3946082/3946082-uhd_1440_2732_25fps.mp4",
        "https://videos.pexels.com/video-files/28999855/12542194_1080_1920_30fps.mp4",
        "https://videos.pexels.com/video-files/8908443/8908443-hd_1080_192https://videos.pexels.com/video-files/7662734/7662734-hd_1080_1920_30fps.mp40_25fps.mp4",
        "https://samplelib.com/lib/preview/mp4/sample-5s.mp4"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reelPagerAdapter = ReelPagerAdapter(this, videoUrls)
        binding.viewPager.adapter = reelPagerAdapter
    }
}