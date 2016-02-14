package com.chrisahn.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

// Pass in Sort parameter value as a String
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieInfoContainer>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    public MovieAdapter mMovieAdapter;
    private Context mContext;

    public FetchMovieTask (Context context, MovieAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }


    // helper function to pull values that we want from passed in JSONstr
    // the throws is used to state that whoever uses this method has to catch the JSONexception
    private ArrayList<MovieInfoContainer> getMovieDataFromJson(String movieJsonStr) throws JSONException {


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray("results");
        // Need to get the poster_path to use for image

        //String[] posterPathArray = new String[resultsArray.length()];
        //int[] movieIdArray = new int[resultsArray.length()];

        // Simple container to hold the final required values
        MovieInfoContainer[] movieInfoContainer = new MovieInfoContainer[resultsArray.length()];

        // ArrayList of MovieInfoContainer
        ArrayList<MovieInfoContainer> movieInfoContainerArrayList = new ArrayList<>();

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
            movieInfoContainerArrayList.add(infoContainer);
            //movieInfoContainer[i] = new MovieInfoContainer(poster_path, id);
        }

        return movieInfoContainerArrayList;
    }

    @Override
    protected ArrayList<MovieInfoContainer> doInBackground(String... params) {

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
                    .appendQueryParameter(API_PARAM, mContext.getString(R.string.API_KEY))
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
    protected void onPostExecute(ArrayList<MovieInfoContainer> movieInfoContainers) {
        // update the adapter with new values
        mMovieAdapter.clear();
        for (MovieInfoContainer item : movieInfoContainers) {
            mMovieAdapter.add(item);
        }
    }
}
