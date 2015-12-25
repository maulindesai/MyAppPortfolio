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
public interface MovieReview {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID ="_id";
    //fk of fav movie _id field
    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.FAV_MOVIE_DETAIL,column = FavouriteMovieDetail.MOVIE_ID)
    String FK_MOVIE_ID="movie_id";

    @DataType(DataType.Type.TEXT) String MOVIE_REVIEW_AUTHOR="movie_review_author";
    @DataType(DataType.Type.TEXT) String MOVIE_REVIEW_CONTENT="movie_review_content";
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @DataType(DataType.Type.TEXT) String MOVIE_REVIEW_ID="movie_review_id";
}
