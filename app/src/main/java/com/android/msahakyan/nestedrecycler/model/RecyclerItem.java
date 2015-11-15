package com.android.msahakyan.nestedrecycler.model;

import java.io.Serializable;

/**
 * Created by msahakyan on 14/11/15.
 * <p/>
 * Common interface for both type of items which are using in current project
 */
public interface RecyclerItem extends Serializable {
    String getType();
}
