package com.michael_zhu.myruns.ui.start

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.ui.start.manual.ManualInputActivity
import com.michael_zhu.myruns.ui.start.map.MapsDisplayActivity

class StartFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance() = StartFragment()
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private lateinit var viewModel: StartViewModel
    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner: Spinner
    private lateinit var startBtn: Button

    /**
     * Sets the view of the fragment, adds the possible options for input and activity,
     * and initializes the listeners for the buttons.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        viewModel = ViewModelProvider(this)[StartViewModel::class.java]

        inputTypeSpinner = view.findViewById(R.id.input_type_spinner)
        activityTypeSpinner = view.findViewById(R.id.activity_type_spinner)
        startBtn = view.findViewById(R.id.start_btn)

        ArrayAdapter.createFromResource(
            requireActivity(), R.array.input_array, android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inputTypeSpinner.adapter = it
        }

        ArrayAdapter.createFromResource(
            requireActivity(), R.array.activity_array, android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityTypeSpinner.adapter = it
        }

        startBtn.setOnClickListener(this)

        return view
    }

    /**
     * Determines the actions to perform on [v] clicked.
     */
    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.start_btn -> {
                val activityType = activityTypeSpinner.selectedItem.toString()

                when (inputTypeSpinner.selectedItem.toString()) {
                    "Manual" -> startInputActivity(ManualInputActivity::class.java, activityType)
                    "GPS" -> {
                        if (checkPermissions()) {
                            startInputActivity(
                                MapsDisplayActivity::class.java, activityType, "GPS"
                            )
                        }
                    }
                    "Automatic" -> {
                        if (checkPermissions()) {
                            startInputActivity(
                                MapsDisplayActivity::class.java, activityType, "Automatic"
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts a new activity as defined by [java] while passing the type of activity, [activityType],
     * and the input type, [inputType].
     */
    private fun startInputActivity(
        java: Class<*>, activityType: String, inputType: String = "Manual"
    ) {
        val intent = Intent(requireActivity(), java)
        intent.putExtra("input_type", inputType)
        intent.putExtra("activity_type", activityType)
        startActivity(intent)
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        return false
    }
}