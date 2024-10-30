package com.example.reel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.reel.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reelPagerAdapter: ReelPagerAdapter
    private lateinit var database: DatabaseReference
    private val videoUrls = mutableListOf<VideoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check Google Play Services
        checkGooglePlayServices()

        // Initialize Firebase Database with correct URL
        database = FirebaseDatabase.getInstance("https://reelsapp-81e28-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        fetchVideosFromFirebase()
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Log.e("PlayServices", "This device is not supported")
                finish()
            }
        } else {
            Log.d("PlayServices", "Google Play Services is available")
        }
    }

    private fun fetchVideosFromFirebase() {
        database.child("videos").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Data fetch started")
                videoUrls.clear()
                if (snapshot.exists()) {
                    Log.d("Firebase", "Snapshot exists with ${snapshot.childrenCount} children")
                    for (videoSnapshot in snapshot.children) {
                        val url = videoSnapshot.child("url").getValue(String::class.java)
                        val likeCount = videoSnapshot.child("likeCount").getValue(Int::class.java) ?: 0
                        url?.let {
                            videoUrls.add(VideoItem(it, likeCount))
                            Log.d("Firebase", "Added URL: $it with like count: $likeCount")
                        }
                    }
                    setupViewPager()
                    Log.d("Firebase", "Total URLs fetched: ${videoUrls.size}")
                } else {
                    Log.d("Firebase", "Snapshot does not exist or is empty")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data: ${error.message}")
            }
        })
    }

    private fun setupViewPager() {
        if (videoUrls.isNotEmpty()) {
            reelPagerAdapter = ReelPagerAdapter(this, videoUrls)
            binding.viewPager.adapter = reelPagerAdapter
            Log.d("ViewPager", "ViewPager setup complete with ${videoUrls.size} items")
        } else {
            Log.d("ViewPager", "No items to display in ViewPager")
        }
    }
}