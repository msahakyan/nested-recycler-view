package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.model.Poster;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author msahakyan
 *         <p/>
 *         Movie posters adapter
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.EpisodeViewHolder> {

    private List<Poster> mMoviePosters;
    private Context mContext;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

    public PosterAdapter(Context context, List<Poster> moviePosters) {
        mMoviePosters = moviePosters;
        mContext = context;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_poster_item, null, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        final Poster poster = mMoviePosters.get(position);
        if (poster != null) {
            String fullPosterPath = "http://image.tmdb.org/t/p/w185/" + poster.getFilePath();
            holder.poster.setImageUrl(fullPosterPath, mImageLoader);
        }
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Implement click logic here
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviePosters.size();
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener clickListener;

        @Bind(R.id.movie_poster_thumbnail)
        protected FadeInNetworkImageView poster;

        public EpisodeViewHolder(View view) {
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

