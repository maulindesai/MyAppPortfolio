package com.maulin.popularmovies.ui;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.maulin.popularmovies.database.MovieReview;
import com.maulin.popularmovies.database.MovieTrailer;
import com.maulin.popularmovies.database.MoviesProvider;
import com.maulin.popularmovies.fragment.SettingsFragment;
import com.maulin.popularmovies.model.MovieDetail;
import com.maulin.popularmovies.model.Movies;

import java.util.ArrayList;

import static com.maulin.popularmovies.database.FavouriteMovieDetail.*;
import static com.maulin.popularmovies.provider.MoviesProvider.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class StoreFavoriteMovieIntentService extends IntentService {

    private static final String ACTION_ADD_FAV_MOVIE = "com.maulin.popularmovies.action.addFavoriteMovie";
    private static final String ACTION_REMOVE_FAV_MOVIE = "com.maulin.popularmovies.action.removeFavoriteMovie";
    private static final String EXTRA_MOVIES = "com.maulin.popularmovies.extra.MoviesInfo";
    //movie detail like trailer and review
    private static final String EXTRA_MOVIE_DETAIL = "com.maulin.popularmovies.extra.MovieDetails";
    private static final String EXTRA_MOVIE_ID = "com.maulin.popularmovies.extra.MovieID";


    public StoreFavoriteMovieIntentService() {
        super("StoreFavoriteMovieIntentService");
    }

    /**
     * Starts this service to perform action add fav movie with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startAction_ADD_FAV_MOVIE(Context context, Movies movies, MovieDetail movieDetail) {
        Intent intent = new Intent(context, StoreFavoriteMovieIntentService.class);
        intent.setAction(ACTION_ADD_FAV_MOVIE);
        intent.putExtra(EXTRA_MOVIES,movies);
        intent.putExtra(EXTRA_MOVIE_DETAIL,movieDetail);
        context.startService(intent);
    }

    /**
     * remove fav movie by user
     *
     */
    public static void removeFavriteMovie(Context context,String movieID) {
        Intent intent = new Intent(context, StoreFavoriteMovieIntentService.class);
        intent.setAction(ACTION_REMOVE_FAV_MOVIE);
        intent.putExtra(EXTRA_MOVIE_ID,movieID);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD_FAV_MOVIE.equals(action)) {
                final Movies movie = intent.getParcelableExtra(EXTRA_MOVIES);
                final MovieDetail movieDetail = intent.getParcelableExtra(EXTRA_MOVIE_DETAIL);
                try {
                    handleAction_ADD_FAV_MOVIE(movie, movieDetail);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            } else if(ACTION_REMOVE_FAV_MOVIE.endsWith(action)) {
                final String Movie_id=intent.getStringExtra(EXTRA_MOVIE_ID);
                try {
                    handleAction_REMOVE_FAV_MOVIE(Movie_id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * remove favourite movie
     * @param movie_id movie id which is unique
     */
    private void handleAction_REMOVE_FAV_MOVIE(String movie_id) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> operations=new ArrayList<>();
        operations.add(ContentProviderOperation.newDelete(MoviesProvider.FavouriteMovieDetail.withId(movie_id))
                .build());
       //remove trailer and review
        operations.add(ContentProviderOperation.newDelete(MoviesProvider.MovieReview.withId(movie_id))
                .build());
        operations.add(ContentProviderOperation.newDelete(MoviesProvider.MovieTrailer.withId(movie_id))
                .build());

        getContentResolver().applyBatch(AUTHORITY,operations);
    }

    /**
     * Handle action ADD_FAV_MOVIE in the provided background thread with the provided
     * parameters.
     */
    private void handleAction_ADD_FAV_MOVIE(Movies movie, MovieDetail movieDetail) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> operations=new ArrayList<>();
        //add FAV movie
        //add movie detail
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
         String sortOrder= sharedPref.getString(SettingsFragment.KEY_SORT_ORDER, "most_popular");
        ContentValues contentValues=null;
        /**
         * insert movie detail only when user
         * select other then favourite sort because
         * right now favourite sort movie detail
         * are coming from db cache not from web api
         */
        if(!sortOrder.equals("sort_as_favourite")) {
            contentValues = new ContentValues();
            contentValues.put(MOVIE_ID, movie.getId());
            contentValues.put(MOVIE_OVERVIEW, movie.getOverview());
            contentValues.put(MOVIE_TITLE, movie.getTitle());
            contentValues.put(POSTER_PATH, movie.getPoster_path());
            contentValues.put(MOVIE_VOTE_AVERAGE, movie.vote_average);
            contentValues.put(MOVIE_RELEASE_DATE, movie.getRelease_date());
            operations.add(ContentProviderOperation.newInsert(MoviesProvider.FavouriteMovieDetail.CONTENT_URI)
                    .withValues(contentValues).build());
        }
        //add movie trailer
        for(com.maulin.popularmovies.model.MovieTrailer trailer:movieDetail.getMovieTrailers()) {
            contentValues = new ContentValues();
            contentValues.put(MovieTrailer.MOVIE_TRAILER_NAME, trailer.getName());
            contentValues.put(MovieTrailer.MOVIE_TRAILER_KEY,trailer.getKey());
            contentValues.put(MovieTrailer.MOVIE_TRAILER_SIZE, trailer.getSize());
            contentValues.put(MovieTrailer.MOVIE_TRAILER_TYPE, trailer.getType());
            contentValues.put(MovieTrailer.FK_MOVIE_ID,movie.getId());
            operations.add(ContentProviderOperation.newInsert(MoviesProvider.MovieTrailer.CONTENT_URI)
                    .withValues(contentValues).build());
        }

        //add movie review
        for(com.maulin.popularmovies.model.MovieReview review:movieDetail.getMovieReviews()) {
            contentValues = new ContentValues();
            contentValues.put(MovieReview.MOVIE_REVIEW_AUTHOR, review.getAuthor());
            contentValues.put(MovieReview.MOVIE_REVIEW_CONTENT,review.getContent());
            contentValues.put(MovieReview.MOVIE_REVIEW_ID,review.getId());
            contentValues.put(MovieTrailer.FK_MOVIE_ID,movie.getId());
            operations.add(ContentProviderOperation.newInsert(MoviesProvider.MovieReview.CONTENT_URI)
                    .withValues(contentValues).build());
        }

        getContentResolver().applyBatch(AUTHORITY,operations);
    }
}
