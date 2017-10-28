package com.omni.moviewdb.model;

import org.parceler.Parcel;

import io.realm.RealmMovieRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



@Parcel(implementations = { RealmMovieRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { RealmMovie.class })

public class RealmMovie extends RealmObject {


    @PrimaryKey
    private int mId ;
    private String mImageResource;
    private String mOverView;
    private String mReleaseDate;
    private String mOriginalTitle;
    private double mVoteAverage;
    private int favorite;

    private String backdropPath;


    public RealmMovie(){}


    public RealmMovie(String mImageResource, String mOriginalTitle, String mOverView, String mReleaseDate, double mVoteAverage, int mId) {
        this.mImageResource = mImageResource;
        this.mOverView = mOverView;
        this.mReleaseDate = mReleaseDate;
        this.mOriginalTitle = mOriginalTitle;
        this.mVoteAverage = mVoteAverage;
        this.mId = mId ;

    }



    public String getmImageResource() {
        return mImageResource;
    }

    public void setmImageResource(String mImageResource) {
        this.mImageResource = mImageResource;
    }

    public String getmOverView() {
        return mOverView;
    }

    public void setmOverView(String mOverView) {
        this.mOverView = mOverView;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
