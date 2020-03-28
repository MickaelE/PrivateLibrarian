package com.mickenet.privatelibrarian.books

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mickenet.privatelibrarian.R
import com.mickenet.privatelibrarian.database.DatabaseHandler
import kotlinx.android.synthetic.main.item_books.view.*


class BookAdapter : ListAdapter<LocalBook, BookAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_books, parent, false)

        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position))

    }
    override fun getItemId(position: Int): Long = position.toLong()
    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        fun bind(item: LocalBook) = with(itemView) {
            itemView.tvTitle.text = item.title
            itemView.tvAuthor.text = item.author
            Glide.with(this).load(item.coverMedium).into(itemView.ivBookCover);
            val db : DatabaseHandler = DatabaseHandler(itemView.context)
            itemView.setOnCreateContextMenuListener { contextMenu, _, _ ->

                contextMenu.add(R.string.contextDelete).setOnMenuItemClickListener {
                   var mOos = getAdapterPosition()
                    val book : LocalBook? = item
                    db.deleteBook(book)
                    true
                }
                contextMenu.add(R.string.contextInfo).setOnMenuItemClickListener {
                    val toast = Toast.makeText(this.context, "I'm pressed for the item at position => $position", 10)
                 var book = item
                    db.deleteBook(book)
                    true
                }
            }
        }

    }
class DiffCallback : DiffUtil.ItemCallback<LocalBook>() {
    override fun areItemsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
       return oldItem.openLibraryId == newItem.openLibraryId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
        return oldItem == newItem
    }

}}


