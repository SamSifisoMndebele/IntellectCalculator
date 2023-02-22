package com.avidco.intellectcalculator.ui.calculations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.ActivityNumberSystemBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class NumberSystemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumberSystemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityNumberSystemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val navController = findNavController(R.id.nav_host_fragment_activity_number_system)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_convert, R.id.navigation_excess,
                R.id.navigation_complement, R.id.navigation_floating_point
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