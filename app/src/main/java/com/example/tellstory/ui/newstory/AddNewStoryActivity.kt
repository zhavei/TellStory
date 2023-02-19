package com.example.tellstory.ui.newstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
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
import com.example.tellstory.ui.maps.MapsActivity
import com.example.tellstory.ui.viewmodel.AddNewStoryViewModel
import com.google.android.gms.maps.model.LatLng
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
        const val IS_NEW_STORY_EXTRA_UPLOADED = "Add_New_Story_Activity"
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
                openCamera()
            }
            btnGalery.setOnClickListener {
                openGallery()
            }
            btnPost.setOnClickListener {
                postNewStory()
            }

            binding.ivPickLocation.setOnClickListener {
                shouldProvideLocation = !shouldProvideLocation
                if (shouldProvideLocation) {
                    binding.apply {
                        etInputLocation.visibility = View.VISIBLE
                        tvPlease.visibility = View.GONE
                        ivCloseLocation.visibility = View.VISIBLE
                    }
                } else {
                    selectedLocationLatLng = null
                    binding.apply {
                        etInputLocation.text = null
                        etInputLocation.visibility = View.GONE
                        tvPlease.visibility = View.VISIBLE
                        ivCloseLocation.visibility = View.GONE
                    }
                }
            }

            binding.etInputLocation.setOnClickListener {
                val intent = Intent(this@AddNewStoryActivity, MapsActivity::class.java)
                intent.putExtra(MapsActivity.MAPS_PICKED_LATLON, true)
                locationLauncher.launch(intent)
            }

            binding.ivCloseLocation.setOnClickListener {
                selectedLocationLatLng = null
                with(binding) {
                    etInputLocation.text = null
                }
            }

        }

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun postNewStory() {
        with(binding) {
            if (etDescription.text.isNullOrEmpty() || selectedPhotoFile == null) {
                etDescription.error = getString(R.string.empty_desc)
                return
            }
            val reducedImage = reduceFileImage(selectedPhotoFile!!)
            addNewStoryViewModel.postNewStory(
                file = reducedImage,
                lat = selectedLocationLatLng?.latitude?.toFloat(),
                lon = selectedLocationLatLng?.longitude?.toFloat(),
                desc = etDescription.text?.trim().toString()
            )
        }
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddNewStoryActivity,
                "com.example.tellstory",
                it
            )
            selectedPhotoFilePath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun openGallery() {
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

            selectedPhotoFile = uriToFile(selectedImg, this@AddNewStoryActivity)
            binding.imageViewHolder.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            selectedPhotoFile = selectedPhotoFilePath?.let { selected ->
                File(selected)
            }
            val selectedFile = BitmapFactory.decodeFile(selectedPhotoFile?.path)
            val rotate = rotateBitmap(selectedFile, isBackCamera = true)
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
                    binding.etInputLocation.setText(locationAddress)
                }
            }
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

    override fun onResume() {
        super.onResume()
        if (selectedPhotoFile != null) binding.imageViewHolder.setImageURI(
            Uri.fromFile(
                selectedPhotoFile
            )
        )
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
            intent.putExtra(IS_NEW_STORY_EXTRA_UPLOADED, isStoryUploaded)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedCallback.handleOnBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}