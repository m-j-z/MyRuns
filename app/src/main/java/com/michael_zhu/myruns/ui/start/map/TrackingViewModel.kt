package com.michael_zhu.myruns.ui.start.map

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackingViewModel : ViewModel(), ServiceConnection {
    private var trackingHandler: TrackingHandler = TrackingHandler(Looper.getMainLooper())

    private var _latLngList: ArrayList<Location> = ArrayList()
    var latLngList: MutableLiveData<ArrayList<Location>> = MutableLiveData<ArrayList<Location>>(
        arrayListOf()
    )
    val activityTypeList: ArrayList<Double> = ArrayList()
    val activityType: MutableLiveData<Double> = MutableLiveData()

    /**
     * Add [location] to [latLngList].
     */
    fun add(location: Location) {
        _latLngList = latLngList.value!!
        _latLngList.add(location)
        latLngList.postValue(_latLngList)
    }

    /**
     * Return [latLngList].
     */
    fun getAll(): ArrayList<Location> {
        _latLngList = latLngList.value!!
        return _latLngList
    }

    /**
     * On service connection, bind handler.
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("Location Service", "Service connected.")
        val binder = service as TrackingService.TrackingBinder
        binder.setLocationHandler(trackingHandler)
    }

    /**
     * On service disconnection.
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("LocationService", "Service disconnected.")
        return
    }

    inner class TrackingHandler(looper: Looper) : Handler(looper) {
        @Suppress("DEPRECATION")
        override fun handleMessage(msg: Message) {
            if (msg.what == TrackingService.MSG_IDENTIFIER) {
                val bundle = msg.data
                val location: Location =
                    bundle.getParcelable(TrackingService.BUNDLE_NAME_LOCATION) ?: return
                add(location)
            }
            if (msg.what == TrackingService.MSG_ID_SENSOR) {
                val bundle = msg.data
                val p = bundle.getDouble(TrackingService.BUNDLE_NAME_SENSOR, -1.0)
                activityTypeList.add(p)
                activityType.postValue(p)
            }
        }
    }

}