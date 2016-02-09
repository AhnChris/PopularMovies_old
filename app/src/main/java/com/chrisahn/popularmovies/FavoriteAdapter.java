package com.chrisahn.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.chrisahn.popularmovies.data.FavoriteColumns;
import com.squareup.picasso.Picasso;


public class FavoriteAdapter extends CursorAdapter {

    private final String LOG_TAG = FavoriteAdapter.class.getSimpleName();

    public FavoriteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 0, 0);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String BASE_IMG_URL = "http://image.tmdb.org/t/p/w185/";
        Uri uri = Uri.parse(BASE_IMG_URL).buildUpon()
                .appendEncodedPath(cursor.getString(cursor.getColumnIndex(FavoriteColumns.POSTER_PATH)))
                .build();
        String url = uri.toString();

        Log.v(LOG_TAG, "Favorite URL with Picasso: " + url);

        Picasso.with(context).load(url).error(R.drawable.error_image2).into((ImageView) view);

    }
}
