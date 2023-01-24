package com.example.tellstory.ui.newstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tellstory.R
import com.example.tellstory.common.*
import com.example.tellstory.databinding.ActivityAddNewStoryBinding
import com.example.tellstory.ui.viewmodel.AddNewStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
class AddNewStoryActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    private val addNewStoryViewModel: AddNewStoryViewModel by viewModels() {
        ViewModelFactory(UserDataPreferences.getInstance(userDataStore))
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var token: String? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        hideSystemUI()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }
        binding.btnGalery.setOnClickListener {
            startGalery()
        }
        binding.btnPost.setOnClickListener {
            postNewStory()
        }

    }

    private fun postNewStory() {
        binding.apply {
            val description = etDescription.text.toString().trim()
            when {
                description.isEmpty() -> {
                    etDescription.error = getString(R.string.empty_desc)
                }
                getFile == null -> {
                    Toast.makeText(
                        this@AddNewStoryActivity,
                        getString(R.string.take_your_picture),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                else -> {
                    val description =
                        etDescription.text.toString().toRequestBody("text/plain".toMediaType())
                    val reduceFile = reduceFileImage(getFile as File)
                    val imageFile = reduceFile.asRequestBody()
                    val multiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        reduceFile.name,
                        imageFile
                    )

                    //region post to it

                    addNewStoryViewModel.apply {
                        getUser().observe(this@AddNewStoryActivity) { user ->
                            token = user.userToken
                        }
                        token?.let { postNewStory(multiPart, description, it) }

                        loading.observe(this@AddNewStoryActivity) { show ->
                            showLoading(show)
                        }
                        status.observe(this@AddNewStoryActivity) { success ->
                            showStatus(success)
                        }
                    }

                    //endregion

                }


            }
        }

    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddNewStoryActivity,
                "com.example.tellstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGalery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddNewStoryActivity)

            getFile = myFile

            binding.imageViewHolder.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            val rotate = rotateBitmap(result, isBackCamera = true)
            binding.imageViewHolder.setImageBitmap(rotate)
        }
    }

    private fun showStatus(statusSuccess: Boolean) {
        if (statusSuccess) {
            finish()
        } else {
            Toast.makeText(this, "Failed Posted Stories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                loadingProgress.visibility = View.VISIBLE
                btnPost.visibility = View.GONE
            }
        } else {
            binding.apply {
                loadingProgress.visibility = View.GONE
                btnPost.visibility = View.VISIBLE
            }
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}