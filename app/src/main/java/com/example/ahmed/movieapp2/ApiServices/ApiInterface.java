package com.example.ahmed.movieapp2.ApiServices;

import com.example.ahmed.movieapp2.Data.Movies.Movies;
import com.example.ahmed.movieapp2.Data.Movies.Reviews.Reviews;
import com.example.ahmed.movieapp2.Data.Movies.Trailers.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ahmed on 2/28/2018.
 */

public interface ApiInterface {

    @GET("{sort}")
    Call<Movies> getMovies(@Path("sort") String sortBy,
                           @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<Trailers> getTrailers(@Path("id") String id,
                               @Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Call<Reviews> getReviews(@Path("id") String id,
                             @Query("api_key") String apiKey);
}
