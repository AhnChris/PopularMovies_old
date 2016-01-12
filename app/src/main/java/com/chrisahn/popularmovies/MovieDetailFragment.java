package com.chrisahn.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieDetailFragment extends android.support.v4.app.Fragment {

    private MovieInfoContainer mMovieInfoContainer;
    static final String MOVIE_DATA = "MOVIE_DATA";
    private String mSource;
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.movieTitleView) TextView title;
    @Bind(R.id.releaseDateView) TextView releaseDate;
    @Bind(R.id.voteAverageView) TextView voteAverage;
    @Bind(R.id.posterImageView) ImageView posterImage;
    @Bind(R.id.plotView) TextView plot;
    @Bind(R.id.trailerView) TextView trailer;
    @Bind(R.id.reviewTextView) TextView review;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // grab intent data from the MovieDetailActivity
        /*Bundle bundle = getArguments();
        MovieInfoContainer movieInfoContainer = bundle.getParcelable("key");
        mMovieInfoContainer = movieInfoContainer;*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        // Using Butterknife here to make it a little cleaner in this block
        ButterKnife.bind(this, rootView);

        // Get the intent data from the Bundle that was passed from MovieDetailActivity
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieInfoContainer = bundle.getParcelable(MovieDetailFragment.MOVIE_DATA);
            // Set views with corresponding values obtained from the Bundle
            title.setText(mMovieInfoContainer.getOriginalTitle());
            releaseDate.setText(mMovieInfoContainer.getReleaseDate());
            voteAverage.setText(mMovieInfoContainer.getVoteAverage() + "/10");
            plot.setText(mMovieInfoContainer.getOverview());

            String URL = "http://image.tmdb.org/t/p/w185/" + mMovieInfoContainer.getPosterPath();
            Picasso.with(getActivity()).load(URL).error(R.drawable.error_image2).into(posterImage);

            FetchTrailerReview fetchTrailerReview = new FetchTrailerReview();
            fetchTrailerReview.execute(mMovieInfoContainer.getId());

            trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playTrailer(mSource);
                }
            });
        }

        return rootView;
    }

    // function to launch youtube intent
    public void playTrailer(String source) {
        try {
            // launch youtube app if available
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + source));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // if there is no youtube app available, launch with browser
            Intent intent = new Intent (Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + source));
            startActivity(intent);
        }
    }

    public class FetchTrailerReview extends AsyncTask<Integer, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchTrailerReview.class.getSimpleName();



        // helper function to grab trailer and review
        private ArrayList<String> getJsonData(String jsonStr) throws JSONException {

            ArrayList<String> arrayList = new ArrayList<String>();

            JSONObject json = new JSONObject(jsonStr);
            JSONObject trailers = json.getJSONObject("trailers");
            JSONObject reviews = json.getJSONObject("reviews");

            JSONArray youtube = trailers.getJSONArray("youtube");
            JSONObject youtubeArray = youtube.getJSONObject(0);
            String youtubeSource = youtubeArray.getString("source");
            Log.v(LOG_TAG, "youtube source: " + youtubeSource);

            JSONArray results = reviews.getJSONArray("results");
            JSONObject resultsArray = results.getJSONObject(0);
            String content = resultsArray.getString("content");
            Log.v(LOG_TAG, "content review: " + content);

            arrayList.add(youtubeSource);
            arrayList.add(content);
            return arrayList;
        }



        protected ArrayList<String> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection =  null;
            BufferedReader bufferedReader = null;

            String JsonTrailerReviewStr = null;

            try {
                // Build URL
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";
                final String APPEND_PARAM = "append_to_response";

                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendEncodedPath(params[0].toString())
                        .appendQueryParameter(API_PARAM, getString(R.string.API_KEY))
                        .appendQueryParameter(APPEND_PARAM, "trailers,reviews")
                        .build();
                URL url = new URL(uri.toString());
                Log.v(LOG_TAG, uri.toString());

                // open connection with built URL
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    // append new line for readability
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer == null) {
                    // empty response
                    Log.e(LOG_TAG, "stingBuffer is empty");
                    return null;
                }

                // non-empty response
                JsonTrailerReviewStr = stringBuffer.toString();
                Log.v(LOG_TAG, "JSON RESPONSE: " + JsonTrailerReviewStr);


            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                // close connections/stream

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (final IOException e) {
                    // could not close stream
                    Log.e(LOG_TAG, "Error in closing stream ", e);
                }
            }

            try {
                return getJsonData(JsonTrailerReviewStr);
            } catch (final JSONException e) {
                Log.e(LOG_TAG, "JSON Exception", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            mSource = strings.get(0);

            if (strings.get(1) == null) {
                // no reviews update textview with "No reviews available"
                review.setText("No reviews available.");
            } else {
                // if there is a review update textview with the review
                review.setText(strings.get(1));
            }
        }
    }
}
