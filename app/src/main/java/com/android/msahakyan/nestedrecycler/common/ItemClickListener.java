package com.android.msahakyan.nestedrecycler.common;

import android.view.View;

/**
 * Created by msahakyan on 15/11/15.
 * <p/>
 * This interface will be overridden in viewHolder classes
 * to be able to call onClick method on corresponding recyclerView items
 */
public interface ItemClickListener {
    void onClick(View view, int position);
}
