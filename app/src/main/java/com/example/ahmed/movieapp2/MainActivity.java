package com.example.ahmed.movieapp2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements DataAdapter.ItemClickListener{

    RecyclerView recyclerView;
    DataAdapter dataAdapter;
    String[] titles, dates, details;
    ImageView imageViews;
    Uri[] images;
    int imagesLength;
    double[] ratings;
    public static final String URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=d04160312987af22a80ba27b59cd080c";
    public static final String TAG = "jsonData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvNumbers);
        int numberOfColumns = calculateNoOfColumns(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        dataAdapter = new DataAdapter();
//        recyclerView.setAdapter(dataAdapter);
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setClickListener(this);

//        imageViews = findViewById(R.id.movie_image);
//        recyclerView.setAdapter(dataAdapter);

        /*if favourites button is clicked display favourites movies from SQLite else display all movies*/
        if (getIntent().getIntExtra("favourites", 0) == 0) {
            new AsyncMethod().execute();
        } else {
            Data db= new Data(this);
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(Data.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.getCount() == 0) {
                new TextView(this).setText("No favourite movies found");
                return;
            }

            titles = new String[cursor.getCount()];
            ratings = new double[cursor.getCount()];
            images = new Uri[cursor.getCount()];
            dates = new String[cursor.getCount()];
            details = new String[cursor.getCount()];

            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: ratings = " + cursor.getColumnName(cursor.getColumnIndex(Data.COLUMN_RATING)));
                    Log.i(TAG, "onCreate: title = " + cursor.getColumnName(cursor.getColumnIndex(Data.COLUMN_TITLE)));

                    titles[i] = cursor.getString(cursor.getColumnIndex(Data.COLUMN_TITLE));
                    ratings[i] = cursor.getDouble(cursor.getColumnIndex(Data.COLUMN_RATING));
                    images[i] = Uri.parse(cursor.getString(cursor.getColumnIndex(Data.COLUMN_POSTER)));
                    dates[i] = cursor.getString(cursor.getColumnIndex(Data.COLUMN_DATE));
                    details[i++] = cursor.getString(cursor.getColumnIndex(Data.COLUMN_OVERVIEW));

                } while (cursor.moveToNext());
            }

            dataAdapter.setData(images, titles, ratings);
        }
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @Override
    public void onItemClick(View view, int position) {
        // TODO: 2/4/2018 opens the details activity
//        Log.i("TAG", "onItemClick: item Clicked");
        Intent intent;
        intent = new Intent(MainActivity.this, MovieDetails.class);
        intent.putExtra("title", titles[position]);
        intent.putExtra("image", images[position].toString());
        intent.putExtra("date", dates[position]);
        intent.putExtra("rating", String.valueOf(ratings[position]));
        intent.putExtra("details", details[position]);

        startActivity(intent);
    }

//    public void createJson(final Context context) {
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    Log.i(TAG, "getResultsData: jsonObject = " + jsonObject);
//                    JSONArray results = jsonObject.getJSONArray("results");

//
//                    titles = new String[results.length()];
//                    ratings = new double[results.length()];
//                    images = new String[results.length()];
//
//                    for(int i=0; i<results.length(); i++) {
//                        JSONObject object = results.getJSONObject(i);
//                        String title = object.getString("title");
//                        double voteAverage = object.getDouble("vote_average");
//                        String imgUrl = object.getString("poster_path");
//
//                        titles[i] = title;
//                        ratings[i] = voteAverage;
//
//                        images[i] = "http://image.tmdb.org/t/p/w500" +imgUrl.substring(imgUrl.indexOf("/"));
////                        Log.i(TAG, "getResultsData: imgUrls[i] = " + images[i]);
////            Picasso.with(context).load(images[i]).into((ImageView)R.id.movie_image);
//                    }
//
////                    Log.i(TAG, "createJson: images = " + Arrays.toString(images));
////                    Log.i(TAG, "onResponse: titles = " + Arrays.toString(titles));
////                    Log.i(TAG, "onResponse: ratings = " + Arrays.toString(ratings));
//
//                    DataAdapter dataAdapter = new DataAdapter();
//
//                    dataAdapter.setData(images, titles, ratings);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "Error retrieving JSON data", Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue.add(stringRequest);
//
//
//    }

    class AsyncMethod extends AsyncTask<Void, Void, Uri[]> {
        @Override
        protected Uri[] doInBackground(Void... voids) {

            String response = "";
            HttpURLConnection urlConnection = null;
            try {
                String line = "";
                java.net.URL url = new URL(URL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream input = url.openStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                while ((line = reader.readLine()) != null) {
                    response = response + line;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: error = " + e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: error = " + e);
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
//                Log.i(TAG, "getResultsData: jsonObject = " + jsonObject);
                JSONArray results = jsonObject.getJSONArray("results");

                titles = new String[results.length()];
                ratings = new double[results.length()];
                images = new Uri[results.length()];
                dates = new String[results.length()];
                details = new String[results.length()];
//                imageViews = new ImageView[results.length()];
                imagesLength = results.length();

                for(int i=0; i<results.length(); i++) {
//                    imageViews[i] = findViewById(R.id.movie_image);
                    JSONObject object = results.getJSONObject(i);
                    String title = object.getString("title");

                    double voteAverage = object.getDouble("vote_average");
                    String imgUrl = object.getString("poster_path");
                    String date = object.getString("release_date");
                    String detail = object.getString("overview");

                    titles[i] = title;
                    ratings[i] = voteAverage;
                    dates[i] = date;
                    details[i] = detail;

                    images[i] = Uri.parse("http://image.tmdb.org/t/p/w185" +imgUrl.substring(imgUrl.indexOf("/")));
//                    Log.i(TAG, "doInBackground: title = " + titles[i]);
//                    Log.i(TAG, "doInBackground: rating = " + ratings[i]);
//                    Log.i(TAG, "getResultsData: imgUrls[i] = " + images[i]);

                }

//                    Log.i(TAG, "createJson: images = " + Arrays.toString(images));
//                    Log.i(TAG, "onResponse: titles = " + Arrays.toString(titles));
//                    Log.i(TAG, "onResponse: ratings = " + Arrays.toString(ratings));

//                DataAdapter dataAdapter = new DataAdapter();


//                Log.i(TAG, "doInBackground: data set");


            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: error = " + e);
            }
            return images;
        }

        @Override
        protected void onPostExecute(Uri[] uris) {
            dataAdapter.setData(images, titles, ratings);
//            for(int i=0; i<uris.length; i++) {
//                imageViews = (ImageView) findViewById(R.id.movie_image);
//                Log.i(TAG, "onPostExecute: image = " + images[i]);
//                Log.i(TAG, "onPostExecute: view = " + imageViews);
//                Picasso.with(MainActivity.this).load(images[i]).into(imageViews);
//            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.favourites:
                Intent intent;
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("favourites", 1);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
