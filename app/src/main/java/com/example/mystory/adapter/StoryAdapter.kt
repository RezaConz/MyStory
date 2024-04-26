package com.example.mystory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystory.R
import com.example.mystory.data.remote.response.ListStoryItem
import com.example.mystory.databinding.StoryRowItemBinding
import com.example.mystory.util.DateFormat
import java.util.TimeZone

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(diffCallback) {

    var onClick: ((ListStoryItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user!!, onClick)
    }

    class ListViewHolder(private var binding: StoryRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem,onClick: ((ListStoryItem) -> Unit)?) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .error(R.drawable.shiba_vector)
                    .into(ivPhoto)
                if(story.createdAt != null) {
                    val datePosted = DateFormat.formatDate(story.createdAt.toString(), TimeZone.getDefault().id)
                    tvDatePosted.text = itemView.context.getString(R.string.created_at, datePosted)
                } else {
                    tvDatePosted.visibility = View.GONE
                }
                tvItemName.text = story.name
                tvItemDescription.text = story.description

                itemView.setOnClickListener {
                    onClick?.invoke(story)
                }
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}