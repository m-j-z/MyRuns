package com.michael_zhu.myruns.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.misc.Utility

class EntryListViewAdapter(
    private val dataList: List<Entry>,
    private val context: Context
) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var newView = convertView
        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (newView == null) {
            newView = layoutInflater!!.inflate(R.layout.listview_item, null)
        }
        newView!!

        val data = dataList[position]

        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        val unitPref = preference.getString("unit_preference", "km").toString()

        val entryTypeTextView: TextView = newView.findViewById(R.id.entry_type_tv)
        val activityTypeTextView: TextView = newView.findViewById(R.id.activity_type_tv)
        val timeTextView: TextView = newView.findViewById(R.id.time_tv)
        val dateTextView: TextView = newView.findViewById(R.id.date_tv)
        val distanceTextView: TextView = newView.findViewById(R.id.distance_tv)
        val durationTextView: TextView = newView.findViewById(R.id.duration_tv)

        entryTypeTextView.text = data.inputType
        activityTypeTextView.text = data.activityType
        timeTextView.text = Utility.longToTime(data.time)
        dateTextView.text = Utility.longToDate(data.date)
        val distance = Utility.convertUnits(unitPref, data.unitSavedAs, data.distance).toString() + " $unitPref"
        distanceTextView.text = distance
        durationTextView.text = Utility.longToDuration(data.duration.toLong())

        return newView
    }

}