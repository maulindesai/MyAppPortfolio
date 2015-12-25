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
public interface MovieTrailer {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID ="_id";
    //fk of fav movie _id field
    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.FAV_MOVIE_DETAIL,column = FavouriteMovieDetail.MOVIE_ID)
    String FK_MOVIE_ID="movie_id";
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @DataType(DataType.Type.TEXT) String MOVIE_TRAILER_KEY="movie_trailer_key";
    @DataType(DataType.Type.TEXT) String MOVIE_TRAILER_NAME="movie_trailer_name";
    @DataType(DataType.Type.TEXT) String MOVIE_TRAILER_SIZE="movie_trailer_size";
    @DataType(DataType.Type.TEXT) String MOVIE_TRAILER_TYPE="movie_trailer_type";
}
