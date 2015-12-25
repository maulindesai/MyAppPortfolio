package com.maulin.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.maulin.popularmovies.Constants;

/**
 * Created by maulin on 11/12/15.
 */
public class MovieTrailer implements Parcelable {

    String movie_id;
    String key;
    String name;
    String size;
    String type;

    public MovieTrailer(){}

    protected MovieTrailer(Parcel in) {
        movie_id = in.readString();
        key = in.readString();
        name = in.readString();
        size = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movie_id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(size);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * get Youtube Url
     */
    public String getTailerThumbURL() {
        if(getKey()!=null)
            return Constants.YOUTUBE_THUMB_URL+getKey()+"/0.jpg";
        else
            return null;
    }

    /**
     * get Youtubee Video URL
     */
    public String getTrailerURL() {
        if(getKey()!=null)
            return Constants.YOUTUBE_URL+"?v="+getKey();
        else
            return null;
    }
}
