package com.chrisahn.popularmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
            voteAverage.setText(mMovieInfoContainer.getVoteAverage()+"/10");
            plot.setText(mMovieInfoContainer.getOverview());

            String URL = "http://image.tmdb.org/t/p/w185/"+ mMovieInfoContainer.getPosterPath();
            Picasso.with(getActivity()).load(URL).error(R.drawable.error_image2).into(posterImage);
        }

        return rootView;
    }
}
