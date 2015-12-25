package com.maulin.popularmovies.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.Constants;
import com.maulin.popularmovies.adapters.MovieListAdapter;
import com.maulin.popularmovies.database.FavouriteMovieDetail;
import com.maulin.popularmovies.database.MoviesProvider;
import com.maulin.popularmovies.model.Movies;
import com.maulin.popularmovies.ui.MainActivity;
import com.maulin.popularmovies.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * Activities that contain this fragment must implement the
 * {@link com.maulin.popularmovies.fragment.MovieListFragment.OnMovieItemClickListener} interface
 * to handle interaction events.
 *
 */
public class MovieListFragment extends Fragment implements AdapterView.OnItemClickListener,
            LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MovieListFragment";
    private static final String KEY_MOVIE_LIST = "MovieList";
    private static final String KEY_SORT_ORDER = "movie_sort_order";
    private static final String KEY_SELECTED_MOVIE_POSITION = "key_selected_movie_position";
    private static final String KEY_GRIDVIEW_SCROLL_POSITION = "key_gridview_scrollposition";
    private static final int FAVOURITE_LOADER_ID = 0;
    private OnMovieItemClickListener mListener;
    private GridView mMovieListGridView;
    private MovieListAdapter mMovieListAdapter;
    private String mSort_order="";
    private ProgressBar mProgressBar;
    private int mSelectedMoviePosition=0;
    private utility mUtility;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_movie_list, container, false);
        mMovieListGridView= (GridView) view.findViewById(R.id.movieList_grid);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get utility
        mUtility=utility.getUtility();

        //set item click listener
        mMovieListGridView.setOnItemClickListener(this);

        if(savedInstanceState!=null) {
            ArrayList<Movies> moviesArrayList=savedInstanceState.getParcelableArrayList(KEY_MOVIE_LIST);
            //restore previous user selected position
            mSelectedMoviePosition=savedInstanceState.getInt(KEY_SELECTED_MOVIE_POSITION,0);

            //restore sort order
            mSort_order=savedInstanceState.getString(KEY_SORT_ORDER,"most_popular");
            mMovieListAdapter=new MovieListAdapter(getActivity(),moviesArrayList);
            mMovieListGridView.setAdapter(mMovieListAdapter);

            //restore to selected item
            if(((MainActivity)getActivity()).isTabletMode)
                mMovieListGridView.performItemClick(mMovieListAdapter.getView(0,null,null),
                        mSelectedMoviePosition,0);

            //smooth scroll grid view
            mMovieListGridView.smoothScrollToPosition(savedInstanceState.getInt(KEY_SELECTED_MOVIE_POSITION,0));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //check weather config change and android restore its state
        if(mMovieListAdapter==null) {
            mSort_order = sharedPref.getString(SettingsFragment.KEY_SORT_ORDER, "most_popular");
            //create adapter
            mMovieListAdapter=new MovieListAdapter(getActivity(),new ArrayList<Movies>());
            mMovieListGridView.setAdapter(mMovieListAdapter);
            fetchTmdbMovies(mSort_order);
        } else {
            //check if there is change in user movie sort order
            //if yes then call service other wise config changes no
            //need to hit web service
            String sort_order=sharedPref.getString(SettingsFragment.KEY_SORT_ORDER, "most_popular");
            if(!mSort_order.equals(sort_order)) {
                //clear previous list to get latest sort
                //order movies
                mSort_order=sort_order;
                mMovieListAdapter.clear();
                fetchTmdbMovies(sort_order);
            }
        }
    }

    /**
     * call asyctask to fetch data
     * @param sort_order movie sort order
     */
    private void fetchTmdbMovies(String sort_order) {
        mProgressBar.setVisibility(View.VISIBLE);
        //start download movie poster
        FetchData fetchData = new FetchData();
        switch (sort_order) {
            case "most_popular":
                fetchData.execute(Constants.TMDB_DISCOVER_MOVIE + "&sort_by=popularity.desc");
                break;
            case "highest_rated":
                fetchData.execute(Constants.TMDB_DISCOVER_MOVIE + "&sort_by=vote_average.desc");
                break;
            default:
                ((AppCompatActivity) getActivity())
                        .getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, MovieListFragment.this);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mMovieListAdapter!=null) {
            ArrayList<Movies> moviesArrayList=mMovieListAdapter.getValues();
            if(moviesArrayList!=null)
                outState.putParcelableArrayList(KEY_MOVIE_LIST,moviesArrayList);
        }
        //store current sort order
        outState.putString(KEY_SORT_ORDER,mSort_order);
        //store current selected movie position
        outState.putInt(KEY_SELECTED_MOVIE_POSITION,mSelectedMoviePosition);
        //store current scroll position
        outState.putInt(KEY_GRIDVIEW_SCROLL_POSITION,mMovieListGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMovieItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedMoviePosition=position;
        Movies movies= (Movies) mMovieListGridView.getItemAtPosition(position);
        mListener.onMovieItemClick(movies);
    }

    /**
     * setup loader to load and display offline fav movie list
     * @param id loader id
     * @param args loader argument
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesProvider.FavouriteMovieDetail.CONTENT_URI,null
                    ,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.clear();
        ArrayList<Movies> moviesArrayList=new ArrayList<>();
        if(data.moveToFirst()) {
            do {
                Movies movies = new Movies();
                movies.setId(data.getString(data.getColumnIndex(FavouriteMovieDetail.MOVIE_ID)));
                movies.setTitle(data.getString(data.getColumnIndex(FavouriteMovieDetail.MOVIE_TITLE)));
                movies.setOverview(data.getString(data.getColumnIndex(FavouriteMovieDetail.MOVIE_OVERVIEW)));
                movies.setPoster_path(data.getString(data.getColumnIndex(FavouriteMovieDetail.POSTER_PATH)));
                movies.setRelease_date(data.getString(data.getColumnIndex(FavouriteMovieDetail.MOVIE_RELEASE_DATE)));
                movies.setVote_average(data.getString(data.getColumnIndex(FavouriteMovieDetail.MOVIE_VOTE_AVERAGE)));
                moviesArrayList.add(movies);
            } while (data.moveToNext());
        }
        mMovieListAdapter.addAll(moviesArrayList);

        if(mProgressBar!=null)
            mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG,"loader reset ");
    }

    /**
     * call back interface
     */
    public interface OnMovieItemClickListener {
        public void onMovieItemClick(Movies movie);
    }

    /**
     * fetch data from tmdb assign asyncTask
     */
    public class FetchData extends AsyncTask<String,Void,ArrayList<Movies>> {

        @Override
        protected ArrayList<Movies> doInBackground(String... Url) {
            //download url
            String response =mUtility.downloadUrl(Url[0]);
            if(response!=null) {
                try {
                    //parse tmdb json
                    return getMovieLists(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> movieList) {
            super.onPostExecute(movieList);
            if(movieList!=null) {
                //add and notify adapter s
                mMovieListAdapter.addAll(movieList);
                //display coach mark in tablet mode

            } else {
                Toast.makeText(getActivity(), R.string.network_error_msg,Toast.LENGTH_SHORT).show();
                Log.d(TAG,"no movie list found");
            }
            if(mProgressBar!=null)
                mProgressBar.setVisibility(View.GONE);
        }
    }

    //get TMDB discover movie response
    private ArrayList<Movies> getMovieLists(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        ArrayList<Movies> moviesArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieClass = jsonArray.getJSONObject(i);
            Movies movies = new Movies();
            movies.setId(movieClass.getString("id"));
            movies.setTitle(movieClass.getString("original_title"));
            movies.setOverview(movieClass.getString("overview"));
            movies.setPoster_path(movieClass.getString("poster_path"));
            movies.setRelease_date(movieClass.getString("release_date"));
            movies.setVote_average(movieClass.getString("vote_average"));
            moviesArrayList.add(movies);
        }
        return moviesArrayList;
    }
}
