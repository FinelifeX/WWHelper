package ru.kpfu.itis.wwhelper.util.weather

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.kpfu.itis.wwhelper.model.weather.Weather


/*
*** Created by Bulat Murtazin on 03.08.2018 ***
*/

class WeatherApi {

    companion object {
        const val KEY : String = "188b5c5acc1f7f558254a9abbcc3c9e0"
        const val BASE_URL : String = "http://api.openweathermap.org/data/2.5/"
        private var retrofit : Retrofit? = null

        fun getClient() : Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }
    }

    interface ApiInterface {
        @GET("weather")
        fun getWeather(
                @Query("lat") lat : Double,
                @Query("lon") lon : Double,
                @Query("units") units : String,
                @Query("appid") appid : String) : Call<Weather>
    }
}