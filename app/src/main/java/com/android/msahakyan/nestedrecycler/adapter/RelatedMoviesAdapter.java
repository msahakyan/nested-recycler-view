package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.activity.MovieDetailActivity;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by msahakyan on 14/11/15.
 * <p/>
 * Related movies adapter which is actually adapter of nested recycler view
 */
public class RelatedMoviesAdapter extends RecyclerView.Adapter<RelatedMoviesAdapter.RelatedMoviesViewHolder> {

    private List<Movie> mMovieItems;
    private Context mContext;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

    public RelatedMoviesAdapter(Context context, List<Movie> movieItems) {
        mMovieItems = movieItems;
        mContext = context;
    }

    @Override
    public RelatedMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_movie_related_item, null, false);
        return new RelatedMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelatedMoviesViewHolder holder, int position) {
        final Movie movie = mMovieItems.get(position);
        holder.name.setText(movie.getTitle());
        if (movie.getPosterPath() != null) {
            String fullPosterPath = "http://image.tmdb.org/t/p/w500/" + movie.getPosterPath();
            holder.thumbnail.setImageUrl(fullPosterPath, mImageLoader);
        }
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startDetailsActivity(movie);
            }
        });
    }

    private void startDetailsActivity(Movie movie) {
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra(BundleKey.EXTRA_MOVIE, movie);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mMovieItems.size();
    }

    static class RelatedMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener clickListener;

        @Bind(R.id.movie_related_name)
        protected TextView name;

        @Bind(R.id.movie_related_thumbnail)
        protected FadeInNetworkImageView thumbnail;

        public RelatedMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.clickListener.onClick(v, getPosition());
        }
    }
}
