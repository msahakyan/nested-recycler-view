package com.android.msahakyan.nestedrecycler.model;

import java.util.List;

/**
 * Created by msahakyan on 14/11/15.
 */
public class RelatedMoviesItem implements RecyclerItem {

    private List<Movie> relatedMovieList;

    public List<Movie> getRelatedMovieList() {
        return relatedMovieList;
    }

    public void setRelatedMovieList(List<Movie> relatedMovieList) {
        this.relatedMovieList = relatedMovieList;
    }


    @Override
    public String getType() {
        return null;
    }
}
