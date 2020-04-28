package com.mickenet.privatelibrarian.books

/**
 * Bookadapter for use in a resycle view.
 */
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mickenet.privatelibrarian.R
import com.mickenet.privatelibrarian.database.DatabaseHandler
import kotlinx.android.synthetic.main.item_books.view.*

/**
 * Costructor
 */
class BookAdapter : ListAdapter<LocalBook, BookAdapter.ItemViewholder>(DiffCallback()) {
    init{

    }
    /**
     * Function to inflate xml resource.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_books, parent, false)

        )
    }

    /**
     * Bind items to view.
     */
    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position))

    }

    /**
     *  procedure to get position and data for a special row in list.
     */
    override fun getItemId(position: Int): Long = position.toLong()
    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        fun bind(item: LocalBook) = with(itemView) {
            itemView.tvTitle.text = item.title
            itemView.tvAuthor.text = item.author
            Glide.with(this).load(item.coverMedium).into(itemView.ivBookCover);
            val db : DatabaseHandler = DatabaseHandler(itemView.context)
            itemView.setOnCreateContextMenuListener { contextMenu, _, _ ->
                contextMenu.add(R.string.contextDelete).setOnMenuItemClickListener {
                    val book : LocalBook? = item
                    db.deleteBook(book)

                    true
                }
                contextMenu.add(R.string.contextInfo).setOnMenuItemClickListener {
                 var book = item
                    db.deleteBook(book)
                    true
                }
            }
        }
    }

    /**
     *  Call to callback.
     */
    class DiffCallback : DiffUtil.ItemCallback<LocalBook>() {
    override fun areItemsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
       return oldItem.openLibraryId == newItem.openLibraryId
    }

        /**
         * Fucntion to check for change.
         */
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
        return oldItem == newItem
    }

}}


