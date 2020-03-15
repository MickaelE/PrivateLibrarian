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
    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: LocalBook) = with(itemView) {
            itemView.tvTitle.text = item.title
            itemView.tvAuthor.text = item.author
            Glide.with(this).load(item.coverMedium).into(itemView.ivBookCover);
            setOnClickListener {
                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("PrivateLibrary")
                builder.setMessage("Vill du verkligen ta bort boken?")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->

                    if (context is InlineScanActivity) {
                        (context as InlineScanActivity).deleteBook(item)
                    }

                }

                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(context,
                        android.R.string.no, Toast.LENGTH_SHORT).show()
                }

                builder.show()

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

}