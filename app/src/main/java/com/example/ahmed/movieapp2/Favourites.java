package com.example.ahmed.movieapp2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.ahmed.movieapp2.Data.Movies.MoviesContract;
import com.example.ahmed.movieapp2.Data.Movies.TableData;

import java.util.Arrays;

import static com.example.ahmed.movieapp2.MainActivity.calculateNoOfColumns;

public class Favourites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapter.ItemClickListener{

    public static final int LOADER_ID = 0;
    private static final String TAG = "Favourites";
    RecyclerView recyclerView;
    DataAdapter dataAdapter;
    String[] titles, dates, details;
    Uri[] images;
    float[] ratings;
    Integer[] id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle("Favourites");

        recyclerView = findViewById(R.id.rvFav);
        int numberOfColumns = calculateNoOfColumns(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        dataAdapter = new DataAdapter();
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setClickListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor cursor = null;

            @Override
            protected void onStartLoading() {
                if (cursor != null) {
                    deliverResult(cursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    Log.i(TAG, "loadInBackground: ");
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "loadInBackground: ", e);
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                cursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int size = data.getCount();

        id = new Integer[size];
        titles = new String[size];
        ratings = new float[size];
        images = new Uri[size];
        dates = new String[size];
        details = new String[size];
        int i = 0;
        Log.i(TAG, "onLoadFinished: ");

        if (data.moveToFirst()) {
            Log.i(TAG, "onLoadFinished: start Cursor");
            do {
                id[i] = Integer.valueOf(data.getString(data.getColumnIndex(TableData.COLUMN_MOVIE_ID)));
                titles[i] = data.getString(data.getColumnIndex(TableData.COLUMN_TITLE));
                ratings[i] = Float.parseFloat(data.getString(data.getColumnIndex(TableData.COLUMN_RATING)));
                images[i] = Uri.parse(data.getString(data.getColumnIndex(TableData.COLUMN_POSTER)));
                dates[i] = data.getString(data.getColumnIndex(TableData.COLUMN_DATE));
                Log.i(TAG, "onLoadFinished: id = " + id[i]);
                details[i++] = data.getString(data.getColumnIndex(TableData.COLUMN_OVERVIEW));

            } while (data.moveToNext());

            dataAdapter.setData(images, titles, ratings);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dataAdapter.setData(null, null, null);
        Log.i(TAG, "onLoaderReset: ");
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent;
        intent = new Intent(Favourites.this, MovieDetails.class);
        intent.putExtra("title", titles[position]);
        intent.putExtra("image", images[position].toString());
        intent.putExtra("date", dates[position]);
        intent.putExtra("rating", String.valueOf(ratings[position]));
        intent.putExtra("details", details[position]);
        intent.putExtra("id", id[position]);

        startActivity(intent);
    }
}
