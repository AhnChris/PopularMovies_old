package com.chrisahn.popularmovies;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieDetailFragment extends android.support.v4.app.Fragment {

    private MovieInfoContainer mMovieInfoContainer;
    static final String MOVIE_DATA = "MOVIE_DATA";

    @Bind(R.id.movieTitleView) TextView title;
    @Bind(R.id.releaseDateView) TextView releaseDate;
    @Bind(R.id.voteAverageView) TextView voteAverage;
    @Bind(R.id.posterImageView) ImageView posterImage;
    @Bind(R.id.plotView) TextView plot;

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
        }

        return rootView;
    }

    public class FetchTrailerReview extends AsyncTask<Integer, Void, Void> {

        private final String LOG_TAG = FetchTrailerReview.class.getSimpleName();



        protected Void doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection =  null;
            BufferedReader bufferedReader = null;

            String JsonTrailerReviewStr = null;

            try {
                // Build URL
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String ID_PARAM = "id";
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
            return null;
        }
    }
}
