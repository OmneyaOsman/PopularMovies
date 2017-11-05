package com.omni.moviewdb.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.omni.moviewdb.data.MovieContract.MovieEntry.TABLE_NAME;


public class MovieProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper ;


    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private  static final UriMatcher sUiMatcher = buildUriMatcher();


    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //directory
        uriMatcher.addURI(MovieContract.AUTHORITY , MovieContract.PATH_MOVIES ,MOVIES);

        //single item
        uriMatcher.addURI(MovieContract.AUTHORITY , MovieContract.PATH_MOVIES+"/#" ,MOVIE_WITH_ID);

        return  uriMatcher;

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDbHelper(context);

    return  true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUiMatcher.match(uri);


        Cursor readCursor;
        switch (match) {
            case MOVIES:

                readCursor = db.query(TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movie_id=?";
                String[] mSelectionArgs = new String[]{id};

                readCursor = db.query(TABLE_NAME, projection,
                        mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }


        readCursor.setNotificationUri(getContext().getContentResolver() ,uri);

        return readCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUiMatcher.match(uri);

        Uri returnedUri ;

        switch (match){
            case MOVIES:
              long id =  db.insert(TABLE_NAME ,null , contentValues);
                Log.d(TAG, "insert: "+id);
                if ( id > 0 ) {

                    returnedUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);

                } else {

                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }


        getContext().getContentResolver().notifyChange(uri, null);



        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {


        // COMPLETED (1) Get access to the database and write URI matching code to recognize a single item

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();


        int match = sUiMatcher.match(uri);
        // Keep track of the number of deleted tasks

        int moviesDeleted; // starts as 0


        // COMPLETED (2) Write the code to delete a single row of data

        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                moviesDeleted = db.delete(TABLE_NAME, "movie_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // COMPLETED (3) Notify the resolver of a change and return the number of items deleted
        if (moviesDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return moviesDeleted;
    }




    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
