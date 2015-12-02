package com.maulin.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.fragment.MovieListFragment;
import com.maulin.popularmovies.model.Movies;

public class MainActivity extends AppCompatActivity implements
        MovieListFragment.OnMovieItemClickListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMovieItemClick(Movies movie) {
        //call movie detail activity which display movie detail
        Intent intent=new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_MOVIE,movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //open settings activity
                Intent settingActivity=new Intent(this,SettingsActivity.class);
                startActivity(settingActivity);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
