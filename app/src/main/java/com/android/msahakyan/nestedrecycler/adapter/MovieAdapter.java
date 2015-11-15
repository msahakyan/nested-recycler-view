package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.common.ItemClickListener;
import com.android.msahakyan.nestedrecycler.common.datasource.DataSource;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;
import com.android.msahakyan.nestedrecycler.model.RelatedMoviesItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by msahakyan on 12/11/15.
 * <p/>
 * Movie adapter which actually is adapter of  parent recycler view's
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.GenericViewHolder> {

    private static final int TYPE_MOVIE = 1001;
    private static final int TYPE_RELATED_ITEMS = 1002;

    private Context mContext;
    private List<RecyclerItem> mItemList;
    private int relatedItemsPosition = -1;
    private String lastRelatedMoviesType;
    private RecyclerView mRecyclerView;

    public MovieAdapter(Context context, List<RecyclerItem> itemList) {
        mContext = context;
        mItemList = itemList;
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
            bindRelatedItemsViewHolder((RelatedMoviesViewHolder) holder, (RelatedMoviesItem) mItemList.get(position));
        }
    }

    private void bindMovieViewHolder(MovieViewHolder holder, final Movie movie) {
        holder.name.setText(movie.getName());
        holder.thumbnail.setImageResource(movie.getThumbnailResId());
        holder.date.setText(movie.getProductionDate());
        holder.type.setText(movie.getType());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                RelatedMoviesItem relatedMoviesItem = new RelatedMoviesItem();
                lastRelatedMoviesType = movie.getType();
                relatedMoviesItem.setRelatedMovieList(DataSource.getRelatedMoviesByType(mContext, lastRelatedMoviesType));

                // If related item was added before, we have to remove it and add a new one
                if (relatedItemsPosition != -1) {
                    if (position > relatedItemsPosition) {
                        position--;
                    }

                    mItemList.remove(relatedItemsPosition);
                    notifyItemRemoved(relatedItemsPosition);
                    notifyItemRangeChanged(relatedItemsPosition, mItemList.size());
                }

                if (position < mItemList.size() - 1) {
                    relatedItemsPosition = position % 2 == 0 ? position + 2 : position + 1;
                } else {
                    relatedItemsPosition = position + 1;
                }

                mItemList.add(relatedItemsPosition, relatedMoviesItem);
                notifyItemInserted(relatedItemsPosition);
                notifyItemRangeChanged(relatedItemsPosition, mItemList.size());
                if (mRecyclerView != null) {
                    mRecyclerView.smoothScrollToPosition(relatedItemsPosition);
                }
            }
        });
    }

    private void bindRelatedItemsViewHolder(RelatedMoviesViewHolder holder, final RelatedMoviesItem relatedMoviesItem) {
        LinearLayoutManager layoutManager
            = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.relatedItemsRecyclerView.setLayoutManager(layoutManager);
        holder.relatedItemsRecyclerView.setAdapter(new RelatedMoviesAdapter(mContext, relatedMoviesItem.getRelatedMovieList()));
        holder.relatedMoviesHeader.setText(mContext.getString(R.string.related_movies) + " " + lastRelatedMoviesType);
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

        @Bind(R.id.movie_name)
        protected TextView name;

        @Bind(R.id.movie_thumbnail)
        protected ImageView thumbnail;

        @Bind(R.id.movie_production_date)
        protected TextView date;

        @Bind(R.id.movie_type)
        protected TextView type;

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

        public RelatedMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            // do nothing
        }
    }

    // Overriding this method to get access to recyclerView on which current MovieAdapter has been attached.
    // In the future we will use that reference for scrolling to newly added relatedMovies item
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }
}

