package com.omni.moviewdb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Omni on 31/10/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {



    // The database name
    private static final String DATABASE_NAME = "movielist.db";
    private static final int DATABASE_VERSION = 1;



    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +

                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_ID + "  INTEGER NOT NULL," +
                MovieContract.MovieEntry.COVER + "  TEXT NOT NULL," +
                MovieContract.MovieEntry.VOTE_AVERAGE + "  DOUBLE NOT NULL," +
                MovieContract.MovieEntry.OVERVIEW + "  TEXT NOT NULL," +
                MovieContract.MovieEntry.FAVORITE + "  INTEGER NOT NULL," +
                MovieContract.MovieEntry.RELEASE + "  TEXT NOT NULL " +
                "); ";

        // COMPLETED (7) Execute the query by calling execSQL on sqLiteDatabase and pass the string query SQL_CREATE_WAITLIST_TABLE
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    // COMPLETED (8) Override the onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);



    }


}
