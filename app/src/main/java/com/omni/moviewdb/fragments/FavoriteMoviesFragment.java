package com.omni.moviewdb.fragments;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omni.moviewdb.R;
import com.omni.moviewdb.activity.MainActivity;
import com.omni.moviewdb.adapter.ImageAdapter;
import com.omni.moviewdb.data.MovieContract;
import com.omni.moviewdb.event.MovieClickListener;
import com.omni.moviewdb.model.movieResponse.MovieItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Omni on 04/11/2017.
 */

public class FavoriteMoviesFragment extends Fragment implements
        ImageAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MovieClickListener movieListener;

    private ImageAdapter mAdapter;

    private static final int MOVIE_LOADER_ID = 0;
    private GridLayoutManager manager;


    public void setMovieListener(MovieClickListener movieClickListener) {
        this.movieListener = movieClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Unbinder unbinder = ButterKnife.bind(this, rootView);

        setMovieListener((MainActivity) getActivity());

         manager = new GridLayoutManager(getActivity(), numberOfColumns());
        recyclerView.setLayoutManager(manager);

        return rootView;
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 185;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mAdapter = new ImageAdapter(getActivity(), FavoriteMoviesFragment.this, 1);
        recyclerView.setAdapter(mAdapter);
        // re-queries for all tasks
        getActivity().getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }


    @Override
    public void setOnItemClickListener(int position) {
        movieListener.onMovieClickListener(createMovieObject(mCursor, position));

    }


    private MovieItem createMovieObject(Cursor mCursor, int position) {

        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID);  // Indices for the _id, description, and priority columns
        int posterIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH);
        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.TITLE);
        int coverIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COVER);
        int overViewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW);
        int releaseIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.RELEASE);
        int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.VOTE_AVERAGE);
        int favoriteIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.FAVORITE);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        return new MovieItem(mCursor.getString(posterIndex), mCursor.getString(titleIndex)
                , mCursor.getString(overViewIndex), mCursor.getString(releaseIndex),
                mCursor.getDouble(voteAverageIndex), mCursor.getInt(idIndex), mCursor.getString(coverIndex), mCursor.getInt(favoriteIndex));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(Homefragment.class.getSimpleName(), "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    private Cursor mCursor = null;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
        mCursor = data;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    @Override
    public void onPause() {
        super.onPause();


        mBundleRecyclerViewState = new Bundle();
        mListState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//            gridLayoutManager.setSpanCount(columns);
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//
//            gridLayoutManager.setSpanCount(columns);
//
//        }
        recyclerView.setLayoutManager(manager);
    }
}
