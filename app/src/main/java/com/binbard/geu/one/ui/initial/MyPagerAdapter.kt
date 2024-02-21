package com.binbard.geu.one.ui.initial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import com.binbard.geu.one.R

class MyPagerAdapter(val fm: FragmentManager) : PagerAdapter() {
    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(container.context)
        val view = when (position) {
            0 -> layoutInflater.inflate(R.layout.fragment_describe_page1, container, false)
            1 -> layoutInflater.inflate(R.layout.fragment_describe_page2, container, false)
            else -> throw IllegalArgumentException("Invalid position")
        }
        view.findViewById<Button>(R.id.btnOnboard)?.setOnClickListener {
            if(it == null) return@setOnClickListener
            fm.beginTransaction().replace(
                R.id.fragmentContainerView3, SelectCampusFragment()
            ).commit()
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
