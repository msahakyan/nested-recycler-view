package com.android.msahakyan.nestedrecycler.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.PosterAdapter;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.model.ImageListParser;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.Poster;
import com.android.msahakyan.nestedrecycler.net.NetworkRequestListener;
import com.android.msahakyan.nestedrecycler.net.NetworkUtilsImpl;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Bind(R.id.movie_detail_thumbnail)
    protected FadeInNetworkImageView imageThumbnail;

    @Bind(R.id.movie_detail_title)
    protected TextView movieTitle;

    @Bind(R.id.movie_detail_date)
    protected TextView movieDate;

    @Bind(R.id.movie_detail_vote_average)
    protected TextView movieVoteAverage;

    @Bind(R.id.movie_detail_description)
    protected TextView movieDescription;

    @Bind(R.id.progress_view)
    protected CircularProgressView progressView;

    @Bind(R.id.movie_detail_episode_list)
    RecyclerView mEpisodeRecycler;

    private String mEndpoint;
    private Map<String, String> mUrlParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialization ButterKnife
        ButterKnife.bind(this);
        mEpisodeRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        Movie movie = getMovieFromIntent();
        if (movie != null) {
            loadEpisodes(movie.getId());
        } else {
            Log.w(TAG, "Can't load movies from intent");
        }
    }

    private void loadEpisodes(long movieId) {
        initEndpointAndUrlParams(movieId);
        progressView.startAnimation();
        progressView.setVisibility(View.VISIBLE);
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(mEndpoint),
            mUrlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
                    List<Poster> posterList = new Gson().fromJson(jsonResponse.toString(), ImageListParser.class).getPosters();
                    PosterAdapter episodeAdapter = new PosterAdapter(MovieDetailActivity.this, posterList);
                    mEpisodeRecycler.setAdapter(episodeAdapter);
                    episodeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(VolleyError error) {
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
                    Toast.makeText(MovieDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void initEndpointAndUrlParams(long movieId) {
        mEndpoint = "http://api.themoviedb.org/3/movie/" + movieId + "/images";
        mUrlParams = new HashMap<>();
        mUrlParams.put("api_key", "746bcc0040f68b8af9d569f27443901f");
    }

    private Movie getMovieFromIntent() {
        Movie movie = null;
        if (getIntent().getExtras().containsKey(BundleKey.EXTRA_MOVIE)) {
            movie = (Movie) getIntent().getSerializableExtra(BundleKey.EXTRA_MOVIE);
            if (movie != null) {
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                String fullPosterPath = "http://image.tmdb.org/t/p/w500/" + movie.getPosterPath();
                imageThumbnail.setImageUrl(fullPosterPath, imageLoader);
                movieTitle.setText(movie.getTitle());
                movieDate.setText(movie.getReleaseDate());
                movieVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
                movieDescription.setText(movie.getOverview());
            }
        }
        return movie;
    }
}
