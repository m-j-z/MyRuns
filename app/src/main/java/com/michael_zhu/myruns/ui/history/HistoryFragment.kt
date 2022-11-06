package com.michael_zhu.myruns.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.database.history.*

class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var listView: ListView
    private lateinit var historyDatabase: HistoryDatabase
    private lateinit var historyDatabaseDao: HistoryDatabaseDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyViewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    /**
     * Sets the view of the fragment.
     * Initializes the history database.
     * Initializes the list view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        listView = view.findViewById(R.id.list_view)
        historyDatabase = HistoryDatabase.getInstance(requireActivity())
        historyDatabaseDao = historyDatabase.historyDatabaseDao
        historyRepository = HistoryRepository(historyDatabaseDao)
        historyViewModelFactory = HistoryViewModelFactory(historyRepository)
        historyViewModel = ViewModelProvider(
            requireActivity(),
            historyViewModelFactory
        )[HistoryViewModel::class.java]

        listView.setOnItemClickListener { parent, _, position, _ ->
            val entry = parent.adapter.getItem(position) as Entry
            if (entry.inputType == "Manual") {
                startDisplayActivity(DisplayEntryActivity::class.java, entry.id)
            } else {
                startDisplayActivity(DisplayMapsEntryActivity::class.java, entry.id)
            }
        }

        historyViewModel.historyLiveData.observe(requireActivity()) {
            val adapter = EntryListViewAdapter(it, requireActivity())
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        return view
    }

    private fun startDisplayActivity(java: Class<*>, id: Long) {
        val intent = Intent(requireActivity(), java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (listView.adapter != null) {
            historyViewModel.historyLiveData.observe(requireActivity()) {
                val adapter = EntryListViewAdapter(it, requireActivity())
                listView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }
}