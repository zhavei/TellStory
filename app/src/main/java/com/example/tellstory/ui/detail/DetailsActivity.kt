package com.example.tellstory.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.databinding.ActivityDetailsBinding
import com.example.tellstory.ui.viewmodel.DetailsViewModel

class DetailsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    private val detailsViewModel: DetailsViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }

    private var storyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storyId = intent.getStringExtra(DETAILS_EXTRA)
        Log.d(TAG, "check user ID: $storyId")

        //region check status
        loadingStatus()
        detailsViewModel.statusMessage.observe(this) { statusMessageError ->
            Toast.makeText(this, statusMessageError, Toast.LENGTH_SHORT).show()
        }
        //endregion

        setData()
        storyId?.let { detailsViewModel.fetchStoryDetails(it) }

    }

    private fun setData() {

        detailsViewModel.storyDetails.observe(this) { setData ->
            binding.apply {
                tvNameDetail.text = setData.name
                tvDescDetail.text = setData.description
                Glide.with(this@DetailsActivity).load(setData?.photoUrl).centerCrop()
                    .into(ivDetails)
            }

        }

    }

    private fun loadingStatus() {
        detailsViewModel.loadingStatus.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.cardView.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.cardView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()

        return super.onSupportNavigateUp()
    }


    companion object {
        const val DETAILS_EXTRA = "detail_activity"
        private val TAG = DetailsActivity::class.simpleName
    }
}