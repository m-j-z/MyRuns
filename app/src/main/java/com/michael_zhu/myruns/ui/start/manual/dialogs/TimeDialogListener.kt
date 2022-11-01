package com.michael_zhu.myruns.ui.start.manual.dialogs

import android.app.TimePickerDialog
import android.os.Parcel
import android.os.Parcelable
import android.widget.TimePicker
import com.michael_zhu.myruns.ui.start.manual.ManualInputViewModel

class TimeDialogListener() : TimePickerDialog.OnTimeSetListener, Parcelable {
    lateinit var viewModel: ManualInputViewModel

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeDialogListener> {
        override fun createFromParcel(parcel: Parcel): TimeDialogListener {
            return TimeDialogListener(parcel)
        }

        override fun newArray(size: Int): Array<TimeDialogListener?> {
            return arrayOfNulls(size)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.hourOfDay = hourOfDay
        viewModel.minute = minute

        viewModel.timeEpoch = (hourOfDay * 3600 + minute * 60).toLong()
    }


}