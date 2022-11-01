package com.michael_zhu.myruns.ui.start.manual.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class RotatableTimePickerDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments == null) {
            return TimePickerDialog(
                requireActivity(),
                { _: TimePicker, _: Int, _: Int -> },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false
            )
        }

        val hour = requireArguments().getInt("hour")
        val minute = requireArguments().getInt("minute")
        val listener = requireArguments().getParcelable<TimeDialogListener>("listener")

        return TimePickerDialog(
            requireActivity(),
            { timePicker: TimePicker, i: Int, i1: Int ->
                listener?.onTimeSet(timePicker, i, i1)
            },
            hour,
            minute,
            false
        )
    }

}