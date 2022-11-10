package com.michael_zhu.myruns.misc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.util.*

object Utility {

    /**
     * Checks whether the permission required for the application was granted.
     */
    fun checkPermissions(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    /**
     * Returns the bitmap as given by [imgUri] and saves it to [imageFileName].
     */
    fun getBitmap(context: Context, imgUri: Uri, imageFileName: String): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(getRotation(context, imgUri, imageFileName))
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Automatically rotates the picture.
     */
    private fun getRotation(context: Context, imageUri: Uri, imageFileName: String): Float {
        var rotate = 0
        context.contentResolver.notifyChange(imageUri, null)
        val imageFile = File(context.getExternalFilesDir(null), imageFileName)
        if (!imageFile.exists()) {
            imageFile.createNewFile()
        }
        val exifInterface = ExifInterface(imageFile.absolutePath)
        when (exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
        }
        return rotate.toFloat()
    }

    /**
     * Converts [num] from [savedAsUnits] to [currentUnits].
     */
    fun convertUnits(currentUnits: String, savedAsUnits: String, num: Double): Double {
        if (currentUnits == savedAsUnits) {
            return roundToDecimalPlaces(num, 5)
        }

        if (currentUnits == "km" && savedAsUnits == "mi") {
            return roundToDecimalPlaces(num * 1.609, 5)
        }

        return roundToDecimalPlaces(num / 1.609, 5)
    }

    /**
     * Converts [long] to a date.
     */
    fun longToDate(long: Long): String? {
        val date = Date(long)
        val format = SimpleDateFormat("MMM dd yyyy", Locale.CANADA)
        return format.format(date)
    }

    /**
     * Converts [seconds] to a time.
     */
    fun longToTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    /**
     * Converts [seconds] to a time.
     */
    fun longToDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d hours %02d minutes %02d seconds".format(h, m, s)
    }

    /**
     * Rounds and returns [num] to [numOfDecimalPlaces].
     */
    fun roundToDecimalPlaces(num: Double, numOfDecimalPlaces: Int = 1): Double {
        var tens = 1.0
        for (i in 1..numOfDecimalPlaces) {
            tens *= 10.0
        }
        return (num * tens).toInt() / tens
    }
}