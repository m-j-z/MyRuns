package com.michael_zhu.myruns.ui.start.map

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.michael_zhu.myruns.R
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.sqrt

class TrackingService : Service(), LocationListener, SensorEventListener {
    private lateinit var trackingBinder: TrackingBinder
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null

    // Sensors
    private lateinit var sensorManager: SensorManager
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private lateinit var job: Job

    private lateinit var intent: Intent

    override fun onCreate() {
        super.onCreate()
        intent = Intent(applicationContext, MapsDisplayActivity::class.java)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        mAccBuffer = ArrayBlockingQueue<Double>(ACCELEROMETER_BLOCK_CAPACITY)
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
        startSensorCoroutine()
    }

    /**
     * Creates coroutine that calculates p.
     * Sends p back to MapsDisplayActivity.
     */
    private fun startSensorCoroutine() {
        job = CoroutineScope(IO).launch {
            var blockSize = 0
            val fft = FFT(ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
            var max: Double
            while (isActive) {
                try {
                    accBlock[blockSize++] =
                        withContext(IO) {
                            mAccBuffer.take()
                        }.toDouble()
                    if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        val inst: ArrayList<Double> = ArrayList()
                        for (i in accBlock.indices) {
                            val mag = sqrt(
                                accBlock[i] * accBlock[i] + im[i] * im[i]
                            )
                            inst.add(mag)
                            im[i] = .0
                        }
                        inst.add(ACCELEROMETER_BLOCK_CAPACITY, max)
                        val p = WekaClassifier.classify(inst.toArray())

                        // Put value into bundle and pass back to MapsDisplayActivity
                        val bundle = Bundle()
                        bundle.putDouble(BUNDLE_NAME_SENSOR, p)
                        if (msgHandler != null) {
                            val message = msgHandler!!.obtainMessage()
                            message.data = bundle
                            message.what = MSG_ID_SENSOR
                            msgHandler!!.sendMessage(message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Initialize Location Manager.
     * Get last known location.
     * Request location updates.
     */
    @SuppressLint("MissingPermission")
    private fun initializeLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val provider = LocationManager.GPS_PROVIDER

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
        sensorManager.unregisterListener(this)
        job.cancel()
        stopSelf()
    }

    inner class TrackingBinder : Binder() {
        private val service: TrackingService get() = this@TrackingService

        fun setLocationHandler(msgHandler: Handler) {
            service.msgHandler = msgHandler
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) return

        x = (event.values[0]).toDouble()
        y = (event.values[1]).toDouble()
        z = (event.values[2]).toDouble()

        val m = sqrt(x * x + y * y + z * z)

        try {
            mAccBuffer.add(m)
        } catch (e: IllegalStateException) {
            val newBuffer = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
            mAccBuffer.drainTo(newBuffer)
            mAccBuffer = newBuffer
            mAccBuffer.add(m)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_NAME = "notification"
        const val NOTIFICATION_ID = 668439

        const val BUNDLE_NAME_LOCATION = "location"
        const val MSG_IDENTIFIER = 56228466

        const val ACCELEROMETER_BLOCK_CAPACITY = 64
        const val BUNDLE_NAME_SENSOR = "sensor"
        const val MSG_ID_SENSOR = 73661
    }
}