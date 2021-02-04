package com.example.travelweatherapp.Common;

import android.location.Location;
import com.example.travelweatherapp.ModelPlaces.Results;
import com.example.travelweatherapp.Remote.IGoogleAPIService;
import com.example.travelweatherapp.Remote.RetrofitClientTravel;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Common {
    public static final String APP_ID = "bff09a5488593903167b38a1cc70409e";
    public static Location current_location = null;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static Results currentResult;


    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClientTravel.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }


    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
