package com.michael_zhu.myruns.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private lateinit var tabs: ArrayList<Fragment>
    private lateinit var tabNames: ArrayList<String>

    fun setTabs(listOfTabs: ArrayList<Fragment>) {
        tabs = listOfTabs
    }

    fun setTabNames(listOfTabNames: ArrayList<String>) {
        tabNames = listOfTabNames
    }

    override fun getItemCount(): Int {
        return tabNames.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabs[position]
    }

    fun getTitle(position: Int): String {
        return tabNames[position]
    }

}