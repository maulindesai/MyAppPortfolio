package com.maulin.popularmovies.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.maulin.popularmovies.Constants;
import com.maulin.popularmovies.database.FavouriteMovieDetail;
import com.maulin.popularmovies.database.MoviesProvider;
import com.maulin.popularmovies.model.MovieDetail;
import com.maulin.popularmovies.model.MovieReview;
import com.maulin.popularmovies.model.MovieTrailer;
import com.maulin.popularmovies.model.Movies;
import com.maulin.popularmovies.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.maulin.popularmovies.database.MovieReview.MOVIE_REVIEW_AUTHOR;
import static com.maulin.popularmovies.database.MovieReview.MOVIE_REVIEW_CONTENT;
import static com.maulin.popularmovies.database.MovieReview.MOVIE_REVIEW_ID;
import static com.maulin.popularmovies.database.MovieTrailer.MOVIE_TRAILER_KEY;
import static com.maulin.popularmovies.database.MovieTrailer.MOVIE_TRAILER_NAME;
import static com.maulin.popularmovies.database.MovieTrailer.MOVIE_TRAILER_SIZE;
import static com.maulin.popularmovies.database.MovieTrailer.MOVIE_TRAILER_TYPE;


public class FetchMovieDetailAsync extends AsyncTask<String,Void,MovieDetail> {

    private final Context mContext;
    private final Movies mMovies;
    private boolean isMovieFavorite=false;

    public FetchMovieDetailAsync(Context context, Movies movies) {
        mContext = context;
        mMovies=movies;
    }

    @Override
    protected MovieDetail doInBackground(String... params) {
        /**
         * check weather movie id is already in fav list
         * if yes then need to display offline from
         * db
         */
        if (mContext != null) {
            isMovieFavorite = checkIsMovieFavorite(params[0]);
            /**
             * get review and trailer instead of
             * adding query parameter with attach
             * String it's easy to use uri
             */
            Uri uri = Uri.parse(Constants.TMDB_URL);
            uri = uri.buildUpon().appendPath("movie")
                    .appendPath(params[0])
                    .appendQueryParameter("api_key", Constants.API_KEY)
                    .appendQueryParameter("append_to_response", "trailers,reviews")
                    .build();
            String response = utility.getUtility().downloadUrl(uri.toString());
            if (response != null) {
                try {
                    return parseResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (isMovieFavorite) {
                        return getOfflineMovieDB(params[0]);
                    } else {
                        return null;
                    }
                }
            } else {
                //user is currently offline if
                //movie mark as fav display it from db
                if (isMovieFavorite)
                    return getOfflineMovieDB(params[0]);
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * parse json response add it into model of movie detail
     * @param response network response string
     * @return movie detail object which contain trailer and review detail
     */
    private MovieDetail parseResponse(String response) throws JSONException {
        //prepare model
        MovieDetail movieDetail=new MovieDetail();
        ArrayList<MovieTrailer> trailers=movieDetail.getMovieTrailers();
        ArrayList<MovieReview> reviews=movieDetail.getMovieReviews();

        JSONObject responseJson=new JSONObject(response);
        JSONObject trailerClassObject=responseJson.getJSONObject("trailers");
        JSONArray trailerList=trailerClassObject.getJSONArray("youtube");
        //fetch all trailer list
        for(int i=0;i<trailerList.length();i++) {
            JSONObject trailer=trailerList.getJSONObject(i);

            MovieTrailer movieTrailer=new MovieTrailer();
            movieTrailer.setKey(trailer.getString("source"));
            movieTrailer.setType(trailer.getString("type"));
            movieTrailer.setName(trailer.getString("name"));
            movieTrailer.setSize(trailer.getString("size"));
            trailers.add(movieTrailer);
        }

        //fetch all review list
        JSONObject reviewClass=responseJson.getJSONObject("reviews");
        JSONArray reviewList=reviewClass.getJSONArray("results");
        for(int i=0;i<reviewList.length();i++) {
            JSONObject review=reviewList.getJSONObject(i);

            MovieReview movieReview=new MovieReview();
            movieReview.setId(review.getString("id"));
            movieReview.setAuthor(review.getString("author"));
            movieReview.setContent(review.getString("content"));
            reviews.add(movieReview);
        }
        movieDetail.setMovieTrailers(trailers);
        movieDetail.setMovieReviews(reviews);

        movieDetail.setMovieFavorite(isMovieFavorite);
        /*********************************
         * update local cache means db
         * if mark as favorite
         *************************************/
        if(isMovieFavorite)
            StoreFavoriteMovieIntentService.startAction_ADD_FAV_MOVIE(mContext,mMovies,movieDetail);
        return movieDetail;
    }

    public boolean checkIsMovieFavorite(String movieID) {
        Cursor cursor = mContext.getContentResolver().query(MoviesProvider.FavouriteMovieDetail.withId(movieID), new String[]{
                FavouriteMovieDetail.MOVIE_ID
        }, null, null, null);
        boolean result=false;
        if (cursor != null) {
            result=cursor.moveToFirst();
            cursor.close();
        }
        return result;
    }

    public MovieDetail getOfflineMovieDB(String movieID) {
        MovieDetail detail=new MovieDetail();
        //prepare model
        MovieDetail movieDetail=new MovieDetail();
        ArrayList<MovieTrailer> trailers=movieDetail.getMovieTrailers();
        ArrayList<MovieReview> reviews=movieDetail.getMovieReviews();

        //fetch trailer from db
        Cursor trailerCursor=mContext.getContentResolver()
                .query(MoviesProvider.MovieTrailer.withId(movieID),null,null,null,null);
        if(trailerCursor!=null && trailerCursor.moveToFirst()) {
            MovieTrailer movieTrailer=new MovieTrailer();
            movieTrailer.setKey(trailerCursor.getString(trailerCursor.getColumnIndex(MOVIE_TRAILER_KEY)));
            movieTrailer.setType(trailerCursor.getString(trailerCursor.getColumnIndex(MOVIE_TRAILER_TYPE)));
            movieTrailer.setName(trailerCursor.getString(trailerCursor.getColumnIndex(MOVIE_TRAILER_NAME)));
            movieTrailer.setSize(trailerCursor.getString(trailerCursor.getColumnIndex(MOVIE_TRAILER_SIZE)));
            trailers.add(movieTrailer);
            trailerCursor.close();
        } else {
            if(trailerCursor != null)
                trailerCursor.close();
        }

        //fetch review from db
        Cursor reviewCursor=mContext.getContentResolver()
                .query(MoviesProvider.MovieReview.withId(movieID),null,null,null,null);
        if(reviewCursor!=null && reviewCursor.moveToFirst()) {
            MovieReview movieReview=new MovieReview();
            movieReview.setId(reviewCursor.getString(reviewCursor.getColumnIndex(MOVIE_REVIEW_ID)));
            movieReview.setAuthor(reviewCursor.getString(reviewCursor.getColumnIndex(MOVIE_REVIEW_AUTHOR)));
            movieReview.setContent(reviewCursor.getString(reviewCursor.getColumnIndex(MOVIE_REVIEW_CONTENT)));
            reviews.add(movieReview);
            reviewCursor.close();
        } else {
            if(reviewCursor!=null)
                reviewCursor.close();
        }

        detail.setMovieFavorite(isMovieFavorite);
        detail.setMovieTrailers(trailers);
        detail.setMovieReviews(reviews);
        return detail;
    }
}
