package com.avidco.intellectcalculator.ui.expressions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.ui.calculations.history.DatabaseHelper
import com.avidco.intellectcalculator.ui.calculations.history.ListAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HistoryListActivity : AppCompatActivity() {
    private lateinit var historyAdapter : ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_history_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        findViewById<AdView>(R.id.adView).loadAd(adRequest)

        val recyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
        historyAdapter = ListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_clear -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Clear All List?")
                    setMessage("All history data will be erased.")
                    setPositiveButton("Clear All"){d,_->
                        DatabaseHelper(this@HistoryListActivity).clearStrings()
                        d.dismiss()
                        this@HistoryListActivity.finish()
                    }
                    setNegativeButton("Cancel"){d,_->
                        d.dismiss()
                    }
                    show()
                }

            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}