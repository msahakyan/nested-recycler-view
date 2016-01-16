package com.android.msahakyan.nestedrecycler.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author msahakyan
 */
public class RecyclerListItem<T extends RecyclerItem> implements Serializable {

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
