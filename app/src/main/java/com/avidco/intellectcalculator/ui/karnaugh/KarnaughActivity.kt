package com.avidco.intellectcalculator.ui.karnaugh

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.ActivityKarnaughBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class KarnaughActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKarnaughBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityKarnaughBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        val navController = findNavController(R.id.nav_host_fragment_activity_karnaugh)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_karnaugh2, R.id.navigation_karnaugh3,
                R.id.navigation_karnaugh4
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.upButton.setOnClickListener {
            if (!navController.navigateUp())
                finish()
        }
    }

}