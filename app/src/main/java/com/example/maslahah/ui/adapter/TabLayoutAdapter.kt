package com.example.maslahah.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.maslahah.ui.home.AllServicesScreen
import com.example.maslahah.ui.home.MyServiceScreen


class TabLayoutAdapter(
    fm: FragmentManager, behavior: Int,
    private val count: Int, private val type: Int
) :
    FragmentPagerAdapter(fm, behavior) {


    override fun getItem(position: Int): Fragment {

        if (position == 0)
            return AllServicesScreen()

        return MyServiceScreen()


    }

    override fun getCount(): Int = count


}