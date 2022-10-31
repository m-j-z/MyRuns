package com.michael_zhu.myruns.ui.start.automatic

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michael_zhu.myruns.R

class AutomaticFragment : Fragment() {
    companion object {
        fun newInstance() = AutomaticFragment()
    }

    private lateinit var viewModel: AutomaticViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_automatic, container, false)
        viewModel = ViewModelProvider(this)[AutomaticViewModel::class.java]
        return view
    }
}