package com.example.tellstory.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.coredata.remote.ListStoryItems
import com.example.tellstory.databinding.ActivityDetailsBinding
import com.example.tellstory.ui.viewmodel.DetailsViewModel
import com.example.tellstory.ui.viewmodel.MainViewModel

class DetailsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    private val detailsViewModel: DetailsViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val getData = intent.getStringExtra(DETAILS_EXTRA)


        binding.apply {
            tvNameDetail.text = getData?.name
            tvDescDetail.text = getData?.description
            Glide.with(this@DetailsActivity).load(getData?.photoUrl).centerCrop().into(ivDetails)
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()

        return super.onSupportNavigateUp()
    }


    companion object {
        const val DETAILS_EXTRA = "detail_activity"
    }
}