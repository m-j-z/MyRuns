package com.michael_zhu.myruns.ui.start.gps

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.michael_zhu.myruns.R

class GPSActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        cancelBtn = findViewById(R.id.cancel_btn)
        saveBtn = findViewById(R.id.save_btn)

        cancelBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.cancel_btn -> {
                Toast.makeText(this, "Entry discarded!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.save_btn -> {
                // TODO implement gps entry
                Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}