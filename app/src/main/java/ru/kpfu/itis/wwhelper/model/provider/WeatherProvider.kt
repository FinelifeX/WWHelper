package ru.kpfu.itis.wwhelper.model.provider

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.kpfu.itis.wwhelper.model.weather.Weather
import ru.kpfu.itis.wwhelper.util.currentLatitude
import ru.kpfu.itis.wwhelper.util.currentLongitude
import ru.kpfu.itis.wwhelper.util.weather.WeatherApi
import ru.kpfu.itis.wwhelper.util.weatherResponseBody

object WeatherProvider {

    private val api = WeatherApi.getClient().create(WeatherApi.ApiInterface::class.java)

    fun getWeather() {

        val units = "metric"
        val key = WeatherApi.KEY

        val callWeather : Call<Weather> = api.getWeather(currentLatitude, currentLongitude, units, key)
        callWeather.enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>?, t: Throwable?) {
                Log.e("getWeather()", "onFailure")
                Log.e("getWeather()", t.toString())
            }

            override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {
                if (response!!.isSuccessful) {
                    weatherResponseBody = response.body()
                    Log.e("getWeather", "onResponse")
                    Log.e("getWeather", "Response successful")
                    Log.e("getWeather()", "Longitude = $currentLongitude")
                    Log.e("getWeather()", "Latitude = $currentLatitude")
                    Log.e("temp", weatherResponseBody?.tempWithDegree)
                    Log.e("rain", weatherResponseBody?.rainText)
                    Log.e("wind", weatherResponseBody?.windText)
                    Log.e("snow", weatherResponseBody?.snowText)
                }
            }
        })

        while (weatherResponseBody == null) {
            Log.e("getWeather", "Data hasn't been delivered yet")
        }
    }
}