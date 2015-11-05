package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MovieListFragment extends Fragment
{
    public final String LOG_TAG = MovieListFragment.class.getSimpleName();
    public final String MOVIE_LIST = "movie_list";
    public final String SORT_ORDER = "sort_order";
    private MoviePosterAdapter mMoviesAdapter;
    private ArrayList<Movie> mMovieList;
    private String mSortOrder = "";


    public MovieListFragment()
    {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // restore state if available

        if ( savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST))
        {   mMovieList = new ArrayList<Movie>();
            // get default SortOrder from Preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mSortOrder = prefs.getString(getString(R.string.pref_sortby_key),
                    getString(R.string.pref_sortby_rating));

        }
        else
        {   mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mSortOrder = savedInstanceState.getString(SORT_ORDER);
        }


        // allow fragment to handle menu events
        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        outState.putString(SORT_ORDER, mSortOrder);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        updateMovies();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_images, container, false);

        // initialize movie adapter with saved movie list (or empty if first time in).
        mMoviesAdapter = new MoviePosterAdapter(getActivity(),
                R.layout.fragment_movie_images, mMovieList);

        GridView gv = (GridView)rootView.findViewById(R.id.movieGridView);
        gv.setAdapter(mMoviesAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Movie movie = mMoviesAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("movie", movie);
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.menu_movielist_fragment, menu);
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

        // only update from the API if the movie list is empty, or the sort order has changed
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sortby_key),
                getString(R.string.pref_sortby_rating));
        if ( !sortOrder.equals(mSortOrder) || mMovieList.isEmpty())
        {   mSortOrder = sortOrder;
            FetchMovieTask getMovies = new FetchMovieTask();
            getMovies.execute(sortOrder);
        }
    }

    // Called when the AsyncTask completes - copy the new list of movies into
    // the adapter
    private void updateMovieView(List<Movie> movieList)
    {
        // need to reset the GridView because it's lost when use Back to return from Settings
        GridView gv = (GridView)getActivity().findViewById(R.id.movieGridView);
        gv.setAdapter(mMoviesAdapter);
        mMoviesAdapter.clear();
        for( Movie movie : movieList )
        {
            mMoviesAdapter.add(movie);
        }
        mMoviesAdapter.notifyDataSetChanged();
    }


    // AsyncTask class to fetch movie data from themoviedb.org website

    private class FetchMovieTask extends AsyncTask<String, Void, List<Movie>>
    {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private final String MOVIE_API_KEY = "PUT KEY HERE";

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
                final String RELEASE_DATE = "release_date.lte";
                final String VOTE_COUNT = "vote_count.gte";

                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                String today = dt.format(new Date());
                String sortBy = params[0];
                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        //.appendQueryParameter(RELEASE_DATE, today)
                        .appendQueryParameter(VOTE_COUNT, "100")
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

                while ((line = reader.readLine()) != null )
                {   // append a new line for debugging purposes
                    buffer.append(line + "\n");
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

