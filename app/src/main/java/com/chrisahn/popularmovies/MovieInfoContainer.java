package com.chrisahn.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 10/22/2015.
 */
public class MovieInfoContainer implements Parcelable {
    private String mPosterPath;
    private String mOriginalTitle;
    private String mReleaseDate;
    private int mVoteAverage;
    private String mOverview;
    private int mId;

    // Default Constructor
    public MovieInfoContainer() {
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public int getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        mVoteAverage = voteAverage;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    // This wraps our data in a certain order
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeString(mOriginalTitle);
        dest.writeString(mReleaseDate);
        dest.writeInt(mVoteAverage);
        dest.writeString(mOverview);
        dest.writeInt(mId);
    }

    // unwrap our data, must be unwrapped in the order it was wrapped in
    private MovieInfoContainer(Parcel in) {
        mPosterPath = in.readString();
        mOriginalTitle = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readInt();
        mOverview = in.readString();
        mId = in.readInt();
    }

    // This interface must be implemented. Generates instances of the Parcelable class (in this case
    // MovieInfoContainer) from a Parcel
    public static final Creator<MovieInfoContainer> CREATOR = new Creator<MovieInfoContainer>() {
        // Creates a new instance of the Parcelable class (MovieInfoContainer), instantiating it from
        // the given Parcel whose data had previously been written by Parcelable.writeToParcel()
        @Override
        public MovieInfoContainer createFromParcel(Parcel source) {
            return new MovieInfoContainer(source);
        }
        // Create a new array of the Parcelable class.
        @Override
        public MovieInfoContainer[] newArray(int size) {
            return new MovieInfoContainer[size];
        }
    };
}
