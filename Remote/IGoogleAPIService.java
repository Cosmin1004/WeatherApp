package com.example.travelweatherapp.Remote;

import com.example.travelweatherapp.ModelPlaces.MyPlaces;
import com.example.travelweatherapp.ModelPlaces.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}
