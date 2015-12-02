package com.maulin.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.maulin.popularmovies.Constants;

/**
 * Created by maulin on 23/11/15.
 */
public class Movies  implements Parcelable {
    String poster_path;
    String overview;
    String title;
    String vote_average;
    String release_date;

    public Movies(){}

    protected Movies(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        title = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getPoster_path() {
        //get poster path with attached url
        //TODO w185 might be change in tablets ?
        return Constants.TMDB_IMAGE_URL+"/w185/"+poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVote_average() {
        return vote_average+"/10";
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(title);
        dest.writeString(vote_average);
        dest.writeString(release_date);
    }

}
