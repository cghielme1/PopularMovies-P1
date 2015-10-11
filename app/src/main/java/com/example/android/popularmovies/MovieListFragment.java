package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MovieListFragment extends Fragment
{
    public final String LOG_TAG = MovieListFragment.class.getSimpleName();
    MoviePosterAdapter mMoviesAdapter;

    public MovieListFragment()
    {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // allow fragment to handle menu events
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_images, container, false);

        mMoviesAdapter = new MoviePosterAdapter(getActivity(),
                R.layout.fragment_movie_images, new ArrayList<Movie>());

        GridView gv = (GridView)rootView.findViewById(R.id.movieGridView);
        gv.setAdapter(mMoviesAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_movielist_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // handle menu item clicks here
        int id = item.getItemId();
        if ( id == R.id.action_refresh )
        {
            updateMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    // start the background task to get the movie list
    private void updateMovies()
    {
        Log.v(LOG_TAG, "Entering updateMovies");
        FetchMovieTask getMovies = new FetchMovieTask();
        getMovies.execute("test");
    }

    // Called when the AsyncTask completes - copy the new list of movies into
    // the adapter
    private void updateMovieView(List<Movie> movieList)
    {
        mMoviesAdapter.clear();
        for( Movie movie : movieList )
        {
            mMoviesAdapter.add(movie);
        }
    }


    // AsyncTask class to fetch movie data from themoviedb.org website

    private class FetchMovieTask extends AsyncTask<String, Void, List<Movie>>
    {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private final String MOVIE_API_KEY = "6cf5416bc9de0a0395a0f667de338ced";

        @Override
        protected List<Movie> doInBackground(String... params)
        {
            // create the connection and reader vars
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // string to hold the raw JSON response
            String movieJsonStr = null;

            try
            {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String KEY_PARAM = "api_key";
                String sortBy = "popularity.desc";
                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(KEY_PARAM, MOVIE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI=" + builtUri.toString());

                // Create the request to themoviedb and open the connection
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if ( inputStream == null )
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                /*
                while ((line = reader.readLine()) != null )
                {   // append a new line for debugging purposes
                    buffer.append(line + "\n");
                }
                */
                boolean end = false;
                while(!end)
                {
                    line = reader.readLine();
                    if ( line != null )
                        buffer.append(line + "\n");
                    else end=true;
                }


                if ( buffer.length() == 0 )
                    movieJsonStr = null;

                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "movieJsonStr="+ movieJsonStr);


            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Error:", e);
                movieJsonStr = null;
            }
            finally
            {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    } catch (final IOException e)
                    {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // get the movie data from the JSON string
            List<Movie> movieList = MovieUtils.getMoviesFromJson(movieJsonStr);
            return movieList;


        }

        @Override
        protected void onPostExecute(List<Movie> movieList)
        {
            Log.v(LOG_TAG, "fetchMovies complete!");
            if ( movieList != null )
                updateMovieView(movieList);
        }
    }
}

