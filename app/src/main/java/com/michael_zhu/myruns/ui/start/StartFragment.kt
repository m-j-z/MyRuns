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

        viewModel.inputType = inputTypeSpinner.selectedItem.toString()
        viewModel.activityType = activityTypeSpinner.selectedItem.toString()

        return view
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.start_btn -> {
                val intent = Intent(requireActivity(), InputActivity::class.java)
                intent.putExtra("input_type", inputTypeSpinner.selectedItem.toString())
                startActivity(intent)
            }
        }
    }
}