package com.android.msahakyan.nestedrecycler.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazon.insights.ABTestClient;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.Event;
import com.amazon.insights.EventClient;
import com.amazon.insights.InsightsCallback;
import com.amazon.insights.Variation;
import com.amazon.insights.VariationSet;
import com.amazon.insights.error.InsightsError;
import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.Helper;
import com.android.msahakyan.nestedrecycler.common.abtest.ABTestConfig;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ABTestClient abClient;
    private EventClient eventClient;
    private Handler mainHandler;

    @Bind(R.id.movie_detail_thumbnail)
    protected FadeInNetworkImageView imageThumbnail;

    @Bind(R.id.movie_detail_title_label)
    protected TextView movieTitleLabel;

    @Bind(R.id.movie_detail_title)
    protected TextView movieTitle;

    @Bind(R.id.movie_detail_date_label)
    protected TextView movieDateLabel;

    @Bind(R.id.movie_detail_date)
    protected TextView movieDate;

    @Bind(R.id.movie_detail_genre_label)
    protected TextView movieGenreLabel;

    @Bind(R.id.movie_detail_genre)
    protected TextView movieGenre;

    @Bind(R.id.movie_detail_description_label)
    protected TextView movieDescriptionLabel;

    @Bind(R.id.movie_detail_description)
    protected TextView movieDescription;

    @Bind(R.id.progress_view)
    protected CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialization ButterKnife
        ButterKnife.bind(this);

        // Get AmazonInsights singleton instance
        AmazonInsights insightsInstance = new ABTestConfig().getInstance(this);
        abClient = insightsInstance.getABTestClient();
        eventClient = insightsInstance.getEventClient();

        // Create a visit event when the user starts movie details page.
        Event detailsPageLoaded = eventClient.createEvent("detailsPageLoading");

        // Record the details page loading event.
        eventClient.recordEvent(detailsPageLoaded);

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        // Get a handler that can be used to post to the main thread
        mainHandler = new Handler(this.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressView.clearAnimation();
                progressView.setVisibility(View.GONE);

                Helper.createConfirmationDialog(MovieDetailActivity.this, R.string.confirm_title, R.string.confirm_message, R.string.positive_btn,
                    R.string.negative_btn, new Helper.ConfirmationCallback() {
                        @Override
                        public void onConfirm() {
                            generateConfirmEvent();
                            movieTitleLabel.setText(getString(R.string.details_movie_title_label));
                            movieDateLabel.setText(getString(R.string.detail_movie_date_label));
                            movieGenreLabel.setText(getString(R.string.detail_movie_genre_label));
                            validateAndShowMovieBundle();
                        }

                        @Override
                        public void onCancel() {
                            generateCancelEvent();
                            finish();
                        }
                    });
            }
        };
        mainHandler.postDelayed(runnable, 1500);
    }

    private void generateConfirmEvent() {
        // Record when the user confirms loading of details page

        // Create an event
        Event detailsPageLoadingConfirmEvent = eventClient.createEvent("confirmLoading");

        // Record an event
        eventClient.recordEvent(detailsPageLoadingConfirmEvent);
    }

    private void generateCancelEvent() {
        // Record when the user cancels loading of details page

        // Create an event
        Event detailsPageLoadingCancelEvent = eventClient.createEvent("cancelLoading");

        // Record an event
        eventClient.recordEvent(detailsPageLoadingCancelEvent);
    }

    private void validateAndShowMovieBundle() {
        if (getIntent().getExtras().containsKey(BundleKey.EXTRA_MOVIE)) {
            Movie movie = (Movie) getIntent().getSerializableExtra(BundleKey.EXTRA_MOVIE);
            if (movie != null) {
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                String fullPosterPath = "http://image.tmdb.org/t/p/w500/" + movie.getPosterPath();
                imageThumbnail.setImageUrl(fullPosterPath, imageLoader);
                movieTitle.setText(movie.getTitle());
                movieDate.setText(movie.getReleaseDate());
                onShowDescVariationLoading(movie);
            }
        }
    }

    private void onShowDescVariationLoading(final Movie movie) {
        // Allocate/obtain variations for movie description
        abClient.getVariations("Movie")
            .setCallback(new InsightsCallback<VariationSet>() {
                @Override
                public void onComplete(VariationSet variations) {
                    Looper.prepare();

                    // Request a Variation out of the VariationSet
                    final Variation showDescriptionVariation = variations.getVariation("Movie");
                    final boolean showDescriptionEvent = showDescriptionVariation.getVariableAsBoolean("showDescriptionEvent", false);
                    Log.d(TAG, "I got showDescriptionEvent: " + showDescriptionEvent +
                        (showDescriptionEvent ? ". I'm in Testing group" : " . I'm in Control group"));

                    if (showDescriptionEvent) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                movieDescriptionLabel.setText(getString(R.string.detail_movie_description_label));
                                movieDescription.setText(movie.getOverview());
                            }
                        });
                    }
                }

                @Override
                public void onError(final InsightsError error) {
                    super.onError(error);
                    Log.e(TAG, "Exception has been thrown during of getting variation showDescriptionEvent" + error.getMessage());
                }
            });
    }

    protected void onPause() {
        super.onPause();

        // Submit events to the server
        eventClient.submitEvents();
    }
}
