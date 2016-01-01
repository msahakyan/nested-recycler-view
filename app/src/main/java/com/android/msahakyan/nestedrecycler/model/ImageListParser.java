package com.android.msahakyan.nestedrecycler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author msahakyan
 */
public class ImageListParser {

    @SerializedName("id")
    private long id;

    @SerializedName("posters")
    private List<Poster> posters;

    @SerializedName("backdrops")
    private List<Poster> backdrops;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Poster> getPosters() {
        return posters;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }

    public List<Poster> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<Poster> backdrops) {
        this.backdrops = backdrops;
    }
}
