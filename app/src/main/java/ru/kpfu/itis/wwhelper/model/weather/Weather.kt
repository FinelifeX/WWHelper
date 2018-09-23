package ru.kpfu.itis.wwhelper.model.weather

import com.google.gson.annotations.SerializedName

import java.util.Calendar

class Weather {

    @SerializedName("main")
    private lateinit var weatherTemp: WeatherTemp

    @SerializedName("weather")
    private lateinit var description: List<WeatherDescription>

    @SerializedName("dt")
    private var timestamp: Long = 0

    @SerializedName("rain")
    private var rain: Rain? = null

    @SerializedName("snow")
    private var snow: Snow? = null

    @SerializedName("wind")
    private lateinit var wind: Wind

    val date: Calendar
        get() {
            val date = Calendar.getInstance()
            date.timeInMillis = timestamp * 1000
            return date
        }

    val temp: String
        get() = weatherTemp.temp.toString()

    val tempMin: String
        get() = weatherTemp.temp_min.toString()

    val tempMax: String
        get() = weatherTemp.temp_max.toString()

    val tempInteger: String
        get() = weatherTemp.temp!!.toInt().toString()

    val tempWithDegree: String
        get() = weatherTemp.temp!!.toInt().toString() + "\u00B0"

    val icon: String?
        get() = description[0].icon

    val summary: String?
        get() = description[0].description

    val iconUrl: String
        get() = "http://openweathermap.org/img/w/" + description[0].icon + ".png"

    val rainText: String?
        get() = rain?.value.toString()

    val snowText: String?
        get() = snow?.value.toString()

    val windText: String?
        get() = "${wind.speed} m/s"

    val windDouble: Double?
        get() = wind.speed

    inner class WeatherTemp {
        internal var temp: Double? = null
        internal var temp_min: Double? = null
        internal var temp_max: Double? = null
    }

    inner class Wind {
        internal var speed: Double = 0.0
    }

    inner class Rain {
        @SerializedName("3h")
        internal var value: Int = 0
    }

    inner class Snow {
        @SerializedName("3h")
     internal var value: Int = 0
    }

    inner class WeatherDescription {
        internal var icon: String? = null
        internal var description: String? = null
    }
}
