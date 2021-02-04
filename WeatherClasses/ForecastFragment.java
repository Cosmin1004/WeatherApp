package com.example.travelweatherapp.WeatherClasses;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.travelweatherapp.Adapter.WeatherForecastAdapter;
import com.example.travelweatherapp.Common.Common;
import com.example.travelweatherapp.ModelWeather.WeatherForecastResult;
import com.example.travelweatherapp.R;
import com.example.travelweatherapp.Remote.IOpenWeatherMap;
import com.example.travelweatherapp.Remote.RetrofitClientWeather;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;
    static ForecastFragment instance;

    TextView txt_cityName;
    TextView  txt_geoCoord;
    RecyclerView recyclerView;

    public static ForecastFragment getInstance()
    {
        if(instance == null){
            instance = new ForecastFragment();
        }
        return instance;
    }


    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClientWeather.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_cityName = (TextView)itemView.findViewById(R.id.txt_cityName);
        txt_geoCoord = (TextView)itemView.findViewById(R.id.txt_geo_coord);

        recyclerView = (RecyclerView)itemView.findViewById(R.id.recycler_forecast);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        getForecastWeatherInfo();

        return itemView;
    }


    private void getForecastWeatherInfo() {
        compositeDisposable.add(mService.getForecastWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR",""+throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult){
        txt_cityName.setText(new StringBuilder(weatherForecastResult.city.name));
        txt_geoCoord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(),weatherForecastResult);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
