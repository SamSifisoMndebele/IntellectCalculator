package com.avidco.intellectcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import com.avidco.intellectcalculator.databinding.ActivityMainBinding
import com.avidco.intellectcalculator.ui.calculations.NumberSystemActivity
import com.avidco.intellectcalculator.ui.expressions.ExpressionsActivity
import com.avidco.intellectcalculator.ui.karnaugh.KarnaughActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest : AdRequest
    private var displayHomeAsUpEnabled : Boolean = false
    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""


        displayHomeAsUpEnabled = intent.getBooleanExtra("displayHomeAsUpEnabled", false)
        supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)


        MobileAds.initialize(this) {}
        adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.expressions.setOnClickListener {
            val intent = Intent(this, ExpressionsActivity::class.java)
            startActivity(intent)
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                println( "Open: The interstitial ad wasn't ready yet.")
            }
        }
        binding.numberSystem.setOnClickListener {
            val intent = Intent(this, NumberSystemActivity::class.java)
            startActivity(intent)
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                println( "Open: The interstitial ad wasn't ready yet.")
            }
        }
        binding.karnaughMaps.setOnClickListener {
            val intent = Intent(this, KarnaughActivity::class.java)
            startActivity(intent)
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                println( "Open: The interstitial ad wasn't ready yet.")
            }
        }

        /**Back method*/
        onBackPressedMethod()
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (displayHomeAsUpEnabled) {
            finishAffinity()
            true
        } else false
    }

    /*override fun onBackPressed() {
        val toast = Toast.makeText(this, "Click Back again to exit.", Toast.LENGTH_SHORT)
        if (exit){
            toast.cancel()
            super.onBackPressed()
        }else{
            exit = true
            toast.show()

            Handler(Looper.getMainLooper()).postDelayed({
                exit = false
            }, 2000)
        }
    }*/


    private fun onBackPressedMethod() {
        val toast = Toast.makeText(this, "Click Back again to exit.", Toast.LENGTH_SHORT)
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                if (!onSupportNavigateUp()) {
                    if (exit) {
                        toast.cancel()
                        finish()
                    } else {
                        exit = true
                        toast.show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            exit = false
                        }, 4000)
                    }
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (!onSupportNavigateUp()) {
                        if (exit) {
                            toast.cancel()
                            finish()
                        } else {
                            exit = true
                            toast.show()

                            Handler(Looper.getMainLooper()).postDelayed({
                                exit = false
                            }, 4000)
                        }
                    }
                }
            })
        }
    }

    override fun onRestart() {
        super.onRestart()
        InterstitialAd.load(this,getString(R.string.activity_main_interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                println("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_rate -> {
                openPlayStore()
            }
            R.id.item_share -> {
                val intent = Intent("android.intent.action.SEND")
                intent.putExtra("android.intent.extra.TEXT", """Download Intellect Calculator for FREE and solve Computer Science problems. 
                    |
                    |Goto Google Play https://play.google.com/store/apps/details?id=com.avidco.intellectcalculator""".trimMargin())
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share the App"))
            }
            R.id.item_feedback -> {
                try {
                    val intent = Intent("android.intent.action.SENDTO")
                    intent.data = Uri.parse("mailto:" + getString(R.string.contact_email))
                    intent.putExtra("android.intent.extra.SUBJECT", "Contact - Intellect Calculator")
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.gmail_not_installed), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun openPlayStore() {
        try {
            val intent = Intent("android.intent.action.VIEW")
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.avidco.intellectcalculator")
            intent.setPackage("com.android.vending")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Play Store not installed", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun showVideoDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.watch_video)
        builder.setMessage(R.string.watch_video_text)
        builder.setPositiveButton(R.string.watch_video_yes) { _: DialogInterface?, _: Int ->
            Toast.makeText(this@MainActivity2, R.string.video_not_found, Toast.LENGTH_LONG).show()
            super@MainActivity2.onBackPressed()
        }
        builder.setNegativeButton(getString(R.string.watch_video_no)) { _: DialogInterface?, _: Int -> super@MainActivity2.onBackPressed() }
        builder.show()
    }

    private fun showPremiumDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.premium_title)
        builder.setMessage(R.string.premium_message)
        builder.setPositiveButton(R.string.premium_yes) { _: DialogInterface?, _: Int ->
            openPlaystore("com.avidco.intellectcalculator")
        }
        builder.setNegativeButton(R.string.premium_no, null)
        builder.show()
    }*/
}