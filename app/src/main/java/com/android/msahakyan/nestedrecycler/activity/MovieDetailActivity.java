package com.android.msahakyan.nestedrecycler.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.ItemDetailPagerAdapter;
import com.android.msahakyan.nestedrecycler.common.BundleKey;
import com.android.msahakyan.nestedrecycler.common.ZoomOutPageTransformer;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;
import com.android.msahakyan.nestedrecycler.model.RecyclerListItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Bind(R.id.vpPager)
    ViewPager mViewPager;

    private ItemDetailPagerAdapter mAdapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Initialization ButterKnife
        ButterKnife.bind(this);

        List<RecyclerItem> movieList = getMoviesFromIntent();

        mAdapterViewPager = new ItemDetailPagerAdapter(getSupportFragmentManager(), movieList);
        mViewPager.setAdapter(mAdapterViewPager);

        // Attach the page change listener inside the activity
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        // Attach page transformer
        mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // Code goes here
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Code goes here
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Code goes here
        }
    };

    private List<RecyclerItem> getMoviesFromIntent() {
        return ((RecyclerListItem) getIntent().getSerializableExtra(BundleKey.EXTRA_MOVIE_LIST)).getItems();
    }
}
