package com.example.reel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.reel.databinding.FragmentReelBinding

class ReelFragment : Fragment() {
    private var _binding: FragmentReelBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var videoUrl: String? = null

    companion object {
        private const val ARG_VIDEO_URL = "video_url"

        fun newInstance(videoItem: VideoItem): ReelFragment {
            val fragment = ReelFragment()
            val args = Bundle()
            args.putString(ARG_VIDEO_URL, videoItem.url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoUrl = it.getString(ARG_VIDEO_URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayer()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        videoUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    fun pauseVideo() {
        player?.pause()
    }

    fun resumeVideo() {
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onResume() {
        super.onResume()
        resumeVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }
}