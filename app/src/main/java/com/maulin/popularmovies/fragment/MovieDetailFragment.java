package com.maulin.popularmovies.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.model.Movies;

import org.w3c.dom.Text;

public class MovieDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_MOVIE
    private static final String ARG_ITEM_MOVIE = "selectedMovie";

    private Movies mMovieItem;
    private ImageView mMoviePosterImageView;
    private TextView mMoviePosterTitle;
    private TextView mMovieRating;
    private TextView mMovieReleaseDate;
    private TextView mMoviePlot;

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
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_thumb_movie_poster);
        mMoviePosterTitle = (TextView)view.findViewById(R.id.movie_title);
        mMovieRating = (TextView) view.findViewById(R.id.tv_movie_rating);
        mMovieReleaseDate = (TextView) view.findViewById(R.id.tv_release_date);
        mMoviePlot = (TextView) view.findViewById(R.id.tv_movie_plot);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this)
                .load(mMovieItem.getPoster_path())
                .crossFade()
                .into(mMoviePosterImageView);

        mMoviePosterTitle.setText(mMovieItem.getTitle());
        mMoviePlot.setText(mMovieItem.getOverview());
        mMovieRating.setText(mMovieItem.getVote_average());
        mMovieReleaseDate.setText(mMovieItem.getRelease_date());
    }

}
