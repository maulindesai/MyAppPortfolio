package com.maulin.popularmovies.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by maulin on 16/12/15.
 */
@ContentProvider(authority = MoviesProvider.AUTHORITY,database = MovieDatabase.class,packageName = "com.maulin.popularmovies.provider")
public class MoviesProvider {

    public static final String AUTHORITY="com.maulin.popularmovies.MoviesProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    interface Path {
        String MOVIE_DETAIL="selected_movie_detail";
        String MOVIE_TRAILER="selected_movie_trailer";
        String MOVIE_REVIEW="selected_movie_review";
    }
    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.FAV_MOVIE_DETAIL) public static class FavouriteMovieDetail {

        @ContentUri(
                path=Path.MOVIE_DETAIL,
                type = "vnd.android.cursor.dir/movieDetails"
        )
        public static Uri CONTENT_URI=buildUri(Path.MOVIE_DETAIL);

        @InexactContentUri(
                name = "selectedMOVIEDetail",
                path = Path.MOVIE_DETAIL+"/#",
                type = "vnd.android.cursor.item/favourite_movie",
                whereColumn= com.maulin.popularmovies.database.FavouriteMovieDetail.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(String id) {
            return buildUri(Path.MOVIE_DETAIL, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.MOVIE_TRAILER) public static class MovieTrailer {

        @ContentUri(
                path=Path.MOVIE_TRAILER,
                type = "vnd.android.cursor.dir/movieTrailer"
        )
        public static Uri CONTENT_URI=buildUri(Path.MOVIE_TRAILER);

        @InexactContentUri(
                name = "selectedMOVIETrailer",
                path = Path.MOVIE_TRAILER+"/#",
                type = "vnd.android.cursor.item/favourite_movie_trailer",
                whereColumn= com.maulin.popularmovies.database.MovieTrailer.FK_MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(String id) {
            return buildUri(Path.MOVIE_TRAILER, id);
        }
    }

    @TableEndpoint(table = MovieDatabase.MOVIE_REVIEW) public static class MovieReview {

        @ContentUri(
                path=Path.MOVIE_REVIEW,
                type = "vnd.android.cursor.dir/movieReview"
        )
        public static Uri CONTENT_URI=buildUri(Path.MOVIE_REVIEW);

        @InexactContentUri(
                name = "selectedMOVIEReview",
                path = Path.MOVIE_REVIEW+"/#",
                type = "vnd.android.cursor.item/favourite_movie_review",
                whereColumn= com.maulin.popularmovies.database.MovieReview.FK_MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(String id) {
            return buildUri(Path.MOVIE_REVIEW, id);
        }
    }
}
