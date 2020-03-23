package com.mickenet.privatelibrarian.books

import android.annotation.SuppressLint
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mickenet.privatelibrarian.InlineScanActivity
import com.mickenet.privatelibrarian.R
import kotlinx.android.synthetic.main.item_books.view.*


class BookAdapter : ListAdapter<LocalBook, BookAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_books, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        val context = holder.itemView.context
        holder.bind(getItem(position))
    }
    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        fun bind(item: LocalBook) = with(itemView) {
            itemView.tvTitle.text = item.title
            itemView.tvAuthor.text = item.author
            Glide.with(this).load(item.coverMedium).into(itemView.ivBookCover);
            itemView.setOnCreateContextMenuListener { contextMenu, _, _ ->
                contextMenu.add(R.string.contextADD).setOnMenuItemClickListener {
                    val toast = Toast.makeText(this.context, "I'm pressed for the item at position => $position", 10)
                    true
                }
                contextMenu.add(R.string.contextDelete).setOnMenuItemClickListener {
                    val toast = Toast.makeText(this.context, "I'm pressed for the item at position => $position", 10)
                    true
                }
                contextMenu.add(R.string.contextInfo).setOnMenuItemClickListener {
                    val toast = Toast.makeText(this.context, "I'm pressed for the item at position => $position", 10)
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


