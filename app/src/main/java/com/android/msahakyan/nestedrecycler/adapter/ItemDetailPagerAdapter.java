package com.android.msahakyan.nestedrecycler.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.android.msahakyan.nestedrecycler.fragment.MovieDetailFragment;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;

import java.util.List;

/**
 * @author msahakyan
 */
public class ItemDetailPagerAdapter extends SmartFragmentStatePagerAdapter {

    private List<RecyclerItem> mItems;

    public ItemDetailPagerAdapter(FragmentManager fragmentManager, List<RecyclerItem> items) {
        super(fragmentManager);
        mItems = items;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        MovieDetailFragment fragment = MovieDetailFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKey.EXTRA_MOVIE, mItems.get(position));
        bundle.putBoolean(BundleKey.HAS_NEXT, hasNext(position));
        bundle.putBoolean(BundleKey.HAS_PREVIOUS, hasPrevious(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return ((Movie) mItems.get(position)).getTitle();
    }

    private boolean hasNext(int position) {
        return (mItems.size() > 1) && (position < mItems.size() - 1);
    }

    private boolean hasPrevious(int position) {
        return position > 0;
    }
}
