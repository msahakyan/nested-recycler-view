package com.android.msahakyan.nestedrecycler.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.BackdropAdapter;
import com.android.msahakyan.nestedrecycler.adapter.TrailerAdapter;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.Helper;
import com.android.msahakyan.nestedrecycler.common.ViewAnimator;
import com.android.msahakyan.nestedrecycler.config.Config;
import com.android.msahakyan.nestedrecycler.model.Backdrop;
import com.android.msahakyan.nestedrecycler.model.ImageListParser;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.Trailer;
import com.android.msahakyan.nestedrecycler.model.TrailerListParser;
import com.android.msahakyan.nestedrecycler.net.Endpoint;
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
import butterknife.OnClick;


/**
 * @author msahakyan
 */
public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

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

    @Bind(R.id.movie_detail_trailer_recycler)
    RecyclerView mTrailerRecycler;

    @Bind(R.id.label_trailers)
    TextView mTrailersLabel;

    @Bind(R.id.label_backdrops)
    TextView mBackdropsLabel;

    @Bind(R.id.ic_arrow)
    ImageView mIconArrow;

    private String mEndpoint;
    private Map<String, String> mUrlParams;

    private ViewAnimator mViewAnimator;
    private boolean mIsBackdropListVisible;
    private boolean mIsTrailerListVisible;

    public MovieDetailFragment() {
        mViewAnimator = new ViewAnimator();
    }

    public static MovieDetailFragment newInstance() {
        return new MovieDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        mEpisodeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mTrailerRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        Movie movie = getMovieFromArguments();
        if (movie != null) {
            loadTrailers(movie.getId());
            loadEpisodes(movie.getId());
        } else {
            Log.w(TAG, "Can't load movies from intent");
        }
        setupAnimation();
        mIsBackdropListVisible = true;
        mIsTrailerListVisible = true;

        return view;
    }

    private void setupAnimation() {

        TranslateAnimation animation = new TranslateAnimation(0.0f, 30.0f, 0.0f, 0.0f);
        animation.setDuration(500);
        animation.setRepeatCount(Integer.MAX_VALUE);
        animation.setRepeatMode(2);
        animation.setFillAfter(true);
        if (hasNextPage()) {
            mIconArrow.setVisibility(View.VISIBLE);
            mIconArrow.startAnimation(animation);
        } else {
            mIconArrow.setVisibility(View.GONE);
            mIconArrow.clearAnimation();
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
                    List<Backdrop> backdropList = new Gson().fromJson(jsonResponse.toString(), ImageListParser.class).getBackdrops();
                    if (!Helper.isEmpty(backdropList)) {
                        mBackdropsLabel.setVisibility(View.VISIBLE);
                        mEpisodeRecycler.setVisibility(View.VISIBLE);
                        BackdropAdapter episodeAdapter = new BackdropAdapter(getActivity(), backdropList);
                        mEpisodeRecycler.setAdapter(episodeAdapter);
                        episodeAdapter.notifyDataSetChanged();
                    } else {
                        mEpisodeRecycler.setVisibility(View.GONE);
                        mBackdropsLabel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
                    mEpisodeRecycler.setVisibility(View.GONE);
                    mBackdropsLabel.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadTrailers(long movieId) {

        String endpoint = Endpoint.MOVIE + movieId;
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("api_key", Config.API_KEY);
        urlParams.put("append_to_response", "trailers");

        progressView.startAnimation();
        progressView.setVisibility(View.VISIBLE);
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(endpoint),
            urlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
                    List<Trailer> trailerList = new Gson().fromJson(jsonResponse.toString(), TrailerListParser.class).getTrailerList();
                    if (!Helper.isEmpty(trailerList)) {
                        mTrailersLabel.setVisibility(View.VISIBLE);
                        mTrailerRecycler.setVisibility(View.VISIBLE);
                        TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), trailerList);
                        mTrailerRecycler.setAdapter(trailerAdapter);
                        trailerAdapter.notifyDataSetChanged();
                    } else {
                        mTrailersLabel.setVisibility(View.GONE);
                        mTrailerRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
                    mTrailersLabel.setVisibility(View.GONE);
                    mTrailerRecycler.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }


    private void initEndpointAndUrlParams(long movieId) {
        mEndpoint = Endpoint.MOVIE + movieId + "/images";
        mUrlParams = new HashMap<>();
        mUrlParams.put("api_key", Config.API_KEY);
    }

    private Movie getMovieFromArguments() {
        Movie movie = null;
        if (getArguments().containsKey(BundleKey.EXTRA_MOVIE)) {
            movie = (Movie) getArguments().getSerializable(BundleKey.EXTRA_MOVIE);
            if (movie != null) {
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                String fullBackdropPath = Endpoint.IMAGE + "/w500/" + movie.getPosterPath();
                imageThumbnail.setImageUrl(fullBackdropPath, imageLoader);
                imageThumbnail.setErrorImageResId(R.drawable.error);
                movieTitle.setText(movie.getTitle());
                movieDate.setText(movie.getReleaseDate());
                movieVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
                movieDescription.setText(!TextUtils.isEmpty(movie.getOverview()) ?
                    movie.getOverview() : getString(R.string.no_description_available));
            }
        }
        return movie;
    }

    @OnClick(R.id.label_backdrops)
    void onClickBackdropsLabel() {
        if (mEpisodeRecycler != null) {
            if (mIsBackdropListVisible) {
                mViewAnimator.collapse(mEpisodeRecycler);
                mBackdropsLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            } else {
                mViewAnimator.expand(mEpisodeRecycler);
                mBackdropsLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            }
            mIsBackdropListVisible = !mIsBackdropListVisible;
        }
    }

    @OnClick(R.id.label_trailers)
    void onClickTrailersLabel() {
        if (mTrailerRecycler != null) {
            if (mIsTrailerListVisible) {
                mViewAnimator.collapse(mTrailerRecycler);
                mTrailersLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            } else {
                mViewAnimator.expand(mTrailerRecycler);
                mTrailersLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            }
            mIsTrailerListVisible = !mIsTrailerListVisible;
        }
    }


    private boolean hasNextPage() {
        return getArguments().getBoolean(BundleKey.HAS_NEXT, false);
    }

    private boolean hasPreviousPage() {
        return getArguments().getBoolean(BundleKey.HAS_PREVIOUS, false);
    }
}
