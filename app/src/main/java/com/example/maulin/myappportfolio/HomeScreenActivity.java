package com.example.maulin.myappportfolio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.maulin.popularmovies.MainActivity;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onClickButton1(View view) {
        //call my first achievement :-)
        Intent popularMovies =new Intent(this, MainActivity.class);
        startActivity(popularMovies);
    }

    public void onClickButton2(View view) {
        Toast.makeText(this,R.string.super_duo1,Toast.LENGTH_SHORT).show();
    }

    public void onClickButton3(View view) {
        Toast.makeText(this,R.string.super_duo2,Toast.LENGTH_SHORT).show();
    }

    public void onClickButton4(View view) {
        Toast.makeText(this,R.string.ant_terminator,Toast.LENGTH_SHORT).show();
    }

    public void onClickButton5(View view) {
        Toast.makeText(this,R.string.materialize,Toast.LENGTH_SHORT).show();
    }

    public void onClickButton6(View view) {
        Toast.makeText(this,R.string.capstone,Toast.LENGTH_SHORT).show();
    }
}
