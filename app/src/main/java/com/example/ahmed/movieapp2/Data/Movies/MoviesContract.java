package com.example.ahmed.movieapp2.Data.Movies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import static com.example.ahmed.movieapp2.MainActivity.TAG;

/**
 * Created by Ahmed on 3/1/2018.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.ahmed.movieapp2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TableData.TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TableData.TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TableData.TABLE_NAME;

        public static Uri buildFlavorsUri(long id) {
            Log.i(TAG, "buildFlavorsUri: " + ContentUris.withAppendedId(CONTENT_URI, id));
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
