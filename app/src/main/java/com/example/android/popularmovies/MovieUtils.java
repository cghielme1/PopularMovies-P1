package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by cindy on 10/9/2015.
 */
public class MovieUtils
{
    private final static String LOG_TAG = MovieUtils.class.getSimpleName();

    public static List<Movie> getMoviesFromJson(String jsonStr)
    {
        List<Movie> movieList = new ArrayList<Movie>();


        try
        {
            JSONObject moviesJson = new JSONObject(jsonStr);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            for(int i=0; i < movieArray.length(); i++)
            {
                JSONObject movieData = movieArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setTitle(movieData.getString("original_title"));
                movie.setPopularity(movieData.getDouble("popularity"));
                movie.setVoteRating(movieData.getDouble("vote_average"));
                movie.setReleaseDate(Date.valueOf(movieData.getString("release_date")));
                movie.setImagePath(movieData.getString("poster_path"));
                movie.setSynopsis(movieData.getString("overview"));

                movieList.add(movie);

            }
        }
        catch(JSONException e)
        {
            Log.e(LOG_TAG, "Error:", e);
        }

        return movieList;


    }



}
