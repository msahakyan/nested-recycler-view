package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.activity.TrailerActivity;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.model.Trailer;
import com.android.msahakyan.nestedrecycler.net.Endpoint;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author msahakyan
 *         <p/>
 *         Movie trailers adapter
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> mTrailerList;
    private Context mContext;

    public TrailerAdapter(Context context, List<Trailer> movieTrailers) {
        mTrailerList = movieTrailers;
        mContext = context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_trailer_item, null, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        final Trailer trailer = mTrailerList.get(position);
        if (trailer != null) {
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            holder.trailerThumbnail.setImageUrl(getYoutubeThmbUrl(trailer.getSource()), imageLoader);
            holder.trailerName.setText(trailer.getName());
        }
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startTrailerActivity(trailer);
            }
        });
    }

    private String getYoutubeThmbUrl(String source) {
        return Endpoint.YOUTUBE_THUMBNAIL + source + "/mqdefault.jpg";
    }

    private void startTrailerActivity(Trailer trailer) {
        Intent intent = new Intent(mContext, TrailerActivity.class);
        intent.putExtra(BundleKey.EXTRA_TRAILER_ID, trailer.getSource());
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    static class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener clickListener;

        @Bind(R.id.movie_trailer_thumbnail)
        protected FadeInNetworkImageView trailerThumbnail;

        @Bind(R.id.movie_trailer_name)
        protected TextView trailerName;

        public TrailerViewHolder(View view) {
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

