package com.mickenet.privatelibrarian;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.LinearLayout;

public class BookWiew extends LinearLayout {
    public BookWiew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public BookWiew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public BookWiew(Context context) {
        super(context);
        initView();
    }
    private void initView() {
        View view = inflate(getContext(), R.layout.item_books, null);

        addView(view);
    }
}
