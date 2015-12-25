package com.maulin.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by maulin on 11/12/15.
 */
public class MovieDetail implements Parcelable {
    boolean isMovieFavorite=false;
    ArrayList<MovieTrailer> movieTrailers= new ArrayList<>();
    ArrayList<MovieReview> movieReviews=new ArrayList<>();

    protected MovieDetail(Parcel in) {
        movieTrailers = in.createTypedArrayList(MovieTrailer.CREATOR);
        movieReviews = in.createTypedArrayList(MovieReview.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(movieTrailers);
        dest.writeTypedList(movieReviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    public ArrayList<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(ArrayList<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
    }

    public ArrayList<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(ArrayList<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public boolean isMovieFavorite() {
        return isMovieFavorite;
    }

    public void setMovieFavorite(boolean movieFavorite) {
        isMovieFavorite = movieFavorite;
    }

    public MovieDetail(){}
}
