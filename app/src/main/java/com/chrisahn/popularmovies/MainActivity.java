package com.chrisahn.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback{

    private boolean isTablet;
    private final static String MOVIEDETAILFRAGMENT_TAG = "MDFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container_detail) != null) {
            // If we find this view, then we are on a tablet device
            isTablet = true;

            if (savedInstanceState == null) {
                // If building from new, place MovieDetailFragment in the container with a tag
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, new MovieDetailFragment(), MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            // Not on a tablet device
            isTablet = false;
        }
    }

    @Override
    public void onItemSelected(MovieInfoContainer movieData) {
        if (isTablet) {
            // If on tablet device, show detail views by replacing the MovieDetailFragment. Use
            // DETAILFRAGMENT_TAG, so it grabs the same one
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.MOVIE_DATA, movieData);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_detail, movieDetailFragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            // If not on tablet device. Make an intent passed in with necessary data to send to
            // DetailActivity
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.MOVIE_DATA, movieData);
            startActivity(intent);
        }
    }
}
