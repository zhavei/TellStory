package com.example.tellstory.ui.newstory

import android.Manifest
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.tellstory.R
import com.example.tellstory.common.*
import com.example.tellstory.databinding.ActivityAddNewStoryBinding
import com.example.tellstory.ui.MapsActivity
import com.example.tellstory.ui.viewmodel.AddNewStoryViewModel
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddNewStoryActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    private val addNewStoryViewModel: AddNewStoryViewModel by viewModels() {
        ViewModelFactories.getInstance(application)
    }

    private var selectedPhotoFile: File? = null
    private var selectedPhotoFilePath: String? = null
    private var isStoryUploaded: Boolean = false
    private var selectedLocationLatLng: LatLng? = null
    private var shouldProvideLocation: Boolean = false


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val TAG = AddNewStoryActivity::class.java.simpleName
        const val ADD_NEW_STORY_EXTRA_UPLOADED = "Add_New_Story_Activity"
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

        addNewStoryViewModel.apply {
            loadingStatus.observe(this@AddNewStoryActivity) {
                showLoading(it)
            }
            responseMessage.observe(this@AddNewStoryActivity) {
                Toast.makeText(this@AddNewStoryActivity, it, Toast.LENGTH_SHORT).show()
            }
            statusUploaded.observe(this@AddNewStoryActivity) {
                binding.etDescription.text?.clear()
                binding.imageViewHolder.setImageURI(null)
                binding.imageViewHolder.setImageBitmap(null)
                isStoryUploaded = true
                onBackPressedCallback.handleOnBackPressed()
            }
        }

        //add new story process
        binding.apply {
            btnCamera.setOnClickListener {
                startCamera()
            }
            btnGalery.setOnClickListener {
                startGalery()
            }
            btnPost.setOnClickListener {
                postNewStory()
            }

            binding.ivPickLocation.setOnClickListener {
                shouldProvideLocation = !shouldProvideLocation
                if (shouldProvideLocation) {
                    val intent = Intent(this@AddNewStoryActivity, MapsActivity::class.java)
                    intent.putExtra(MapsActivity.MAPS_EXTRA, true)
                    locationLauncher.launch(intent)
                } else {
                    selectedLocationLatLng = null
                    with(binding) {
                        etPickLocation.text = null
                    }
                }
                /**
                 * baru sampe sini oke
                 */

            }

        }

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun postNewStory() {
        binding.apply {
            val description = etDescription.text
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
                        binding.etDescription.text.toString()
                            .toRequestBody("text/plain".toMediaType())
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
                        responses.observe(this@AddNewStoryActivity) { getRespone ->
                            showRespones(getRespone)
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

    private val locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val locationDataMarker =
                    it.data?.extras?.getParcelable(MapsActivity.MAPS_LOCATION) as LatLng?
                val locationAddress =
                    it.data?.extras?.getString(MapsActivity.MAPS_ADDRESS)
                if (locationDataMarker != null) {
                    selectedLocationLatLng = locationDataMarker
                    binding.etPickLocation.setText(locationAddress)
                }
            }
        }

    private fun showStatus(statusSuccess: Boolean) {
        if (statusSuccess) {
            finish()
        } else {
            Toast.makeText(this, "Failed Posted Stories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRespones(response: String) {
        Toast.makeText(this@AddNewStoryActivity, response.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingProgress.visibility = View.VISIBLE
            binding.btnPost.visibility = View.GONE
        } else {
            binding.loadingProgress.visibility = View.GONE
            binding.btnPost.visibility = View.VISIBLE
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

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intent.putExtra(ADD_NEW_STORY_EXTRA_UPLOADED, isStoryUploaded)
            setResult(RESULT_OK, intent)
            finish()
        }
    }


}