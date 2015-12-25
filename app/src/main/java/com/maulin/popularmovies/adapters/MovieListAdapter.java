package com.maulin.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.model.Movies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maulin on 23/11/15.
 */
public class MovieListAdapter extends ArrayAdapter<Movies> {

    private final LayoutInflater inflater;
    private final List<Movies> mArrayListMovies;

    //movies list adapter constructor
    public MovieListAdapter(Context context,List<Movies> objects) {
        super(context, 0, objects);
        mArrayListMovies=objects;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
       if(convertView==null) {
           holder=new ViewHolder();
           convertView=inflater.inflate(R.layout.movie_item, parent, false);
           holder.moviePosterView= (ImageView) convertView.findViewById(R.id.iv_movie_poster);
           convertView.setTag(holder);
       } else {
           holder= (ViewHolder) convertView.getTag();
       }

        //movie poster url
        String movie_poster_url=getItem(position).getPoster_path();

        //glide download image
        Glide.with(getContext())
                .load(movie_poster_url)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_dialog_alert)
                .crossFade()
                .into(holder.moviePosterView);

        return convertView;
    }

    /**
     * return collection of arrayList values
     * @return
     */
    public ArrayList<Movies> getValues(){
        return (ArrayList<Movies>) mArrayListMovies;
    }

    /**
     * ViewHolder static class
     */
    public static class ViewHolder {
        ImageView moviePosterView;
    }
}
