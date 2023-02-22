package com.avidco.intellectcalculator.ui.expressions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.ActivityExpressionsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class ExpressionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressionsBinding
    private lateinit var adRequest: AdRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityExpressionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        val navController = findNavController(R.id.nav_host_fragment_activity_expressions)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_expressions, R.id.navigation_tree_diagram,
                R.id.navigation_trace_table
            )
        )

        MobileAds.initialize(this) {}
        adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.upButton.setOnClickListener {
            if (!navController.navigateUp())
                finish()
        }
    }
}