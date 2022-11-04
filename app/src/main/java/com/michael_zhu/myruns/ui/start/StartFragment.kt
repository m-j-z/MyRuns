package com.michael_zhu.myruns.ui.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.ui.start.automatic.AutomaticActivity
import com.michael_zhu.myruns.ui.start.gps.GPSActivity
import com.michael_zhu.myruns.ui.start.manual.ManualInputActivity

class StartFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance() = StartFragment()
    }

    private lateinit var viewModel: StartViewModel
    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner: Spinner
    private lateinit var startBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        viewModel = ViewModelProvider(this)[StartViewModel::class.java]

        inputTypeSpinner = view.findViewById(R.id.input_type_spinner)
        activityTypeSpinner = view.findViewById(R.id.activity_type_spinner)
        startBtn = view.findViewById(R.id.start_btn)

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.input_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inputTypeSpinner.adapter = it
        }

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.activity_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityTypeSpinner.adapter = it
        }

        startBtn.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.start_btn -> {
                val activityType = activityTypeSpinner.selectedItem.toString()

                when (inputTypeSpinner.selectedItem.toString()) {
                    "Manual" -> startInputActivity(ManualInputActivity::class.java, activityType)
                    "GPS" -> startInputActivity(GPSActivity::class.java, activityType)
                    "Automatic" -> startInputActivity(AutomaticActivity::class.java, activityType)
                }
            }
        }
    }

    private fun startInputActivity(java: Class<*>, activityType: String) {
        val intent = Intent(requireActivity(), java)
        intent.putExtra("activity_type", activityType)
        startActivity(intent)
    }
}