package com.example.tellstory.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tellstory.R
import com.example.tellstory.coredata.model.MainStory
import com.example.tellstory.databinding.ItemListStoriesBinding

class StoryAdapter : PagingDataAdapter<MainStory, StoryAdapter.StoryViewHolders>(DIFFUTILS) {

    var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private object DIFFUTILS : DiffUtil.ItemCallback<MainStory>() {
        // DiffUtil uses this test to help discover if an item was added, removed, or moved.
        override fun areItemsTheSame(oldItem: MainStory, newItem: MainStory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MainStory, newItem: MainStory): Boolean {
            return oldItem == newItem
        }
    }

    inner class StoryViewHolders(var binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolders {
        return StoryViewHolders(
            ItemListStoriesBinding.inflate(
                LayoutInflater.from(
                    parent
                        .context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolders, position: Int) {
        val itemPosition = getItem(position)

        holder.binding.apply {
            ivTitle.text = itemPosition?.name
            Glide.with(holder.itemView.context)
                .load(itemPosition?.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(
                        R.drawable.ic_baseline_refresh_24
                    ).error(R.drawable.ic_baseline_broken_image_24)
                ).into(ivImage)
        }

        holder.itemView.setOnClickListener {
            if (itemPosition != null) {
                onItemClickCallback?.onItemClicked(itemPosition)
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(itemClick: MainStory)
    }

}