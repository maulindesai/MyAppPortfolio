package com.maulin.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.adapters.MovieDetailAdapter;
import com.maulin.popularmovies.fragment.MovieDetailFragment;
import com.maulin.popularmovies.fragment.MovieListFragment;
import com.maulin.popularmovies.model.MovieTrailer;
import com.maulin.popularmovies.model.Movies;

public class MainActivity extends AppCompatActivity implements
        MovieListFragment.OnMovieItemClickListener{

    private static final String TAG = "MainActivity";
    public boolean isTabletMode=false;
    private View mCoachMarkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get coach mark view
        mCoachMarkView=findViewById(R.id.coachMarkView);

        // in tablet device
        isTabletMode = findViewById(R.id.detail_fragment_container) != null;
    }

    @Override
    public void onMovieItemClick(Movies movie) {
        //call movie detail activity which display movie detail
        if(!isTabletMode) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.KEY_MOVIE, movie);
            startActivity(intent);
        } else {
            //hide select movie coach mark
            mCoachMarkView.setVisibility(View.GONE);

            //load detail fragment instead of calling activity intent
            //call detail fragment
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, movieDetailFragment,"movieDetailFragment")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu,menu);
        /**
         * inflate only when in tablet mode
         */
        if(!isTabletMode) {
            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.menu_item_share);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //open settings activity
                Intent settingActivity=new Intent(this,SettingsActivity.class);
                startActivity(settingActivity);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
