package com.example.ahmed.movieapp2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.movieapp2.Data.Movies.MoviesContract;
import com.example.ahmed.movieapp2.Data.Movies.TableData;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private static final String TAG = "ahmed";
    TextView title, rating, date, details;
    ImageView image;
    Button favButton;
    Uri imageUri;
    Integer id;
    String titleText, dateText, detialsText, imageText, ratingText;


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
        Picasso.with(this).load(imageUri).resize(400,500).into(image);

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
                } else {
                    Toast.makeText(MovieDetails.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
