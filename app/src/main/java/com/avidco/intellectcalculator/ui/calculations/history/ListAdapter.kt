package com.avidco.intellectcalculator.ui.calculations.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.avidco.intellectcalculator.ui.expressions.HistoryListActivity
import com.avidco.intellectcalculator.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class ListAdapter(private val activity: HistoryListActivity) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var arrayList: ArrayList<StringModel> = DatabaseHelper(activity).dataList
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity,activity.getString(R.string.activity_expressions_history_interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                println("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.history_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        viewHolder.text.text = arrayList[i].string

        viewHolder.itemView.setOnClickListener {
            val string = arrayList[i].string
            DatabaseHelper(activity).updateString(string)
            activity.finish()
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
            } else {
                println( "The interstitial ad wasn't ready yet.")
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            val string = arrayList[i].string
            AlertDialog.Builder(activity).apply {
                setMessage("Deleting :- $string")
                setPositiveButton("Delete"){dialog,_->
                    DatabaseHelper(context).deleteString(string)
                    dialog.dismiss()
                }
                setOnDismissListener {
                    arrayList.clear()
                    arrayList = DatabaseHelper(activity).dataList
                    this@ListAdapter.notifyDataSetChanged()
                }
                show()
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.textHistory)
    }
}