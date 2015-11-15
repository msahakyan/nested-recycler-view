package com.android.msahakyan.nestedrecycler.common.datasource;

import android.content.Context;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by msahakyan on 14/11/15.
 * <p/>
 * Provides fake data for testing purposes. In real life this data should be loaded
 * from somewhere else
 */
public class DataSource {

    private static final String PACKAGE = "com.android.msahakyan.nestedrecycler";

    /**
     * Returns hard-coded movie list which is used just for testing purposes
     *
     * @param context
     * @return
     */
    public static List<RecyclerItem> getMovieList(Context context) {

        List<String> movieNames = Arrays.asList(context.getResources().getStringArray(R.array.movies));
        List<RecyclerItem> movies = new ArrayList<>();

        for (String titleInfo : movieNames) {
            Movie movie = generateMovieByTitleInfo(context, titleInfo);
            movies.add(movie);
        }
        Collections.shuffle(movies);

        return movies;
    }

    /**
     * Returns movie object by given title information
     *
     * @param movieInfo
     * @return
     */
    private static Movie generateMovieByTitleInfo(Context context, String movieInfo) {

        Movie movie = new Movie();
        String[] details = movieInfo.split("_");

        movie.setType(details[0]);
        movie.setProductionDate(details[details.length - 1]);

        String filmName = "";
        for (int i = 1; i < details.length - 1; i++) {
            filmName += details[i] + " ";
        }

        movie.setName(filmName);
        int resId = context.getResources().getIdentifier(PACKAGE + ":drawable/" + movieInfo.substring(0, movieInfo.lastIndexOf("_")), null, null);
        movie.setThumbnailResId(resId);

        return movie;
    }

    /**
     * Returns all movies for given type
     *
     * @param context
     * @param type
     * @return
     */
    public static List<Movie> getRelatedMoviesByType(Context context, String type) {
        List<Movie> relatedMovies = new ArrayList<>();

        for (RecyclerItem item : getMovieList(context)) {
            if (type.equals(item.getType())) {
                relatedMovies.add((Movie) item);
            }
        }

        return relatedMovies;
    }

}
