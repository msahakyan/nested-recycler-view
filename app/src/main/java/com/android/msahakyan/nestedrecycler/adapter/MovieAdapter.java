package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.nestedrecycler.PushService;
import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.activity.MovieDetailActivity;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.CustomItemDecorator;
import com.android.msahakyan.nestedrecycler.common.Helper;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.common.PushNotification;
import com.android.msahakyan.nestedrecycler.config.Config;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.MovieListParser;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;
import com.android.msahakyan.nestedrecycler.model.RecyclerListItem;
import com.android.msahakyan.nestedrecycler.model.RelatedMoviesItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author msahakyan
 *         <p/>
 *         Movie adapter which actually is adapter of parent recycler view's
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.GenericViewHolder> {

    private static final int TYPE_MOVIE = 1001;
    private static final int TYPE_RELATED_ITEMS = 1002;
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private List<RecyclerItem> mItemList;
    private int relatedItemsPosition = RecyclerView.NO_POSITION;
    private List<Integer> mLastItemGenreIds;
    private RecyclerView mRecyclerView;
    private String mEndpoint;
    private Map<String, String> mUrlParams;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    private boolean mRelatedItemsLoading;
    private int mRelatedItemsCurrentPage;
    private List<Movie> mRelatedItemList;

    public MovieAdapter(Context context, List<RecyclerItem> itemList) {
        mContext = context;
        mItemList = itemList;
        mRelatedItemList = new ArrayList<>();
    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        GenericViewHolder customItemViewHolder = null;
        switch (viewType) {
            case TYPE_MOVIE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_movie_item, parent, false);
                customItemViewHolder = new MovieViewHolder(view);
                break;
            case TYPE_RELATED_ITEMS:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_movie_related_items, parent, false);
                customItemViewHolder = new RelatedMoviesViewHolder(view);
                break;
            default:
                break;
        }

        return customItemViewHolder;
    }

    @Override
    public void onBindViewHolder(GenericViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            bindMovieViewHolder((MovieViewHolder) holder, (Movie) mItemList.get(position));
        } else if (holder instanceof RelatedMoviesViewHolder) {
            bindRelatedItemsViewHolder((RelatedMoviesViewHolder) holder);
        }
    }

    private void bindMovieViewHolder(MovieViewHolder holder, final Movie movie) {
//        holder.name.setText(movie.getTitle());
//        holder.voteAvrg.setText(String.valueOf(movie.getVoteAverage()));
        if (movie.getPosterPath() != null) {
            String fullPosterPath = Endpoint.IMAGE + "/w185/" + movie.getPosterPath();
            holder.thumbnail.setImageUrl(fullPosterPath, mImageLoader);
            holder.thumbnail.setErrorImageResId(R.drawable.error);
        }
//        holder.date.setText(movie.getReleaseDate());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startDetailsActivity(position);
                insertRelatedMoviesItem(position, movie);
            }
        });
    }

    private void startDetailsActivity(int position) {
        List<RecyclerItem> movieList = new ArrayList<>(mItemList.subList(position, mItemList.size()));
        List<RecyclerItem> filteredItems = filterRelatedItems(movieList);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        RecyclerListItem recyclerListItem = new RecyclerListItem();
        recyclerListItem.setItems(filteredItems);
        intent.putExtra(BundleKey.EXTRA_MOVIE_LIST, recyclerListItem);
        mContext.startActivity(intent);
    }

    private List<RecyclerItem> filterRelatedItems(List<RecyclerItem> movies) {
        List<RecyclerItem> filteredItems = new ArrayList<>();
        if (movies != null) {
            for (RecyclerItem item : movies) {
                if (item instanceof Movie) {
                    filteredItems.add(item);
                }
            }
        }
        return filteredItems;
    }

    private void insertRelatedMoviesItem(int position, Movie movie) {
        final RelatedMoviesItem relatedMoviesItem = new RelatedMoviesItem();
        mLastItemGenreIds = movie.getGenreIds();
        mRelatedItemList.clear();
        // If related item was added before, we have to remove it and add a new one
        if (relatedItemsPosition != -1) {
            if (position > relatedItemsPosition) {
                position--;
            }

            mItemList.remove(relatedItemsPosition);
            notifyItemRemoved(relatedItemsPosition);
            notifyItemRangeChanged(relatedItemsPosition, mItemList.size());
        }

        if (Helper.isEmpty(mLastItemGenreIds)) {
            Toast.makeText(mContext, R.string.no_related_movies_available, Toast.LENGTH_SHORT).show();
            relatedItemsPosition = RecyclerView.NO_POSITION;
            return;
        }

        if (position < mItemList.size() - 1) {
            relatedItemsPosition = position % 2 == 0 ? position + 2 : position + 1;
        } else {
            relatedItemsPosition = position + 1;
        }

        mItemList.add(relatedItemsPosition, relatedMoviesItem);
        notifyItemInserted(relatedItemsPosition);
        notifyItemRangeChanged(relatedItemsPosition, mItemList.size());
        if (mRecyclerView != null && relatedItemsPosition > 1) {
            ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(relatedItemsPosition - 1, 0);
        }
        createNotification(movie);
    }

    private void createNotification(Movie movie) {
        Intent i = new Intent();
        PushNotification notification = new PushNotification();
        notification.setId(movie.getId());
        notification.setMessage("Similar movies for \"" + movie.getTitle() + "\"");
        notification.setTitle(mContext.getString(R.string.app_name));
        notification.setBigTitle("Similar Movies");
        notification.setIconUrl(movie.getPosterPath());
        notification.setImageUrl(movie.getBackdropPath());
        i.putExtra(BundleKey.EXTRA_NOTIFICATION, notification);

        sendPushNotification(mContext, i);
    }

    private static void sendPushNotification(Context context, Intent i) {
        i.setClass(context, PushService.class);
        context.startService(i);
    }

    private void bindRelatedItemsViewHolder(final RelatedMoviesViewHolder holder) {
        final LinearLayoutManager layoutManager
            = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RelatedMoviesAdapter adapter = new RelatedMoviesAdapter(mContext, mRelatedItemList);
        holder.relatedItemsRecyclerView.setAdapter(adapter);
        holder.relatedItemsRecyclerView.addItemDecoration(new CustomItemDecorator((int) mContext.getResources().getDimension(R.dimen.padding_size_small)));
        holder.relatedMoviesHeader.setText(mContext.getString(R.string.loading_data));
        holder.relatedItemsRecyclerView.setLayoutManager(layoutManager);
        holder.relatedItemsRecyclerView.setHasFixedSize(true);

        holder.relatedItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) {
                    //check for scroll right
                    loadMoreItems(holder);
                }
            }
        });

        holder.progressView.setVisibility(View.VISIBLE);
        holder.progressView.startAnimation();

        // Loading similar products
        initEndpointAndUrlParams(mRelatedItemsCurrentPage = 1);
        loadRelatedMovies(holder);
    }

    private void loadRelatedMovies(final RelatedMoviesViewHolder holder) {
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(mEndpoint),
            mUrlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    holder.progressView.clearAnimation();
                    holder.progressView.setVisibility(View.GONE);
                    List<Movie> freshLoadedList = new Gson().fromJson(jsonResponse.toString(), MovieListParser.class).getResults();
                    mRelatedItemList.addAll(freshLoadedList);
                    holder.relatedItemsRecyclerView.getAdapter().notifyItemRangeInserted(mRelatedItemList.size(), freshLoadedList.size());
                    holder.relatedMoviesHeader.setText(mContext.getString(R.string.related_movies));
                    mRelatedItemsLoading = false;
                }

                @Override
                public void onError(VolleyError error) {
                    holder.progressView.clearAnimation();
                    holder.progressView.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadMoreItems(final RelatedMoviesViewHolder holder) {

        LinearLayoutManager layoutManager = (LinearLayoutManager) holder.relatedItemsRecyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

        if (!mRelatedItemsLoading) {
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                mRelatedItemsLoading = true;
                Log.v(TAG, "Reached last Item!");

                // Fetching new data...
                initEndpointAndUrlParams(++mRelatedItemsCurrentPage);
                holder.progressView.setVisibility(View.VISIBLE);
                holder.progressView.startAnimation();
                loadRelatedMovies(holder);
            }
        }
    }

    private void initEndpointAndUrlParams(int page) {
        mEndpoint = "http://api.themoviedb.org/3/discover/movie";
        mUrlParams = new HashMap<>();
        mUrlParams.put("api_key", Config.API_KEY);
        mUrlParams.put("sort_by", "vote_average.desc");
        mUrlParams.put("with_genres", Helper.getCsvGenreIds(mLastItemGenreIds));
        mUrlParams.put("page", String.valueOf(page));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int type = super.getItemViewType(position);

        if (mItemList.get(position) instanceof Movie) {
            type = TYPE_MOVIE;
        } else if (mItemList.get(position) instanceof RelatedMoviesItem) {
            type = TYPE_RELATED_ITEMS;
        }

        return type;
    }

    static class GenericViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener clickListener;

        public GenericViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.clickListener.onClick(v, getPosition());
        }
    }

    /**
     * ViewHolder of the movie element
     */
    class MovieViewHolder extends GenericViewHolder {

//        @Bind(R.id.movie_name)
//        protected TextView name;

        @Bind(R.id.movie_thumbnail)
        protected FadeInNetworkImageView thumbnail;

//        @Bind(R.id.movie_production_date)
//        protected TextView date;

//        @Bind(R.id.movie_average_vote)
//        protected TextView voteAvrg;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * ViewHolder of the related movies element which contains reference
     * to related movies recyclerView and textView header of that element
     */
    class RelatedMoviesViewHolder extends GenericViewHolder {

        @Bind(R.id.related_movies_recycler)
        protected RecyclerView relatedItemsRecyclerView;

        @Bind(R.id.related_movies_header)
        protected TextView relatedMoviesHeader;

        @Bind(R.id.progress_view)
        protected CircularProgressView progressView;

        public RelatedMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            // do nothing
        }
    }

    public void setRelatedItemsPosition(int relatedItemsPosition) {
        this.relatedItemsPosition = relatedItemsPosition;
    }

    // Overriding this method to get access to recyclerView on which current MovieAdapter has been attached.
    // In the future we will use that reference for scrolling to newly added relatedMovies item
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }
}

