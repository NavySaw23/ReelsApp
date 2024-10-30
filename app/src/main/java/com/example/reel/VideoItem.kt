package com.example.reel


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoItem(val url: String, val likeCount: Int) : Parcelable