package com.michael_zhu.myruns.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private lateinit var tabs: ArrayList<Fragment>
    private lateinit var tabNames: ArrayList<String>

    /**
     * Sets the tabs of the tab layout to [listOfTabs].
     */
    fun setTabs(listOfTabs: ArrayList<Fragment>) {
        tabs = listOfTabs
    }

    /**
     * Sets the tab names of the tab layout to [listOfTabNames].
     */
    fun setTabNames(listOfTabNames: ArrayList<String>) {
        tabNames = listOfTabNames
    }

    /**
     * Returns the number of tabs of the tab layout.
     */
    override fun getItemCount(): Int {
        return tabNames.size
    }

    /**
     * Returns the tab at [position].
     */
    override fun createFragment(position: Int): Fragment {
        return tabs[position]
    }

    /**
     * Returns the name of the tab at [position].
     */
    fun getTitle(position: Int): String {
        return tabNames[position]
    }

}