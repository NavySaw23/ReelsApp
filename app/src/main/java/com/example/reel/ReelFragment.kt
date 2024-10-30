package com.example.reel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.reel.databinding.FragmentReelBinding
import com.google.firebase.database.*

class ReelFragment : Fragment() {
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var videoItem: VideoItem? = null
    private lateinit var database: DatabaseReference

    private var _binding: FragmentReelBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_VIDEO_ITEM = "video_item"

        fun newInstance(videoItem: VideoItem) = ReelFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_VIDEO_ITEM, videoItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoItem = it.getParcelable(ARG_VIDEO_ITEM)
        }
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        setupLikeAndShareButtons()
        updateLikeCountDisplay()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                videoItem?.url?.let { url ->
                    val mediaItem = MediaItem.fromUri(url)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(currentWindow, playbackPosition)
                    exoPlayer.prepare()

                    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_ENDED) {
                                exoPlayer.seekTo(0)
                                exoPlayer.play()
                            }
                        }
                    })
                }
            }
    }

    private fun setupLikeAndShareButtons() {
        binding.likeButton.setOnClickListener {
            binding.likeButton.isSelected = !binding.likeButton.isSelected
            updateLikeCount(binding.likeButton.isSelected)
        }

        binding.shareButton.setOnClickListener {
            shareReel()
        }
    }

    private fun updateLikeCount(isLiked: Boolean) {
        videoItem?.let { item ->
            val newLikeCount = if (isLiked) item.likeCount + 1 else maxOf(0, item.likeCount - 1)
            videoItem = item.copy(likeCount = newLikeCount)
            updateLikeCountDisplay()
            updateLikeCountInDatabase(newLikeCount)
        }
    }

    private fun updateLikeCountInDatabase(newLikeCount: Int) {
        videoItem?.url?.let { url ->
            database.child("videos").orderByChild("url").equalTo(url)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val videoRef = snapshot.children.first().ref
                            videoRef.child("likeCount").setValue(newLikeCount)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Error updating like count: ${error.message}")
                    }
                })
        }
    }

    private fun updateLikeCountDisplay() {
        binding.likeCountText.text = videoItem?.likeCount?.toString() ?: "0"
    }

    private fun shareReel() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing reel: ${videoItem?.url}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Reel"))
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
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