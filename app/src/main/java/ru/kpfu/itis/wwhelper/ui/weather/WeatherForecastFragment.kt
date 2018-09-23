package ru.kpfu.itis.wwhelper.ui.weather

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_weather.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.provider.WeatherProvider
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
        WeatherReceiver(view).execute();
    }

    class WeatherReceiver(var view: View) : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            WeatherProvider.getWeather()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            while (weatherResponseBody == null) {
            }
            return null;
        }

        override fun onPostExecute(result: Void?) {
            setWeatherInfo(view)
        }

        private fun setWeatherInfo(view: View) {
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

}