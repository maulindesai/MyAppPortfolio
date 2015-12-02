package com.maulin.popularmovies.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.maulin.popularmovies.model.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * Activities that contain this fragment must implement the
 * {@link com.maulin.popularmovies.fragment.MovieListFragment.OnMovieItemClickListener} interface
 * to handle interaction events.
 *
 */
public class MovieListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "MovieListFragment";
    private static final String KEY_MOVIE_LIST = "MovieList";
    private static final String KEY_SORT_ORDER = "movie_sort_order";
    private OnMovieItemClickListener mListener;
    private GridView mMovieListGridView;
    private MovieListAdapter mMovieListAdapter;
    private String mSort_order="";
    private ProgressBar mProgressBar;

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
        //set item click listener
        mMovieListGridView.setOnItemClickListener(this);

        if(savedInstanceState!=null) {
            ArrayList<Movies> moviesArrayList=savedInstanceState.getParcelableArrayList(KEY_MOVIE_LIST);
            //restore sort order
            mSort_order=savedInstanceState.getString(KEY_SORT_ORDER,"most_popular");
            mMovieListAdapter=new MovieListAdapter(getActivity(),moviesArrayList);
            mMovieListGridView.setAdapter(mMovieListAdapter);
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
        if(sort_order.equals("most_popular"))
            fetchData.execute(Constants.TMDB_DISCOVER_MOVIE+"&sort_by=popularity.desc");
        else
            fetchData.execute(Constants.TMDB_DISCOVER_MOVIE+"&sort_by=vote_average.desc");
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
        Movies movies= (Movies) mMovieListGridView.getItemAtPosition(position);
        mListener.onMovieItemClick(movies);
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
            String response =downloadUrl(Url[0]);
            if(response!=null) {
                try {
                    //parse tmdb json
                    return getMovieLists(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                Toast.makeText(getActivity(), R.string.network_error_msg,Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> movieList) {
            super.onPostExecute(movieList);
            if(movieList!=null) {
                //add and notify adapter
                mMovieListAdapter.addAll(movieList);
            } else {
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
            movies.setTitle(movieClass.getString("original_title"));
            movies.setOverview(movieClass.getString("overview"));
            movies.setPoster_path(movieClass.getString("poster_path"));
            movies.setRelease_date(movieClass.getString("release_date"));
            movies.setVote_average(movieClass.getString("vote_average"));
            moviesArrayList.add(movies);
        }
        return moviesArrayList;
    }

    /**
     * download url
     */
    private String downloadUrl(String tmdb_url) {
        InputStream inputStream=null;
        try {
            URL url=new URL(tmdb_url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            //check status code
            int responseCode=httpURLConnection.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK) {
                inputStream=httpURLConnection.getInputStream();
                //pares input stream
                return readIt(inputStream);
            } else {
                Log.d(TAG,responseCode+"");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(inputStream!=null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * convert input stream to string
     * @param inputStream inputStream
     */
    private String readIt(InputStream inputStream) {
        InputStreamReader reader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(reader);
        StringBuilder builder=new StringBuilder();
        String thisLine="";
        try {
            while((thisLine=bufferedReader.readLine())!=null){
                builder.append(thisLine+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}
