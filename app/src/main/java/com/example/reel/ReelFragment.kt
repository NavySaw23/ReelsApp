package com.example.reel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reel.databinding.FragmentReelBinding

class ReelFragment : Fragment() {
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var videoUrl: String? = null

    private var _binding: FragmentReelBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "ReelFragment"
        private const val ARG_VIDEO_URL = "video_url"

        fun newInstance(videoUrl: String) = ReelFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_VIDEO_URL, videoUrl)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        videoUrl = arguments?.getString(ARG_VIDEO_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        _binding = FragmentReelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")
        initializePlayer()
        setupLikeAndShareButtons()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(videoUrl!!)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

    private fun setupLikeAndShareButtons() {
        binding.likeButton.setOnClickListener {
            // Implement like functionality
            binding.likeButton.isSelected = !binding.likeButton.isSelected
        }

        binding.shareButton.setOnClickListener {
            // Implement share functionality
            shareReel()
        }
    }

    private fun shareReel() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing reel: $videoUrl")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Reel"))
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called")
        _binding = null
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }
}