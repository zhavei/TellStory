package com.example.tellstory.ui.newstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tellstory.databinding.ActivityAddNewStoryBinding


class AddNewStoryActivity : AppCompatActivity() {

    private val binding by lazy {
       ActivityAddNewStoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




    }
}