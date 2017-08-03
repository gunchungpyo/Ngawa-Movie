package com.viv.gunchung.ngawamovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.viv.gunchung.ngawamovie.data.MovieContract.MovieEntry;

/**
 * Created by gunawan on 05/08/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                        MovieEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +

                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL,"                     +
                        MovieEntry.COLUMN_TITLE          + " TEXT NOT NULL, "                       +
                        MovieEntry.COLUMN_VOTE_AVERAGE   + " REAL NOT NULL, "                       +
                        MovieEntry.COLUMN_POSTER_PATH    + " TEXT NOT NULL, "                       +
                        MovieEntry.COLUMN_BACKDROP_PATH  + " TEXT NOT NULL, "                       +
                        MovieEntry.COLUMN_OVERVIEW       + " TEXT NOT NULL, "                       +
                        MovieEntry.COLUMN_RELEASE_DATE   + " INTEGER NOT NULL, "                    +
                        MovieEntry.COLUMN_POPULARITY     + " REAL NOT NULL, "                       +
                        MovieEntry.COLUMN_TIMESTAMP      + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
