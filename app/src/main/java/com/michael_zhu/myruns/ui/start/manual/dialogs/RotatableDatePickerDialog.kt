package com.michael_zhu.myruns.ui.start.manual.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class RotatableDatePickerDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments == null) {
            return DatePickerDialog(
                requireActivity(),
                { _: DatePicker, _: Int, _: Int, _: Int -> },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
        }

        val year = requireArguments().getInt("year")
        val month = requireArguments().getInt("month")
        val dayOfMonth = requireArguments().getInt("dayOfMonth")
        val listener = requireArguments().getParcelable<DateDialogListener>("listener")

        return DatePickerDialog(
            requireActivity(),
            { datePicker: DatePicker, i1: Int, i2: Int, i3: Int ->
                listener?.onDateSet(datePicker, i1, i2, i3)
            },
            year,
            month,
            dayOfMonth
        )
    }
}