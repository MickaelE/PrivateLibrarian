package com.mickenet.privatelibrarian.books

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mickenet.privatelibrarian.MyAppGlideModule
import com.mickenet.privatelibrarian.R
import kotlinx.android.synthetic.main.item_books.view.*

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
            itemView.tvTitle.text = item.title
            itemView.tvAuthor.text = item.author
            Glide.with(this).load(item.coverUrl).into(itemView.ivBookCover);
            setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    item.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
       return oldItem.openLibraryId == newItem.openLibraryId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }

}