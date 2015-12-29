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
        loadMovieList();
    }

    private void initEndpointAndUrlParams(int page) {
        mEndpoint = "http://api.themoviedb.org/3/discover/movie";
        mUrlParams = new HashMap<>();
        mUrlParams.put("api_key", "746bcc0040f68b8af9d569f27443901f");
        mUrlParams.put("sort_by", "vote_average.desc");
        mUrlParams.put("page", String.valueOf(page));
    }

    private void loadMoreItems() {
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

                loadMovieList();
            }
        }
    }

    private void loadMovieList() {
        new NetworkUtilsImpl().executeJsonRequest(Request.Method.GET, new StringBuilder(mEndpoint),
            mUrlParams, new NetworkRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonResponse) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    final int startPosition = mItems.size();
                    List<Movie> movieList = new Gson().fromJson(jsonResponse.toString(), MovieListParser.class).getResults();
                    for (Movie movie : movieList) {
                        mItems.add(movie);
                    }
                    mAdapter.notifyItemRangeInserted(startPosition, movieList.size());
//                    mAdapter.notifyDataSetChanged();
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
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //  Saving the query during of on handle intent
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

//            mItems = DataSource.searchMoviesByName(MainActivity.this, query);
//            mAdapter = new MovieAdapter(MainActivity.this, mItems);
//            mRecyclerView.setAdapter(mAdapter);
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
//                mItems = DataSource.getMovieList(MainActivity.this);
//                mAdapter = new MovieAdapter(MainActivity.this, mItems);
//                mRecyclerView.setAdapter(mAdapter);

                return true;
            }
        });

        return true;
    }
}
