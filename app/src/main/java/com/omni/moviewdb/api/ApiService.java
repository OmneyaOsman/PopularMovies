package com.omni.moviewdb.api;

import com.omni.moviewdb.model.movieResponse.MovieResponse;
import com.omni.moviewdb.model.reviewsResponse.ReviewResponse;
import com.omni.moviewdb.model.trailerResponse.TrailerResponse;
import com.omni.moviewdb.utils.AppConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface ApiService {

//    @FormUrlEncoded
    @GET(AppConfig.POPULAR_ENDPOINT)
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET(AppConfig.TOP_RATED_ENDPOINT)
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);


    @GET("3/movie/{id}/videos")
    Call<TrailerResponse> getVideos(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewResponse> getReviews(@Path("id") String movieId , @Query("api_key") String apiKey);
}
