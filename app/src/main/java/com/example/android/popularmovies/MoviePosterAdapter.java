package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;

import java.util.List;

/**
 * Created by cindy on 10/10/2015.
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie>
{
    private Context context;
    private List<Movie> movieList;

    public MoviePosterAdapter(Context context, int resource, List<Movie> movies)
    {
        super(context, resource, movies);
        this.context = context;
        this.movieList = movies;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        Movie movie;

        if ( convertView == null )
        {   // get new layout
            itemView = inflater.inflate(R.layout.grid_item_movie, null);
        }
        else
        {   // reuse an existing view
            itemView = (View) convertView;
        }

        // find ImageView
        ImageView imageView = (ImageView)itemView.findViewById(R.id.movieImage);

        // get the current Movie object from the adapter
        movie = movieList.get(position);

        // get the full URL of the poster image
        if ( !movie.getImagePath().equals("null"))
        {   String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.getImagePath();
            Picasso.with(context).load(imageUrl).into(imageView);
        }

        return itemView;
    }

    @Override
    public int getCount()
    {
        return movieList.size();
    }

    @Override
    public Movie getItem(int position)
    {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }




}
