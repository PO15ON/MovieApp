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

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private static final String TAG = "ahmed";
    TextView title, rating, date, details;
    ImageView image;
    Button favButton;
    Uri imageUri;

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

        title.setText(getIntent().getStringExtra("title"));
        rating.setText(getIntent().getStringExtra("rating"));
        date.setText(getIntent().getStringExtra("date"));
        details.setText(getIntent().getStringExtra("details"));
        imageUri = Uri.parse(getIntent().getStringExtra("image"));
        Picasso.with(this).load(imageUri).resize(400,500).into(image);


            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Data db = new Data(MovieDetails.this);
                    SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(Data.COLUMN_TITLE, title.getText().toString());
                    cv.put(Data.COLUMN_DATE, date.getText().toString());
                    cv.put(Data.COLUMN_OVERVIEW, details.getText().toString());
                    cv.put(Data.COLUMN_POSTER, imageUri.toString());
                    cv.put(Data.COLUMN_RATING, rating.getText().toString());
                    Log.i(TAG, "onClick: cv = " + cv.toString());

                    sqLiteDatabase.insert(Data.TABLE_NAME, null, cv);

                    Toast.makeText(MovieDetails.this, "Movie added to favourites", Toast.LENGTH_SHORT).show();
                }
            });



    }
}
