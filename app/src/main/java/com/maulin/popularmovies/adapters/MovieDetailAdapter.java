package com.maulin.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.maulin.myappportfolio.R;
import com.maulin.popularmovies.model.MovieDetail;
import com.maulin.popularmovies.model.MovieReview;
import com.maulin.popularmovies.model.MovieTrailer;
import com.maulin.popularmovies.model.Movies;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maulin on 14/12/15.
 */
public class MovieDetailAdapter extends BaseAdapter implements View.OnClickListener {

    public final static int MOVIE_ITEM_TYPE=0;
    public final static int MOVIE_TRAILER=1;
    public final static int MOVIE_REVIEW=2;
    public static final String FAVOURITE = "Favourite";
    public static final String UNFAVOURITE = "UnFavourite";

    public final Context mContext;
    private final LayoutInflater inflater;
    private MovieDetail mMovieItemDetail=null;
    private Movies mMovieItem=null;
    private FavouriteListener mListener;

    public MovieDetailAdapter(Context context){
        mContext=context;
        inflater= LayoutInflater.from(context);
    }

    // set movie item and notify data set changed
    public void setMovieItem(Movies movieItem) {
        mMovieItem=movieItem;
        notifyDataSetChanged();
    }
    public void setMovieItemDetail(MovieDetail movieDetail) {
        mMovieItemDetail=movieDetail;
        notifyDataSetChanged();
    }

    public MovieDetail getmMovieItemDetail() {
        return mMovieItemDetail;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return MOVIE_ITEM_TYPE;
        else if(position>=1 &&
                position<=(mMovieItemDetail.getMovieTrailers().size()))
            return MOVIE_TRAILER;
        else
            return MOVIE_REVIEW;
    }

    @Override
    public int getCount() {
        //calculate count of value available in listview
        if(mMovieItemDetail!=null)
            return mMovieItemDetail.getMovieTrailers().size()
                    +mMovieItemDetail.getMovieReviews().size()+1;
        else
            return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    /**
     * return movie trailer detail
     * @param position postion
     * @return movie trailer detail
     */
    public MovieTrailer getItemMovieTrailer(int position) {
        if(mMovieItemDetail.getMovieTrailers()!=null &&
                !mMovieItemDetail.getMovieTrailers().isEmpty()) {
            return mMovieItemDetail.getMovieTrailers().get(position-1);
        } else {
            return null;
        }
    }

    /**
     * return movie review
     * @param position postion
     * @return movie review detail
     */
    public MovieReview getItemMovieReview(int position) {
        if(mMovieItemDetail.getMovieReviews()!=null &&
                !mMovieItemDetail.getMovieReviews().isEmpty()) {
            //get review
            int actual_position=position-(mMovieItemDetail.getMovieTrailers().size()+1);
            return mMovieItemDetail.getMovieReviews().get(actual_position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //movie item type
        if(getItemViewType(position)==MOVIE_ITEM_TYPE) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_movie_details, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Glide.with(mContext)
                    .load(mMovieItem.getPoster_path())
                    .crossFade()
                    .into(viewHolder.MoviePosterImageView);

            viewHolder.MoviePosterTitle.setText(mMovieItem.getTitle());
            viewHolder.MoviePlot.setText(mMovieItem.getOverview());
            viewHolder.MovieRating.setText(mMovieItem.getVote_average());
            viewHolder.MovieReleaseDate.setText(mMovieItem.getRelease_date());
            viewHolder.favorite_movie.setOnClickListener(this);

            /**
             * check whether movie detail are fetch from the net or not
             * if not then disable fav button otherwise enabled it
             */
            if(mMovieItemDetail!=null) {
                viewHolder.favorite_movie.setEnabled(true);
                if(mMovieItemDetail.isMovieFavorite()) {
                    viewHolder.favorite_movie.setText(String.format(
                            mContext.getResources().getString(R.string.favorite)
                            ,UNFAVOURITE));
                    viewHolder.favorite_movie.setTag(UNFAVOURITE);
                } else {
                    viewHolder.favorite_movie.setText(String.format(
                            mContext.getResources().getString(R.string.favorite),FAVOURITE));
                    viewHolder.favorite_movie.setTag(FAVOURITE);
                }
                viewHolder.favorite_movie.setVisibility(View.VISIBLE);
            } else {
                viewHolder.favorite_movie.setVisibility(View.INVISIBLE);
            }

            return convertView;
        } else if(getItemViewType(position)==MOVIE_TRAILER) {
            MovieTrailerViewHolder holder;
            if(convertView==null) {
                convertView=inflater.inflate(R.layout.item_movie_trailer,parent,false);
                holder=new MovieTrailerViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder= (MovieTrailerViewHolder) convertView.getTag();
            }

            //hide trailer header
            if(position!=1){
                holder.TrailerHeading.setVisibility(View.GONE);
            }

            holder.movieTrailerName.setText(getItemMovieTrailer(position).getName());

            //load youtube video image thumbnail
            Glide.with(mContext)
                    .load(getItemMovieTrailer(position).getTailerThumbURL())
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_dialog_alert)
                    .crossFade()
                    .into(holder.movie_trailer_thumbnail);

            return convertView;
        } else if(getItemViewType(position)==MOVIE_REVIEW) {
            MovieReviewsViewHolder holder;
            if(convertView==null) {
                convertView=inflater.inflate(R.layout.item_movie_review,parent,false);
                holder=new MovieReviewsViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder=(MovieReviewsViewHolder)convertView.getTag();
            }

            // hide review header
            if(position!=mMovieItemDetail.getMovieTrailers().size()+1) {
                holder.ReviewHeading.setVisibility(View.GONE);
            }

            holder.AuthorName.setText(getItemMovieReview(position).getAuthor());
            holder.Comment.setText(getItemMovieReview(position).getContent());
            return convertView;
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(v.getTag().toString().equalsIgnoreCase(UNFAVOURITE)) {
            mMovieItemDetail.setMovieFavorite(false);
        } else {
            mMovieItemDetail.setMovieFavorite(true);
        }
        notifyDataSetChanged();
        if(mListener!=null)
            mListener.FavouriteMovieSelected(v);
    }

    /**
     * set Favourite Movie Listener
     */
    public void setFavouriteListner(FavouriteListener listener) {
            mListener=listener;
    }

    public interface FavouriteListener {
        void FavouriteMovieSelected(View view);
    }

    //view holder for individual movie info
    static class ViewHolder {
        @Bind(R.id.iv_thumb_movie_poster)
        ImageView MoviePosterImageView;
        @Bind(R.id.movie_title)
        TextView MoviePosterTitle;
        @Bind(R.id.tv_movie_rating)
        TextView MovieRating;
        @Bind(R.id.tv_release_date)
        TextView MovieReleaseDate;
        @Bind(R.id.tv_movie_plot)
        TextView MoviePlot;
        @Bind(R.id.favorite_movie)
        Button favorite_movie;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }

    //view holder for individual movie trailer
    static class MovieTrailerViewHolder {
        @Bind(R.id.movie_trailer_name)
        TextView movieTrailerName;
        @Bind(R.id.movie_trailer_thumb_image_view)
        ImageView movie_trailer_thumbnail;
        @Bind(R.id.trailer_heading)
        TextView TrailerHeading;

        public MovieTrailerViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }

    //view holder for movie reviews
    static class MovieReviewsViewHolder {
        @Bind(R.id.review_heading)
        TextView ReviewHeading;
        @Bind(R.id.Author_Name)
        TextView AuthorName;
        @Bind(R.id.comment)
        TextView Comment;

        public MovieReviewsViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
