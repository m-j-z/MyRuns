package com.michael_zhu.myruns.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.michael_zhu.myruns.R
import com.michael_zhu.myruns.misc.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {
    private val userSettingsName = "user_profile"
    private val imgFileName = "profile_img.jpg"
    private val tmpFileName = "tmp.jpg"
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private var profileChanged: Boolean = false

    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    private lateinit var tmpUri: Uri

    private lateinit var imageView: ImageView
    private lateinit var profileBtn: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var classNumberEditText: EditText
    private lateinit var majorEditText: EditText
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                profileChanged = true
                val bitmap = Utility.getBitmap(this, tmpUri, tmpFileName)
                imageView.setImageBitmap(bitmap)
            }
        }
        galleryResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    profileChanged = true
                    val bitmap = Utility.getBitmap(this, it.data!!.data!!, tmpFileName)
                    imageView.setImageBitmap(bitmap)

                    CoroutineScope(IO).launch {
                        withContext(IO) {
                            val tmpFile = File(getExternalFilesDir(null), tmpFileName)
                            if (!tmpFile.exists()) {
                                tmpFile.createNewFile()
                            }
                            val fileOutputStream = FileOutputStream(tmpFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        }
                    }
                }
            }

        val tmpFile = File(getExternalFilesDir(null), tmpFileName)
        val imgFile = File(getExternalFilesDir(null), imgFileName)
        if (!tmpFile.exists()) {
            tmpFile.createNewFile()
            if (imgFile.exists()) {
                imgFile.copyTo(tmpFile, true)
            }
        }
        tmpUri = FileProvider.getUriForFile(this, "com.michael_zhu.myruns", tmpFile)

        imageView = findViewById(R.id.profile_picture)
        profileBtn = findViewById(R.id.profile_picture_btn)
        nameEditText = findViewById(R.id.name_input)
        emailEditText = findViewById(R.id.email_input)
        phoneNumberEditText = findViewById(R.id.phone_number_input)
        genderRadioGroup = findViewById(R.id.gender_label)
        classNumberEditText = findViewById(R.id.class_input)
        majorEditText = findViewById(R.id.major_input)
        cancelBtn = findViewById(R.id.cancel_btn)
        saveBtn = findViewById(R.id.save_btn)

        cancelBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        profileBtn.setOnClickListener(this)

        loadSettings()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel_btn -> {
                loadSettings()
                Toast.makeText(this, "Changes discarded!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.save_btn -> {
                onClickSaveBtn()
                Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.profile_picture_btn -> {
                onClickProfileBtn()
            }
        }
    }

    private fun onClickProfileBtn() {
        if (Utility.checkPermissions(this)) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setItems(R.array.profile_choices) { _: DialogInterface, i: Int ->
                    when (i) {
                        0 -> launchCamera()
                        1 -> getFromGallery()
                    }
                }
            }.show()
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun getFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        galleryResult.launch(intent)
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri)
        cameraResult.launch(intent)
    }

    private fun onClickSaveBtn() {
        if (profileChanged) {
            val tmpFile = File(getExternalFilesDir(null), tmpFileName)
            if (tmpFile.exists()) {
                val imgFile = File(getExternalFilesDir(null), imgFileName)
                if (!imgFile.exists()) {
                    imgFile.createNewFile()
                }
                tmpFile.copyTo(imgFile, true)
                profileChanged = false
            }
        }

        val editor = getSharedPreferences(userSettingsName, Context.MODE_PRIVATE).edit()
        var classNum = -1
        if (classNumberEditText.text.toString() != "") {
            classNum = classNumberEditText.text.toString().toInt()
        }

        editor.putString("name", nameEditText.text.toString())
        editor.putString("email", emailEditText.text.toString())
        editor.putString("phone_number", phoneNumberEditText.text.toString())
        editor.putInt("gender", genderRadioGroup.checkedRadioButtonId)
        editor.putInt("class", classNum)
        editor.putString("major", majorEditText.text.toString())

        editor.apply()
    }

    private fun loadSettings() {
        val profileImageFile = File(getExternalFilesDir(null), tmpFileName)
        val imgUri = FileProvider.getUriForFile(this, "com.michael_zhu.myruns", profileImageFile)
        if (profileImageFile.exists()) {
            val bitmap = Utility.getBitmap(this, imgUri, tmpFileName)
            imageView.setImageBitmap(bitmap)
        }

        val settings = getSharedPreferences(userSettingsName, Context.MODE_PRIVATE)

        var classNum = ""
        if (settings.getInt("class", -1) != -1) {
            classNum = settings.getInt("class", -1).toString()
        }

        nameEditText.setText(settings.getString("name", ""))
        emailEditText.setText(settings.getString("email", ""))
        phoneNumberEditText.setText(settings.getString("phone_number", ""))
        genderRadioGroup.check(settings.getInt("gender", 0))
        classNumberEditText.setText(classNum)
        majorEditText.setText(settings.getString("major", ""))
    }
}