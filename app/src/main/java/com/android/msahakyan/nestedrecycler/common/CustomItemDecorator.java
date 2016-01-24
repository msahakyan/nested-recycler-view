package com.android.msahakyan.nestedrecycler.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author msahakyan
 */
public class CustomItemDecorator extends RecyclerView.ItemDecoration {

    private final int mHorizontalSpaceWidth;

    public CustomItemDecorator(int horizontalSpaceHeight) {
        this.mHorizontalSpaceWidth = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = outRect.right = mHorizontalSpaceWidth;
    }
}
