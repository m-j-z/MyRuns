package com.michael_zhu.myruns.ui.start.manual.dialogs

import android.app.DatePickerDialog
import android.os.Parcel
import android.os.Parcelable
import android.widget.DatePicker
import com.michael_zhu.myruns.ui.start.manual.ManualInputViewModel
import java.time.LocalDateTime
import java.time.ZoneId

class DateDialogListener() : DatePickerDialog.OnDateSetListener, Parcelable {
    lateinit var viewModel: ManualInputViewModel

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DateDialogListener> {
        override fun createFromParcel(parcel: Parcel): DateDialogListener {
            return DateDialogListener(parcel)
        }

        override fun newArray(size: Int): Array<DateDialogListener?> {
            return arrayOfNulls(size)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.year = year
        viewModel.month = month
        viewModel.dayOfMonth = dayOfMonth

        val localDateTime = LocalDateTime.of(year, month+1, dayOfMonth, 0, 0)
        viewModel.dateEpoch = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}