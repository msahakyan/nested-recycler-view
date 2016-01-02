package com.android.msahakyan.nestedrecycler.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.MovieAdapter;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.MovieListParser;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;
import com.android.msahakyan.nestedrecycler.net.NetworkRequestListener;
import com.android.msahakyan.nestedrecycler.net.NetworkUtilsImpl;
import com.android.msahakyan.nestedrecycler.provider.SearchSuggestionsProvider;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private List<RecyclerItem> mItems;
    private ProgressDialog mDialog;
    private GridLayoutManager mLayoutManager;
    private Map<String, String> mUrlParams;
    private String mEndpoint;
    private int mCurrentPage;
    private int mTotalPageSize;
    private SearchView mSearchView;

    @Bind(R.id.movie_recycler_view)
    protected RecyclerView mRecyclerView;

    private MovieAdapter mAdapter;

    private boolean mLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        handleIntent(getIntent());

        mCurrentPage = 1;
        mDialog = new ProgressDialog(MainActivity.this);
        mItems = new ArrayList<>();

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mItems.get(position) instanceof Movie ? 1 : 2;
            }
        });

        // Set layout manager to recyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    //check for scroll down
                    loadMoreItems();
                }
            }
        });

        initEndpointAndUrlParams(mCurrentPage);
        mDialog.setMessage(getString(R.string.loading_data));
        mDialog.show();
        mAdapter = new MovieAdapter(MainActivity.this, mItems);
        mRecyclerView.setAdapter(mAdapter);
        loadMovieList(false);
    }

    private void initEndpointAndUrlParams(int page) {
        mEndpoint = "http://api.themoviedb.org/3/discover/movie";
        mUrlParams = new HashMap<>();
        mUrlParams.put("api_key", "746bcc0040f68b8af9d569f27443901f");
        mUrlParams.put("page", String.valueOf(page));
    }

    private void initSearchEndpointAndUrlParams(String query) {
        mEndpoint = "http://api.themoviedb.org/3/search/movie";
        mUrlParams = new HashMap<>();
        mUrlParams.put("query", query.trim());
        mUrlParams.put("api_key", "746bcc0040f68b8af9d569f27443901f");
    }

    private void loadMoreItems() {
        if (mTotalPageSize > mCurrentPage) {
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

            if (!mLoading) {
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    mLoading = true;
                    Log.v(TAG, "Reached last Item!");

                    // Fetching new data...
                    initEndpointAndUrlParams(++mCurrentPage);

                    mDialog.setMessage(getString(R.string.loading_more_data));
                    mDialog.show();

                    loadMovieList(true);
                }
            }
        } else {
            // There are no more items to load
        }
    }

    private void loadMovieList(final boolean shouldInsertResults) {
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(mEndpoint),
            mUrlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    final int startPosition = mItems.size();
                    MovieListParser movieListParser = new Gson().fromJson(jsonResponse.toString(), MovieListParser.class);
                    mTotalPageSize = movieListParser.getTotalPages();
                    List<Movie> movieList = movieListParser.getResults();
                    if (shouldInsertResults) {
                        mItems.addAll(movieList);
                        mAdapter.notifyItemRangeInserted(startPosition, movieList.size());
                    } else {
                        mItems.clear();
                        mItems.addAll(movieList);
                        mAdapter.setRelatedItemsPosition(RecyclerView.NO_POSITION);
                        mAdapter.notifyDataSetChanged();
                    }

                    mLoading = false;
                }

                @Override
                public void onError(VolleyError error) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadSearchResults() {
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(mEndpoint),
            mUrlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    List<Movie> movieList = new Gson().fromJson(jsonResponse.toString(), MovieListParser.class).getResults();
                    mItems.clear();
                    mCurrentPage = 1;
                    mTotalPageSize = 1;
                    for (Movie movie : movieList) {
                        mItems.add(movie);
                    }
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setRelatedItemsPosition(RecyclerView.NO_POSITION);
                    mLoading = false;
                }

                @Override
                public void onError(VolleyError error) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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

            initSearchEndpointAndUrlParams(query);
            mDialog.setMessage(getString(R.string.loading_more_data));
            mDialog.show();
            loadSearchResults();
            mSearchView.clearFocus();
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
        mSearchView = (SearchView) menuItem.getActionView();

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Show list of movies again
                initEndpointAndUrlParams(mCurrentPage = 1);
                loadMovieList(false);

                return true;
            }
        });

        return true;
    }
}
