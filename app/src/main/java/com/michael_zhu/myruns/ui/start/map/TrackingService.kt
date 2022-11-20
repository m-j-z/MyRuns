package com.michael_zhu.myruns.ui.start.map

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Criteria
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

    private lateinit var intent: Intent

    override fun onCreate() {
        super.onCreate()
        intent = Intent(applicationContext, MapsDisplayActivity::class.java)
        sendNotification()
    }

    /**
     * On StartService
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return START_NOT_STICKY
    }

    /**
     * Send notification.
     * Initialize Location Manager.
     */
    private fun start() {
        initializeLocationManager()
    }

    /**
     * Initialize Location Manager.
     * Get last known location.
     * Request location updates.
     */
    @SuppressLint("MissingPermission")
    private fun initializeLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val criteria = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
            powerRequirement = Criteria.POWER_HIGH
            horizontalAccuracy = Criteria.ACCURACY_HIGH
            verticalAccuracy = Criteria.ACCURACY_HIGH
        }
        var provider = locationManager.getBestProvider(criteria, true)
        if (provider == null) provider = LocationManager.GPS_PROVIDER

        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) {
            onLocationChanged(location)
        }

        locationManager.removeUpdates(this)
        locationManager.requestLocationUpdates(provider, 0, 0f, this)
    }

    /**
     * Create notification.
     * Create notification channel.
     */
    private fun sendNotification() {
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setCategory(NotificationCompat.CATEGORY_SERVICE)
            priority = NotificationCompat.PRIORITY_DEFAULT
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
        startForeground(NOTIFICATION_ID, notification.build())
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    /**
     * On Bind, create TrackingBinder.
     */
    override fun onBind(intent: Intent): IBinder {
        trackingBinder = TrackingBinder()
        return trackingBinder
    }

    /**
     * On Unbind, set [msgHandler] and cancel notification and remove updates for location manager.
     */
    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        locationManager.removeUpdates(this)
        return super.onUnbind(intent)
    }

    /**
     * Stop service onTaskRemoved.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    /**
     * On location changed, send message to binder.
     */
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

    /**
     * OnDestroy, set [msgHandler] and cancel notification and remove updates for location manager.
     * Stop service.
     */

    override fun onDestroy() {
        super.onDestroy()
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        locationManager.removeUpdates(this)
        stopSelf()
    }

    inner class TrackingBinder : Binder() {
        private val service: TrackingService get() = this@TrackingService

        fun setLocationHandler(msgHandler: Handler) {
            service.msgHandler = msgHandler
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