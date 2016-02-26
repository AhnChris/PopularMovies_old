package com.chrisahn.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.chrisahn.popularmovies.data.FavoriteProvider;


public class MovieFavoriteFragment extends android.support.v4.app.Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = MovieFavoriteFragment.class.getSimpleName();
    private FavoriteAdapter mFavoriteAdapter;

    public MovieFavoriteFragment() {
    }
    public interface Callback {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // initialize empty adapter
        mFavoriteAdapter = new FavoriteAdapter(getActivity(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.movieGrid);
        gridView.setAdapter(mFavoriteAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // initialize the CursorLoader - initializes the background framework
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavoriteProvider.Favorites.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteAdapter.swapCursor(null);
    }
}
