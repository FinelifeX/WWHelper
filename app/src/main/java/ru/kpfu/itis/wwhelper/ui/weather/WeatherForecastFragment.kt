package ru.kpfu.itis.wwhelper.ui.weather

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.android.synthetic.main.fragment_weather.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.weather.Weather
import ru.kpfu.itis.wwhelper.util.REQUEST_CHECK_SETTINGS
import ru.kpfu.itis.wwhelper.util.REQUEST_PERMISSION_COARSE_LOCATION
import ru.kpfu.itis.wwhelper.util.weather.WeatherApi
import ru.kpfu.itis.wwhelper.util.weatherResponseBody


/*
*** Created by Bulat Murtazin on 02.08.2018 ***
*/

class WeatherForecastFragment : Fragment() {

    companion object {
        fun newInstance() : WeatherForecastFragment {
            return WeatherForecastFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setWeatherInfo(view)
    }

    private fun setWeatherInfo(view: View) {
        while (weatherResponseBody == null) {
            Log.e("getWeather", "Data hasn't been delivered yet")
        }
        val data = weatherResponseBody
        Glide.with(view.context).load(data?.iconUrl).into(view.icon)
        view.temperature.text = data?.tempWithDegree
        view.description.text = data?.summary
        if (!data?.rainText.equals("null")) {
            view.rain_volume.text = data?.rainText
        }
        view.wind_speed.text = data?.windText
        if (!data?.snowText.equals("null")) {
            view.snow_volume.text = data?.snowText
        }

    }

}