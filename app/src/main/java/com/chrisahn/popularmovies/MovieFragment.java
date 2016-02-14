package com.chrisahn.popularmovies;

import android.os.Bundle;
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

import java.util.ArrayList;


public class MovieFragment extends android.support.v4.app.Fragment {

    public  static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieAdapter mMovieAdapter;
    private int mSortPosition;
    private static final String SORT_POSITION_KEY = "SORT_POSITION";
    private boolean spinnerFlag = false;


    public MovieFragment() {

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
                if (!spinnerFlag) {
                    mSortPosition = position;
                    updateMovie(mSortPosition);
                } else {
                    // resume previous sort and set correct spinner text
                    updateMovie(mSortPosition);
                    spinner.setSelection(mSortPosition);
                    // reset flag
                    spinnerFlag = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is clicked
            }
        });
    }

    // MovieDetailFragment callback for when an item is selected
    public interface Callback {
        public void onItemSelected(MovieInfoContainer movieData);
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // initially pass an empty arraylist
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieInfoContainer>());

        GridView gridView = (GridView) rootView.findViewById(R.id.movieGrid);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                // get the movie details and put into movieInfoContainer
                MovieInfoContainer movieInfoContainer = mMovieAdapter.getItem(position);
                // Wrap up data to put into intent as extra then start the intent
                /*Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(MOVIE_DATA, movieInfoContainer);
                startActivity(intent);*/

                // Notify Callback when item is selected
                ((Callback) getActivity())
                        .onItemSelected(movieInfoContainer);

            }
        });

        // If an instance state exist, extract the sort option we were previously in
        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_POSITION_KEY)) {
            mSortPosition = savedInstanceState.getInt(SORT_POSITION_KEY);
            // set flag to restore previous state
            spinnerFlag = true;
        }

        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the sort option that is currently selected
        outState.putInt(SORT_POSITION_KEY, mSortPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        updateMovie(mSortPosition);
        super.onResume();
    }

    // Helper function to update the movie list (popular/highest rated)
    // position = the position of the string array. 0 - popular, 1 - highest rated
    private void updateMovie(int position) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), mMovieAdapter);

        if (position == 0) {
            // Most Popular
            fetchMovieTask.execute("popularity.desc");
        }
        else if (position == 1) {
            // Highest Rated
            fetchMovieTask.execute("vote_average.desc");
        }
    }

}

