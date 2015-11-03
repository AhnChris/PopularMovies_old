package com.chrisahn.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chris on 10/20/2015.
 */
public class MovieAdapter extends ArrayAdapter<MovieInfoContainer>{

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;

    // constructor
    public MovieAdapter(Context context, List<MovieInfoContainer> movieInfoContainer) {
        super(context, 0, movieInfoContainer);
        mContext = context;
    }

    /*
     * position - The position of the item within the adapter's data set of the item whose view we want
     * convertView - The recycled view to populate
     * parent - parent ViewGroup
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the item information from the position
        MovieInfoContainer movieInfoContainer = getItem(position);

        ImageView imageView;
        if (convertView == null) {
            // not recycled, set attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            // recycled
            imageView = (ImageView) convertView;
        }

        // Build the url string
        String BASE_IMG_URL = "http://image.tmdb.org/t/p/w185/";
        Uri uri = Uri.parse(BASE_IMG_URL).buildUpon()
                .appendEncodedPath(movieInfoContainer.getPosterPath())
                .build();

        String url = uri.toString();

        Log.v(LOG_TAG, url);

        Picasso.with(mContext).load(url).error(R.drawable.error_image2).into(imageView);

        return imageView;
    }
}
