package com.android.msahakyan.nestedrecycler.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.MovieAdapter;
import com.android.msahakyan.nestedrecycler.common.datasource.DataSource;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;
import com.android.msahakyan.nestedrecycler.provider.SearchSuggestionsProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private List<RecyclerItem> mItems;

    @Bind(R.id.movie_recycler_view)
    protected RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;

    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        handleIntent(getIntent());

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mItems == null) ? 2 : (mItems.get(position) instanceof Movie ? 1 : 2);
            }
        });

        // Set layout manager to recyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Fake data loading
        new FakeAsyncTask().execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Set layout manager to recyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //  Saving the query during of on handle intent
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            mItems = DataSource.searchMoviesByName(MainActivity.this, query);
            mAdapter = new MovieAdapter(MainActivity.this, mItems);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Show list of movies again
                mItems = DataSource.getMovieList(MainActivity.this);
                mAdapter = new MovieAdapter(MainActivity.this, mItems);
                mRecyclerView.setAdapter(mAdapter);

                return true;
            }
        });

        return true;
    }

    public class FakeAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.loading_data));
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 2 seconds.
            long endTime = System.currentTimeMillis() + 2 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                        mItems = DataSource.getMovieList(MainActivity.this);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            mAdapter = new MovieAdapter(MainActivity.this, mItems);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
