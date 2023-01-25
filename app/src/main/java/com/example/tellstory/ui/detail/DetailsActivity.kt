package com.example.tellstory.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.coredata.remote.ListStoryItems
import com.example.tellstory.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*val getData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DETAILS_EXTRA, ListStoryItems::class.java)
        } else {
            intent.getParcelableExtra<ListStoryItems>(EXTRA_DATA)
        }*/
        val getData = intent.getParcelableExtra<ListStoryItems>(DETAILS_EXTRA)

        binding.apply {
            tvNameDetail.text = getData?.name
        }


    }


    companion object {
        const val DETAILS_EXTRA = "detail_activity"
    }
}