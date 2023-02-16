package com.example.tellstory.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tellstory.R
import com.example.tellstory.common.ViewModelFactories
import com.example.tellstory.common.wrapEspressoIdlingResource
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.databinding.ActivityMainBinding
import com.example.tellstory.ui.auth.LoginActivity
import com.example.tellstory.ui.detail.DetailsActivity
import com.example.tellstory.ui.newstory.AddNewStoryActivity
import com.example.tellstory.ui.profile.UserProfileActivity
import com.example.tellstory.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactories.getInstance(application)
    }

    private lateinit var storyAdapter: StoryAdapter
    private var isUploaded: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()


        mainViewModel.loading.observe(this) {
            showLoading(it)
        }

        //region observe logout status
        mainViewModel.signOut.observe(this) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finishAffinity()
        }

        binding.cvExit.setOnClickListener {
            mainViewModel.signOut()
        }
        //endregion

        setupAdapterData()
        setupView()

        //welcome user
        mainViewModel.getTheName()
        mainViewModel.welcomeUser.observe(this) {
            Log.d(TAG, "log welcome : $it")
            Toast.makeText(this, "Welcome $it", Toast.LENGTH_SHORT).show()
        }


        //to user profile
        binding.apply {
            val getData = intent.getStringExtra(MAIN_EXTRA)
            cardViewMain.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,
                        Pair(cardViewMain, getString(R.string.photo_profile)),
                        Pair(tvAppName, getString(R.string.user_name))
                    )
                startActivity(
                    Intent(this@MainActivity, UserProfileActivity::class.java).also {
                        it.putExtra(UserProfileActivity.USER_PROFILE_EXTRA, getData)
                    },
                    optionsCompat.toBundle(),
                )
            }

        }

        //region activityResult add new story
        val newStoryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    isUploaded = it.data?.getBooleanExtra(
                        AddNewStoryActivity.IS_NEW_STORY_EXTRA_UPLOADED,
                        false
                    )
                    if (isUploaded == true) {
                        storyAdapter.refresh()
                        binding.rvMain.smoothScrollToPosition(0)
                    }
                }
            }
        //insyaalloh g crash
        binding.fabMain.setOnClickListener {
            newStoryLauncher.launch(Intent(this@MainActivity, AddNewStoryActivity::class.java))
            Log.d(TAG, "testing Fab")
        }
        //endregion

    }

    private fun setupView() {
        storyAdapter = StoryAdapter().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    if (positionStart == 0) {
                        binding.rvMain.smoothScrollToPosition(0)
                    }
                }
            })
        }

        binding.apply {
            rvMain.layoutManager = GridLayoutManager(this@MainActivity, 2)
            rvMain.setHasFixedSize(true)
            rvMain.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }

        storyAdapter.refresh()

        wrapEspressoIdlingResource {
            lifecycleScope.launch {
                storyAdapter.loadStateFlow.collect {
                    binding.progressMain.isVisible = it.refresh is LoadState.Loading
                    if (it.refresh is LoadState.Error) {
                        Snackbar.make(
                            window.decorView.rootView,
                            "Error when load stories",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupAdapterData() {
        mainViewModel.listStories.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)

            //to details activity
            storyAdapter.setOnItemClickCallBack(
                object : StoryAdapter.OnItemClickCallback {
                    override fun onItemClicked(itemClick: MainStory) {
                        val detail = Intent(this@MainActivity, DetailsActivity::class.java)
                        detail.putExtra(DetailsActivity.DETAILS_EXTRA_WITH_ID, itemClick.id)
                        startActivity(detail)
                    }
                }
            )
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressMain.visibility = View.VISIBLE
            binding.rvMain.visibility = View.INVISIBLE
        } else {
            binding.progressMain.visibility = View.GONE
            binding.rvMain.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.are_you_sure)
        builder.setPositiveButton(R.string.yes_dialog) { _, _ -> finishAffinity() }
        builder.setNegativeButton(R.string.no_dialog) { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        const val MAIN_EXTRA = "main_activity"
    }

}