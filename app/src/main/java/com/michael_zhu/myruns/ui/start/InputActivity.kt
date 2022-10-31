package com.michael_zhu.myruns.ui.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.ui.start.automatic.AutomaticFragment
import com.michael_zhu.myruns.ui.start.gps.GPSFragment
import com.michael_zhu.myruns.ui.start.manual.ManualInputFragment

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val extras = intent.extras
        if (extras != null) {
            val transaction = supportFragmentManager.beginTransaction()
            var fragment: Fragment = ManualInputFragment.newInstance()
            when (extras.getString("input_type")) {
                "Manual" -> fragment = ManualInputFragment.newInstance()
                "GSP" -> fragment = GPSFragment.newInstance()
                "Automatic" -> fragment = AutomaticFragment.newInstance()
            }
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

    }
}