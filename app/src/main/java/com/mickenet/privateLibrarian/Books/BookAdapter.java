package com.mickenet.privateLibrarian.Books;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mickenet.privateLibrarian.ISBN.BookClient;
import com.mickenet.privateLibrarian.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends   RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> mBooks;

    public BookAdapter(List<Book> mBooks) {
        this.mBooks = mBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.item_books, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bookView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
    // Get the data model based on position
        Book book = mBooks.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
     //   Picasso.with(getContext()).load(Uri.parse(book.getCoverUrl())).error(R.drawable.ic_nocover).into(holder.ivBookCover);
        // Return the completed view to render on screen

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivBookCover;
        public TextView tvTitle;
        public TextView tvAuthor;
        public ViewHolder(View itemView){
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle  = itemView.findViewById(R.id.tvTitle);
            tvAuthor  = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
