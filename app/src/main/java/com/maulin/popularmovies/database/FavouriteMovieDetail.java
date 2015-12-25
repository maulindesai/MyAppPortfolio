package com.maulin.popularmovies.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by maulin on 16/12/15.
 */
public interface FavouriteMovieDetail {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID ="_id";
    //fk of fav movie _id field
    @DataType(DataType.Type.INTEGER)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    String MOVIE_ID="movie_id";

    @DataType(DataType.Type.TEXT) String POSTER_PATH="movie_poster_path";
    @DataType(DataType.Type.TEXT) String MOVIE_OVERVIEW="movie_overview";
    @DataType(DataType.Type.TEXT) String MOVIE_TITLE="movie_title";
    @DataType(DataType.Type.TEXT) String MOVIE_VOTE_AVERAGE="movie_vote_average";
    @DataType(DataType.Type.TEXT) String MOVIE_RELEASE_DATE="movie_release_date";
}
