package com.avidco.intellectcalculator.ui.calculations

import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import android.widget.FrameLayout
import com.google.android.material.tabs.TabLayout
import android.os.Bundle
import android.view.*
import com.avidco.intellectcalculator.R
import androidx.fragment.app.Fragment

class HowFragment : Fragment(), OnTabSelectedListener {
    private var helpContent: FrameLayout? = null
    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return layoutInflater.inflate(R.layout.fragment_how, viewGroup, false)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        helpContent = view.findViewById<View>(R.id.help_content) as FrameLayout
        layoutInflater.inflate(R.layout.help_bases, helpContent, true)
        (view.findViewById<View>(R.id.tablayout) as TabLayout).addOnTabSelectedListener(this as OnTabSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_how, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        helpContent!!.removeAllViews()
        when (tab.position) {
            0 -> {
                layoutInflater.inflate(R.layout.help_bases, helpContent, true)
            }
            1 -> {
                layoutInflater.inflate(R.layout.help_signed, helpContent, true)
            }
            2 -> {
                layoutInflater.inflate(R.layout.help_floating, helpContent, true)
            }
        }
    }
}