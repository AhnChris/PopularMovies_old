package com.chrisahn.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.chrisahn.popularmovies.data.FavoriteProvider;


public class MovieFavoriteFragment extends android.support.v4.app.Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = MovieFavoriteFragment.class.getSimpleName();
    private FavoriteAdapter mFavoriteAdapter;
    private int mSortPosition;
    private boolean spinnerFlag = false;
    private static final String SORT_POSITION_KEY = "SORT_POSITION";

    public MovieFavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // clear existing items to not have duplicates after screen rotations
        menu.clear();
        // inflate the menu layout
        inflater.inflate(R.menu.movie_fragment_menu, menu);
        // get the menu spinner item
        MenuItem item = menu.findItem(R.id.spinner);
        // set the spinner item to this Spinner
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        // create ArrayAdapter with string array with a customer spinner item layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_options,
                R.layout.customer_spinner_item);
        // set the custom spinner dropdown layout
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // set adapter for the spinner
        spinner.setAdapter(adapter);

        // set a listener for what gets clicked and update accordingly
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 2)
                    ((Callback) getActivity()).onSpinnerItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is clicked
            }
        });
    }

    public interface Callback {
        public void onSpinnerItemSelected(int position);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // initialize empty adapter
        mFavoriteAdapter = new FavoriteAdapter(getActivity(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.movieGrid);
        gridView.setAdapter(mFavoriteAdapter);

        // If an instance state exist, extract the sort option we were previously in
        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_POSITION_KEY)) {
            mSortPosition = savedInstanceState.getInt(SORT_POSITION_KEY);
            // set flag to restore previous state
            spinnerFlag = true;
        }

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
