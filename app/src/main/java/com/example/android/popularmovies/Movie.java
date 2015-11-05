package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

/**
 * Created by cindy on 10/9/2015.
 */
public class Movie implements Parcelable
{
    private String mTitle;
    private Date mReleaseDate;
    private double mVoteRating;
    private double mPopularity;
    private int mVoteCount;
    private String mImagePath;
    private String mSynopsis;


    public Movie()
    {

    }

    public Movie(Parcel in)
    {
        mTitle = in.readString();
        mReleaseDate = Date.valueOf(in.readString());
        mVoteRating = in.readDouble();
        mPopularity = in.readDouble();
        mVoteCount = in.readInt();
        mImagePath = in.readString();
        mSynopsis = in.readString();

    }


    // Getters and Setters
    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String mTitle)
    {
        if ( mTitle.equals("null"))
            this.mTitle = "";
        else this.mTitle = mTitle;
    }

    public Date getReleaseDate()
    {
        return mReleaseDate;
    }

    public void setReleaseDate(Date mReleaseDate)
    {
        this.mReleaseDate = mReleaseDate;
    }

    public double getVoteRating()
    {
        return mVoteRating;
    }

    public void setVoteRating(double mVoteRating)
    {
        this.mVoteRating = mVoteRating;
    }

    public double getPopularity()
    {
        return mPopularity;
    }

    public void setPopularity(double mPopularity)
    {
        this.mPopularity = mPopularity;
    }

    public int getmVoteCount()
    {
        return mVoteCount;
    }

    public void setmVoteCount(int mVoteCount)
    {
        this.mVoteCount = mVoteCount;
    }

    public String getImagePath()
    {
        return mImagePath;
    }

    public void setImagePath(String mImagePath)
    {
        this.mImagePath = mImagePath;
    }

    public String getSynopsis()
    {
        return mSynopsis;
    }

    public void setSynopsis(String mSynopsis)
    {
        this.mSynopsis = mSynopsis;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate.toString());
        dest.writeDouble(mVoteRating);
        dest.writeDouble(mPopularity);
        dest.writeInt(mVoteCount);
        dest.writeString(mImagePath);
        dest.writeString(mSynopsis);

    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>()
    {
        @Override
        public Movie createFromParcel(Parcel source)
        {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };
}
