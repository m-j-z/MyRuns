package com.michael_zhu.myruns.ui.start.map

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.michael_zhu.myruns.R

class TrackingService : Service(), LocationListener {
    private lateinit var trackingBinder: TrackingBinder
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return START_NOT_STICKY
    }

    private fun start() {
        initializeLocationManager()
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            onLocationChanged(location)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    private fun sendNotification() {
        val intent = Intent(this, MapsDisplayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("MyRuns: Tracking Location")
            setContentText("MyRuns is currently tracking your location.")
            setSmallIcon(R.drawable.gps_icon)
            setOngoing(true)
            setContentIntent(pendingIntent)
        }

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    override fun onBind(intent: Intent): IBinder {
        sendNotification()
        trackingBinder = TrackingBinder()
        return trackingBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        locationManager.removeUpdates(this)
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
        stopSelf()
    }

    inner class TrackingBinder : Binder() {
        fun setLocationHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }
    }

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_NAME = "notification"
        const val NOTIFICATION_ID = 668439

        const val BUNDLE_NAME_LOCATION = "location"
        const val MSG_IDENTIFIER = 56228466
    }


}