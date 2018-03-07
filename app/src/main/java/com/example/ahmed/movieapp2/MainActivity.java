package com.example.ahmed.movieapp2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.movieapp2.ApiServices.ApiClient;
import com.example.ahmed.movieapp2.ApiServices.ApiInterface;
import com.example.ahmed.movieapp2.Data.Movies.Movies;
import com.example.ahmed.movieapp2.Data.Movies.MoviesContract;
import com.example.ahmed.movieapp2.Data.Movies.Result;
import com.example.ahmed.movieapp2.Data.Movies.TableData;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements DataAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    Spinner spinner;
    DataAdapter dataAdapter;
    String[] titles, dates, details;
    Uri[] images;
    float[] ratings;
    Integer[] id;
    public static final String TAG = "jsonData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Movies");

        if (!isOnline()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        recyclerView = findViewById(R.id.rvNumbers);
        int numberOfColumns = calculateNoOfColumns(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        dataAdapter = new DataAdapter();
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
        intent.putExtra("id", id[position]);

        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String sortBy = adapterView.getItemAtPosition(i).toString();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Movies> getMovies = apiInterface.getMovies(sortBy, BuildConfig.MOVIES_API_KEY);
        getMovies.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
//                    Log.i(TAG, "onResponse: " + response);
//                    Log.i(TAG, "onResponse: results = " + response.body().getResults());
                    int size = response.body().getResults().size();
                    List<Result> results = response.body().getResults();

                    id = new Integer[size];
                    titles = new String[size];
                    ratings = new float[size];
                    images = new Uri[size];
                    dates = new String[size];
                    details = new String[size];

                    for(int i=0; i<size; i++) {
                        Integer movieId = results.get(i).getId();
                        String title = results.get(i).getTitle();
                        float rating = results.get(i).getVoteAverage();
                        Uri image = Uri.parse(results.get(i).getPosterPath());
                        String date = results.get(i).getReleaseDate();
                        String movieDetails = results.get(i).getOverview();

//                        Log.d(TAG, "onResponse: id = " + movieId + " title = " + title);

                        id[i] = movieId;
                        titles[i] = title;
                        ratings[i] = rating;
                        images[i] = Uri.parse("http://image.tmdb.org/t/p/w185/" + image);
                        dates[i] = date;
                        details[i] = movieDetails;
                    }

                    dataAdapter.setData(images, titles, ratings);
                }

                @Override
                public void onFailure(Call<Movies> call, Throwable t) {

                }
            });

        // On selecting a spinner item



    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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
                intent = new Intent(this, Favourites.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


}
