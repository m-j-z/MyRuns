package com.michael_zhu.myruns

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.michael_zhu.myruns.ui.TabPagerAdapter
import com.michael_zhu.myruns.ui.history.HistoryFragment
import com.michael_zhu.myruns.ui.settings.SettingsFragment
import com.michael_zhu.myruns.ui.start.StartFragment

class MainActivity : AppCompatActivity() {
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val tabs = arrayListOf(
        StartFragment.newInstance(), HistoryFragment.newInstance(), SettingsFragment.newInstance()
    )
    private val tabNames = arrayListOf("Start", "History", "Settings")

    /**
     * Sets the view of the activity, initializes the main UI tabs, and request the required permissions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createUiTabs()
        requestPermissions()
    }

    /**
     * Create main tabs.
     */
    private fun createUiTabs() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = TabPagerAdapter(supportFragmentManager, lifecycle)
        adapter.setTabNames(tabNames)
        adapter.setTabs(tabs)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = adapter.getTitle(i)
        }.attach()
    }

    /**
     * Requests the required permissions to run the app as specified by AndroidManifest.xml.
     */
    private fun requestPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }
}