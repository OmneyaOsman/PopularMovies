package com.omni.moviewdb.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Omni on 31/10/2017.
 */

public class MovieContract {


    public static final String AUTHORITY = "com.omni.moviewdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);


    public static final String  PATH_MOVIES= "movies";




    public static final class MovieEntry implements BaseColumns {


        public static final Uri CONTENT_URI= BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String MOVIE_ID = "movie_id";

        public static final String VOTE_AVERAGE = "voteAverage";

        public static final String POSTER_PATH = "posterPath";
        public static final String TITLE = "originalTitle";
        public static final String COVER = "backdropPath";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE = "release_date";
        public static final String FAVORITE = "favorite";

    }

}
