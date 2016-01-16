package com.android.msahakyan.nestedrecycler.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author msahakyan
 *         Common interface for both type of items which are using in current project
 */
public interface RecyclerItem extends Serializable {
    List<Integer> getGenreIds();
}
