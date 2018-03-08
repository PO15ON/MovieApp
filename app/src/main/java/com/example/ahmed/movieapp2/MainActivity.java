package com.example.ahmed.movieapp2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ahmed.movieapp2.ApiServices.ApiClient;
import com.example.ahmed.movieapp2.ApiServices.ApiInterface;
import com.example.ahmed.movieapp2.Data.Movies.Movies;
import com.example.ahmed.movieapp2.Data.Movies.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements DataAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = "jsonData";
    public static final String SAVED_LAYOUT_MANAGER = "layoutManager";
    RecyclerView recyclerView;
    Spinner spinner;
    DataAdapter dataAdapter;
    String[] titles, dates, details;
    Uri[] images;
    float[] ratings;
    Integer[] id;
    GridLayoutManager gridLayoutManager;
    Parcelable savedRecyclerLayoutState = null;

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

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
        gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        dataAdapter = new DataAdapter();
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_LAYOUT_MANAGER, recyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    public void onItemClick(View view, int position) {
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

                        id[i] = movieId;
                        titles[i] = title;
                        ratings[i] = rating;
                        images[i] = Uri.parse("http://image.tmdb.org/t/p/w185/" + image);
                        dates[i] = date;
                        details[i] = movieDetails;
                    }

                    dataAdapter.setData(images, titles, ratings);

                    if (savedRecyclerLayoutState != null) {
                        recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                    }
                }

                @Override
                public void onFailure(Call<Movies> call, Throwable t) {

                }
            });

    }

    public void onNothingSelected(AdapterView<?> arg0) {
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
