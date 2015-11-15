package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.model.Movie;

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

    public RelatedMoviesAdapter(Context context, List<Movie> movieItems) {
        mMovieItems = movieItems;
        mContext = context;
    }

    @Override
    public RelatedMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_movie_related_item, null, false);
        RelatedMoviesViewHolder viewHolder = new RelatedMoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RelatedMoviesViewHolder holder, int position) {
        Movie movie = mMovieItems.get(position);
        holder.name.setText(movie.getName());
        holder.thumbnail.setImageResource(movie.getThumbnailResId());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                //TODO Implement your logic here
                Toast.makeText(mContext, mMovieItems.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
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
        protected ImageView thumbnail;

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
