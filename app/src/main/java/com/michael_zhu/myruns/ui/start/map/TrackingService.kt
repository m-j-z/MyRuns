package com.michael_zhu.myruns.ui.start.map

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.michael_zhu.myruns.R

class TrackingService : Service(), LocationListener {
    private lateinit var trackingBinder: TrackingBinder
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null

    override fun onCreate() {
        super.onCreate()
        trackingBinder = TrackingBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        sendNotification()
        initializeLocationManager()
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    private fun sendNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("MyRuns: Tracking Location")
            setContentText("MyRuns is currently tracking your location.")
            setSmallIcon(R.drawable.gps_icon)
            setOngoing(true)
        }

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    override fun onBind(intent: Intent): IBinder {
        return trackingBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onLocationChanged(location: Location) {
        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_NAME_LOCATION, location)
        if (msgHandler != null) {
            val message = msgHandler!!.obtainMessage()
            message.data = bundle
            message.what = MSG_IDENTIFIER
            msgHandler!!.sendMessage(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NOTIFICATION_ID)
        locationManager.removeUpdates(this)
    }

    inner class TrackingBinder : Binder() {
        fun setLocationHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }
    }

    companion object {
        const val START = "START"

        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_NAME = "notification"
        const val NOTIFICATION_ID = 668439

        const val BUNDLE_NAME_LOCATION = "location"
        const val MSG_IDENTIFIER = 56228466
    }


}