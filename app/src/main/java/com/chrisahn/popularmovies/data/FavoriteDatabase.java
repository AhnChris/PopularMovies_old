package com.chrisahn.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Chris on 1/31/2016.
 */

@Database(version = FavoriteDatabase.VERSION)
public final class FavoriteDatabase {

    public static final int VERSION = 1;

    @Table(FavoriteColumns.class) public static final String FAVORITES = "favorites";
}
