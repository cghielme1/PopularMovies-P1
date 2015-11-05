package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment
{
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private Movie movie;

    public MovieDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();

        movie = (Movie)getActivity().getIntent().getParcelableExtra("movie");
        ((TextView)rootView.findViewById(R.id.movieTitleTextView)).setText(movie.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String movieDate = sdf.format(movie.getReleaseDate());
        ((TextView)rootView.findViewById(R.id.movieReleaseDate)).setText(movieDate);
        ((TextView)rootView.findViewById(R.id.movieAvgRating)).append(Double.toString(movie.getVoteRating()));
        ((TextView)rootView.findViewById(R.id.movieVoteCount)).append(Integer.toString(movie.getmVoteCount()));
        ((TextView)rootView.findViewById(R.id.movieSynopsisTextView)).setText(movie.getSynopsis());


        // get the full URL of the poster image
        ImageView imageView = (ImageView)rootView.findViewById(R.id.moviePosterImageView);
        MovieUtils.GetMoviePosterImage(getActivity(), imageView, movie.getImagePath());

        return rootView;
    }
}
