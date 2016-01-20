package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.model.Backdrop;
import com.android.msahakyan.nestedrecycler.net.Endpoint;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author msahakyan
 *         <p/>
 *         Movie backdropes adapter
 */
public class BackdropAdapter extends RecyclerView.Adapter<BackdropAdapter.BackdropViewHolder> {

    private List<Backdrop> mMovieBackdrops;
    private Context mContext;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

    public BackdropAdapter(Context context, List<Backdrop> movieBackdrops) {
        mMovieBackdrops = movieBackdrops;
        mContext = context;
    }

    @Override
    public BackdropViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_backdrop_item, null, false);
        return new BackdropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BackdropViewHolder holder, int position) {
        final Backdrop backdrop = mMovieBackdrops.get(position);
        if (backdrop != null) {
            String fullBackdropPath = Endpoint.IMAGE + "/w185/" + backdrop.getFilePath();
            holder.backdrop.setImageUrl(fullBackdropPath, mImageLoader);
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
        return mMovieBackdrops.size();
    }

    static class BackdropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener clickListener;

        @Bind(R.id.movie_backdrop_thumbnail)
        protected FadeInNetworkImageView backdrop;

        public BackdropViewHolder(View view) {
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

