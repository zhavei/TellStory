package com.example.tellstory.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tellstory.R
import com.example.tellstory.coredata.remote.ListStoryItems
import com.example.tellstory.databinding.ItemListStoriesBinding

class StoryAdapter : ListAdapter<ListStoryItems, StoryAdapter.StoryViewHolders>(DIFFUTILS) {

    var onItemClickCallback: OnItemClickcallback? = null

    private object DIFFUTILS : DiffUtil.ItemCallback<ListStoryItems>() {
        // DiffUtil uses this test to help discover if an item was added, removed, or moved.
        override fun areItemsTheSame(oldItem: ListStoryItems, newItem: ListStoryItems): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItems, newItem: ListStoryItems): Boolean {
            return oldItem == newItem
        }

    }

    class StoryViewHolders(private val binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storyUser: ListStoryItems, itemClickcallback: () -> Unit) {
            binding.apply {
                ivTitle.text = storyUser.name
                Glide.with(itemView.context)
                    .load(storyUser.photoUrl)
                    .apply(
                        RequestOptions.placeholderOf(
                            R.drawable.ic_baseline_refresh_24
                        ).error(R.drawable.ic_baseline_broken_image_24)
                    ).into(ivImage)
            }
            itemView.setOnClickListener {
                itemClickcallback.invoke()

            }
        }

    }

    interface OnItemClickcallback {
        fun onItemClicked(itemClick: ListStoryItems)
    }

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
        val stories = getItem(position)
        holder.bind(stories) {
            onItemClickCallback?.onItemClicked(stories)
        }
    }

    override fun getItemCount() = currentList.size

}