package com.adhi.githubuser.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.adhi.githubuser.data.local.entity.DetailUserEntity
import com.adhi.githubuser.databinding.ItemsUserBinding
import com.adhi.githubuser.utils.ImageLoader.loadImage
import com.adhi.githubuser.utils.TextLoader.loadData

class FavoriteAdapter(private val callback: OnUserFavCallback)
    : ListAdapter<DetailUserEntity, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): FavoriteViewHolder {
        val binding = ItemsUserBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class FavoriteViewHolder(private val binding: ItemsUserBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: DetailUserEntity) {
            binding.apply {
                tvName.loadData(user.login)
                tvType.loadData(user.type)
                ivUser.loadImage(itemView.context, user.avatarUrl,
                    CenterCrop(), RoundedCorners(16))
                itemView.setOnClickListener { callback.onItemClicked(user) }
            }
        }
    }

    interface OnUserFavCallback {
        fun onItemClicked(user: DetailUserEntity)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailUserEntity>() {
            override fun areItemsTheSame(
                oldItem: DetailUserEntity,
                newItem: DetailUserEntity
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DetailUserEntity,
                newItem: DetailUserEntity
            ): Boolean =
                when {
                    oldItem.id != newItem.id -> false
                    oldItem.avatarUrl != newItem.avatarUrl -> false
                    oldItem.login != newItem.login -> false
                    else -> true
                }
        }
    }
}