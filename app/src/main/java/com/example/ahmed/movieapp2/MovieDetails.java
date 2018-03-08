package com.example.ahmed.movieapp2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.movieapp2.ApiServices.ApiClient;
import com.example.ahmed.movieapp2.ApiServices.ApiInterface;
import com.example.ahmed.movieapp2.Data.Movies.MoviesContract;
import com.example.ahmed.movieapp2.Data.Movies.Reviews.Reviews;
import com.example.ahmed.movieapp2.Data.Movies.TableData;
import com.example.ahmed.movieapp2.Data.Movies.Trailers.Result;
import com.example.ahmed.movieapp2.Data.Movies.Trailers.Trailers;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetails extends AppCompatActivity implements Adapter.ItemClickListener {

    private static final String TAG = "ahmed";
    TextView title, rating, date, details, trailerName, reviewContent, reviewsText;
    ImageView image;
    Button favButton;
    Uri imageUri;
    Integer id;
    String titleText, dateText, detialsText, imageText, ratingText;
    RecyclerView trailerRecyclerView, reviewsRecyclerView;
    String[] keys, names, authors, contents, reviews;
    Adapter adapter;
    ReviewAdapter reviewAdapter;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        title = findViewById(R.id.title_details);
        rating = findViewById(R.id.rating_details);
        date = findViewById(R.id.date_details);
        details = findViewById(R.id.details_text);
        image = findViewById(R.id.image_details);
        favButton = findViewById(R.id.fav_button);
        trailerRecyclerView = findViewById(R.id.recycler_view);
        trailerName = findViewById(R.id.trailer_name);
        reviewContent = findViewById(R.id.user_review);
        reviewsRecyclerView = findViewById(R.id.reviews_rv);

        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerRecyclerView.setHasFixedSize(true);
        adapter = new Adapter();
        trailerRecyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(reviewAdapter);

        id = getIntent().getExtras().getInt("id");

        titleText = getIntent().getStringExtra("title");
        ratingText = getIntent().getStringExtra("rating");
        dateText = getIntent().getStringExtra("date");
        detialsText = getIntent().getStringExtra("details");
        imageText = getIntent().getStringExtra("image");

        title.setText(titleText);
        rating.setText(ratingText);
        date.setText(dateText);
        details.setText(detialsText);
        imageUri = Uri.parse(imageText);
        Picasso.with(this).load(imageUri).resize(400, 500).into(image);

        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        Log.i(TAG, "onCreate: id = " + cursor.getPosition());

        if (cursor.moveToFirst()) {
            final int contentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(TableData.COLUMN_ID)));
            Log.i(TAG, "onCreate: contentId = " + contentId);
            favButton.setText("Remove");
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContentResolver().delete(MoviesContract.MoviesEntry.buildFlavorsUri(contentId),
                            TableData.COLUMN_ID + "=?",
                            new String[]{String.valueOf(contentId)});
                    getContentResolver().notifyChange(MoviesContract.MoviesEntry.buildFlavorsUri(contentId), null);
                    favButton.setText("Favourites");
                    Toast.makeText(MovieDetails.this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MovieDetails.this, Favourites.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP & Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        } else {
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TableData.COLUMN_TITLE, titleText);
                    contentValues.put(TableData.COLUMN_RATING, ratingText);
                    contentValues.put(TableData.COLUMN_DATE, dateText);
                    contentValues.put(TableData.COLUMN_OVERVIEW, detialsText);
                    contentValues.put(TableData.COLUMN_POSTER, imageText);
                    contentValues.put(TableData.COLUMN_MOVIE_ID, id);

                    Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
                    if (uri != null) {
                        Log.i(TAG, "onClick: uri = " + uri);
                        Toast.makeText(MovieDetails.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                        favButton.setText("Remove");
                        getContentResolver().notifyChange(MoviesContract.MoviesEntry.CONTENT_URI, null);
                    } else {
                        Toast.makeText(MovieDetails.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Trailers> getMovieTrailers = apiInterface.getTrailers(String.valueOf(id), BuildConfig.MOVIES_API_KEY);
        getMovieTrailers.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                int size = response.body().getResults().size();

                List<Result> results = response.body().getResults();

                keys = new String[size];
                names = new String[size];
                Log.i(TAG, "onResponse: trailers size = " + size);

                for (int i = 0; i < size; i++) {
                    keys[i] = results.get(i).getKey();
                    names[i] = results.get(i).getName();
                }
                adapter.setData(names);

            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {

            }
        });

        Call<Reviews> getReviews = apiInterface.getReviews(String.valueOf(id), BuildConfig.MOVIES_API_KEY);
        getReviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                List<com.example.ahmed.movieapp2.Data.Movies.Reviews.Result> results = response.body().getResults();
                int size = results.size();

                authors = new String[size];
                contents = new String[size];

                Log.i(TAG, "onResponse: reviews size = " + size);
                for (int i = 0; i < size; i++) {
                    authors[i] = results.get(i).getAuthor() + ":";
                    contents[i] = results.get(i).getContent();
                }
                reviewAdapter.setData(authors, contents);

            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {

            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        Uri link = Uri.parse("http://www.youtube.com/watch?v=" + keys[position]);
        Log.i(TAG, "onItemClick: uri = " + link);
        startActivity(new Intent(Intent.ACTION_VIEW, link));
    }
}
