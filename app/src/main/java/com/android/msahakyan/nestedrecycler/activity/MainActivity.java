package com.android.msahakyan.nestedrecycler.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.adapter.MovieAdapter;
import com.android.msahakyan.nestedrecycler.common.datasource.DataSource;
import com.android.msahakyan.nestedrecycler.model.Movie;
import com.android.msahakyan.nestedrecycler.model.RecyclerItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private List<RecyclerItem> mItems;

    @Bind(R.id.movie_recycler_view)
    protected RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mItems.get(position) instanceof Movie ? 1 : 2;
            }
        });

        // Set layout manager to recyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // Fake data loading
        new FakeAsyncTask().execute();
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
