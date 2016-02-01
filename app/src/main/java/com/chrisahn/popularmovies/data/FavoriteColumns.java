package com.chrisahn.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Chris on 1/31/2016.
 */
public interface FavoriteColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.INTEGER) @NotNull public static final String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT) public static final String ORIGINAL_TITLE = "original_title";
    @DataType(DataType.Type.TEXT) public static final String POSTER_PATH = "poster_path";
    @DataType(DataType.Type.TEXT) public static final String OVERVIEW = "overview";
    @DataType(DataType.Type.INTEGER) public static final String RELEASE_DATE = "release_date";
    @DataType(DataType.Type.INTEGER) public static final String VOTE_AVERAGE = "vote_average";
    @DataType(DataType.Type.TEXT) public static final String REVIEW = "review";
    @DataType(DataType.Type.TEXT) public static final String TRAILER = "trailer";
}
