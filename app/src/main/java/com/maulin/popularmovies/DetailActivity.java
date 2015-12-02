package com.maulin.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.fragment.MovieDetailFragment;
import com.maulin.popularmovies.model.Movies;

public class DetailActivity extends AppCompatActivity {

    public static final String KEY_MOVIE = "SelectedMovie";
    private Movies mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMovies = null;
        if(savedInstanceState==null) {
            if (getIntent().getExtras() != null) {
                mMovies = getIntent().getExtras().getParcelable(KEY_MOVIE);
                //call detail fragment
                MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(mMovies);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_fragment_container, movieDetailFragment)
                        .commit();
            }
        } else {
            mMovies=savedInstanceState.getParcelable(KEY_MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE,mMovies);
    }
}
