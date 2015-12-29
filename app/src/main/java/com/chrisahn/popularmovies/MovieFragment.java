package com.chrisahn.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieFragment extends android.support.v4.app.Fragment {

    public  static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieAdapter mMovieAdapter;
    private int mSortPosition;
    private static final String SORT_POSITION_KEY = "SORT_POSITION";
    private boolean spinnerFlag = false;

    public MovieFragment() {
        // Required empty public constructor
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
        FetchMovieTask fetchMovieTask = new FetchMovieTask();

        if (position == 0) {
            // Most Popular
            fetchMovieTask.execute("popularity.desc");
        }
        else if (position == 1) {
            // Highest Rated
            fetchMovieTask.execute("vote_average.desc");
        }
    }

    // Pass in Sort parameter value as a String
    public class FetchMovieTask extends AsyncTask<String, Void, MovieInfoContainer[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        // helper function to pull values that we want from passed in JSONstr
        // the throws is used to state that whoever uses this method has to catch the JSONexception
        private MovieInfoContainer[] getMovieDataFromJson(String movieJsonStr) throws JSONException {


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray resultsArray = movieJson.getJSONArray("results");
            // Need to get the poster_path to use for image

            //String[] posterPathArray = new String[resultsArray.length()];
            //int[] movieIdArray = new int[resultsArray.length()];

            // Simple container to hold the final required values
            MovieInfoContainer[] movieInfoContainer = new MovieInfoContainer[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject result = resultsArray.getJSONObject(i);

                // New container to set values then put them into the movieInfoContainer array
                MovieInfoContainer infoContainer = new MovieInfoContainer();

                // values from JSON
                String poster_path = result.getString("poster_path");
                String original_title = result.getString("original_title");
                String release_date = result.getString("release_date");
                int vote_average = result.getInt("vote_average");
                String overview = result.getString("overview");
                int movie_id = result.getInt("id");

                // Set values
                infoContainer.setPosterPath(poster_path);
                infoContainer.setOriginalTitle(original_title);
                infoContainer.setReleaseDate(release_date);
                infoContainer.setVoteAverage(vote_average);
                infoContainer.setOverview(overview);
                infoContainer.setId(movie_id);
                // put all set values into our array
                movieInfoContainer[i] = infoContainer;
                //movieInfoContainer[i] = new MovieInfoContainer(poster_path, id);
            }

            return movieInfoContainer;
        }

        @Override
        protected MovieInfoContainer[] doInBackground(String... params) {

            if (params.length == 0) {
                // No inputted sort value
                return null;
            }


            // declared outside of try/catch so we can close in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Contains the raw JSON response as a string
            String movieJsonStr = null;

            try {
                // Construct the URL for the movie API
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_PARAM, getString(R.string.API_KEY))
                        .build();

                URL url = new URL(uri.toString());
                Log.v(LOG_TAG, uri.toString());

                // Create the request to the Movie API, and open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // If inputStream is empty it means there is no response/nothing
                    Log.d(LOG_TAG, "inputStream is empty, meaning no response");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Adding new line is not needed for JSON
                    // However it will overall make debugging easier
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.
                    Log.d(LOG_TAG, "Buffer was Empty");
                    return null;
                }

                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e); // log errors if they occur
                // If we did not successfully get the data, do not need to go further
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // log error if we cannot close stream
                        Log.e(LOG_TAG, "Error in closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieInfoContainer[] movieInfoContainers) {
            // update the adapter with new values
            if (movieInfoContainers != null) {
                mMovieAdapter.clear();
                for (MovieInfoContainer item : movieInfoContainers) {
                    mMovieAdapter.add(item);
                }
            }
        }
    }
}

