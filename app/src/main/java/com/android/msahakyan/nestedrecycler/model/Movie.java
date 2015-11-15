package com.android.msahakyan.nestedrecycler.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by msahakyan on 14/11/15.
 * <p/>
 * Movie class
 */
public class Movie implements RecyclerItem {

    // [FilmType enum  -- start]
    private static final String FILM_TYPE_COMEDY = "comedy";
    private static final String FILM_TYPE_THRILLER = "thriller";
    private static final String FILM_TYPE_DRAMA = "drama";
    private static final String FILM_TYPE_HISTORICAL = "historic";
    private static final String FILM_TYPE_FANTASY = "fantasy";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({FILM_TYPE_COMEDY, FILM_TYPE_DRAMA, FILM_TYPE_FANTASY, FILM_TYPE_HISTORICAL, FILM_TYPE_THRILLER})
    public @interface MovieType {
    }
    // [FilmType enum  -- end]

    private long id;
    private String name;
    private String productionDate;
    private int thumbnailResId;

    @Movie.MovieType
    private String type;

    List<Movie> relatedMovies;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Movie> getRelatedMovies() {
        return relatedMovies;
    }

    public void setRelatedMovies(List<Movie> relatedMovies) {
        this.relatedMovies = relatedMovies;
    }
}
