package com.michael_zhu.myruns.ui.start.manual

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michael_zhu.myruns.R

class ManualInputFragment : Fragment() {
    companion object {
        fun newInstance() = ManualInputFragment()
    }

    private lateinit var viewModel: ManualInputViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manual_input, container, false)
        viewModel = ViewModelProvider(this)[ManualInputViewModel::class.java]
        return view
    }
}