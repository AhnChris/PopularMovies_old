package com.chrisahn.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testInsertDelete() throws Throwable {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteColumns.MOVIE_ID, 12345);
        cv.put(FavoriteColumns.POSTER_PATH, "/posterpath");

        getContext().getContentResolver().delete(FavoriteProvider.Favorites.CONTENT_URI, null, null);
        Cursor c = getContext().getContentResolver().query(FavoriteProvider.Favorites.CONTENT_URI,
                null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        c = getContext().getContentResolver().query(FavoriteProvider.Favorites.withMovieId(12345),
                null, null, null, null);
        assertFalse(c.moveToFirst());
        c.close();

        getContext().getContentResolver().insert(FavoriteProvider.Favorites.CONTENT_URI, cv);
        c = getContext().getContentResolver().query(FavoriteProvider.Favorites.withMovieId(12345),
                null, null, null, null);
        assertTrue(c.moveToFirst());

        int n = c.getInt(c.getColumnIndex(FavoriteColumns.MOVIE_ID));
        assertEquals(12345, n);

        String s = c.getString(c.getColumnIndex(FavoriteColumns.POSTER_PATH));
        assertEquals("/posterpath", s);

        c.close();

        getContext().getContentResolver().delete(FavoriteProvider.Favorites.withMovieId(12345), null, null);
        c = getContext().getContentResolver().query(FavoriteProvider.Favorites.withMovieId(12345),
                    null, null, null, null);
        assertFalse(c.moveToFirst());
    }
}
