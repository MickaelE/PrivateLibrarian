package com.mickenet.privateLibrarian.Books

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mickenet.privateLibrarian.R

class BookAdapter : ListAdapter<Book, BookAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_books, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Book) = with(itemView) {
            // TODO: Bind the data with View

            setOnClickListener {
                // TODO: Handle on click
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Book>() {

    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem?.openLibraryId == newItem?.openLibraryId
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.equals(newItem)
    }
}