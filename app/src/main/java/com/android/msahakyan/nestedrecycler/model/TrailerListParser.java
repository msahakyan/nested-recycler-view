package com.android.msahakyan.nestedrecycler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author msahakyan
 */
public class TrailerListParser {

    @SerializedName("trailers")
    private TrailerObj trailerObj;

    public TrailerObj getTrailers() {
        return trailerObj;
    }

    public void setTrailerObj(TrailerObj trailerObj) {
        this.trailerObj = trailerObj;
    }

    public List<Trailer> getTrailerList() {
        return this.trailerObj.getTrailerList();
    }
}

class TrailerObj {

    @SerializedName("youtube")
    private List<Trailer> trailerList;

    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }
}