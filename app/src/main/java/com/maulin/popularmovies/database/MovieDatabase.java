package com.maulin.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by maulin on 16/12/15.
 */
@Database(version = MovieDatabase.VERSION,packageName = "com.maulin.popularmovies.provider")
public class MovieDatabase {
    public static final int VERSION=1;

    @Table(FavouriteMovieDetail.class) @IfNotExists
    public static final String FAV_MOVIE_DETAIL="fav_movie_detail";
    @Table(MovieReview.class) @IfNotExists
    public static final String MOVIE_REVIEW="movie_review";
    @Table(MovieTrailer.class) @IfNotExists
    public static final String MOVIE_TRAILER="movie_trailer";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }
    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
    }
    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

}
