package com.maulin.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.ui.FetchMovieDetailAsync;
import com.maulin.popularmovies.ui.StoreFavoriteMovieIntentService;
import com.maulin.popularmovies.adapters.MovieDetailAdapter;
import com.maulin.popularmovies.model.MovieDetail;
import com.maulin.popularmovies.model.MovieTrailer;
import com.maulin.popularmovies.model.Movies;

import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment implements MovieDetailAdapter.FavouriteListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_MOVIE
    private static final String ARG_ITEM_MOVIE = "selectedMovie";

    private Movies mMovieItem;
    private ListView mMovieDetailList;
    private MovieDetailAdapter movieDeatailAdapter;
    private ShareActionProvider mShareActionProvider;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 selected movie object
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movies param1) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM_MOVIE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieItem = getArguments().getParcelable(ARG_ITEM_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMovieDetailList = (ListView) inflater.inflate(R.layout.fragment_movie_detail, container, false);
        return mMovieDetailList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        //set adapter
        movieDeatailAdapter=new MovieDetailAdapter(getActivity());
        mMovieDetailList.setAdapter(movieDeatailAdapter);
        movieDeatailAdapter.setMovieItem(mMovieItem);
        mMovieDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(movieDeatailAdapter.getItemViewType(position) == MovieDetailAdapter.MOVIE_TRAILER) {
                    MovieTrailer trailer=movieDeatailAdapter.getItemMovieTrailer(position);
                    //done start trailer yippee
                    startTrailer(trailer.getTrailerURL());
                }
            }
        });

        //set click listner
        movieDeatailAdapter.setFavouriteListner(this);

        //download movie detail
        DownloadMovieDetail fetchMovieDetail=new DownloadMovieDetail(getActivity(),mMovieItem);
        fetchMovieDetail.execute(mMovieItem.getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if(shareIntent.resolveActivity(getActivity().getPackageManager())!=null) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_application_found,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * start video trailer
     * @param trailerURL trailer url
     */
    private void startTrailer(String trailerURL) {
        if(!trailerURL.isEmpty()) {
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse(trailerURL));
            if(intent.resolveActivity(getActivity().getPackageManager())!=null)
                startActivity(intent);
            else
                Toast.makeText(getActivity(), "No Activity Found For Play Video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void FavouriteMovieSelected(View view) {
       //start intent service is good option to store movie
       // data even in background. take reference
       //from http://developer.android.com/reference/android/app/IntentService.html
        if(view.getTag().equals(MovieDetailAdapter.FAVOURITE)) {
            StoreFavoriteMovieIntentService.startAction_ADD_FAV_MOVIE(getActivity(),
                    mMovieItem,
                    movieDeatailAdapter.getmMovieItemDetail());
        } else {
            StoreFavoriteMovieIntentService.removeFavriteMovie(getActivity(),mMovieItem.getId());
        }
        movieDeatailAdapter.notifyDataSetChanged();
    }

    /**
     * get movie detail
     */
     public class DownloadMovieDetail extends FetchMovieDetailAsync {


        private final View footer_view;
        private final TextView tv_network_error;
        private final ProgressBar progresBar;

        public DownloadMovieDetail(Context context, Movies movies) {
            super(context,movies);
            LayoutInflater inflater=LayoutInflater.from(context);
            footer_view=inflater.inflate(R.layout.footer_loader_view,null);
            progresBar=(ProgressBar) footer_view.findViewById(R.id.progressBar);
            tv_network_error=(TextView) footer_view.findViewById(R.id.tv_network_error);
        }

        @Override
        protected void onPreExecute() {
            mMovieDetailList.addFooterView(footer_view);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(MovieDetail movieDetail) {
            super.onPostExecute(movieDetail);
            if(isVisible()) {
                if (movieDetail == null) {
                    progresBar.setVisibility(View.INVISIBLE);
                    tv_network_error.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                } else {
                    //stop loading and remove footer
                    mMovieDetailList.removeFooterView(footer_view);

                    //set share intent
                    if (!movieDetail.getMovieTrailers().isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        MovieTrailer trailer = movieDetail.getMovieTrailers().get(0);
                        intent.putExtra(Intent.EXTRA_TEXT, "Trailer\n" + trailer.getName() + "\n" + trailer.getTrailerURL());
                        intent.setType("text/plain");
                        setShareIntent(intent);
                    }

                    //adapter for movie detail
                    movieDeatailAdapter.setMovieItemDetail(movieDetail);
                }
            }
        }
    }

    public MovieDetailAdapter getMovieDeatailAdapter() {
        return movieDeatailAdapter;
    }
}
