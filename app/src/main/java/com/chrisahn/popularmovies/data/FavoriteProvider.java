package com.chrisahn.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Chris on 1/31/2016.
 */

@ContentProvider(authority = FavoriteProvider.AUTHORITY, database = FavoriteDatabase.class)
public class FavoriteProvider {

    public static final String AUTHORITY = "com.chrisahn.popularmovies.data.FavoriteProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITES = "favorites";
    }

    // helper function to build Uri with BASE_CONTENT_URI
    private static Uri buildUri (String ... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String item : paths) {
            builder.appendPath(item);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavoriteDatabase.FAVORITES) public static class Favorites {
        // @ContentUri annotation is used for Uri that do not change
        @ContentUri(
                path = Path.FAVORITES,
                type = "vnd.android.cursor.dir/favorite"
        )
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITES);

        // @InexactContentUri is used for a Uri that is created based on some kind of value
        @InexactContentUri(
                path = Path.FAVORITES + "/#",
                name = "FAVORITE_ID",
                type = "vnd.android.cursor.item/favorite",
                whereColumn = FavoriteColumns._ID,
                pathSegment = 1)
        public static Uri withId (long id) {
            return buildUri(Path.FAVORITES, String.valueOf(id));
        }
    }
}
