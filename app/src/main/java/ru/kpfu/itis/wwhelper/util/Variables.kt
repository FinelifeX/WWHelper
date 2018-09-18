package ru.kpfu.itis.wwhelper.util

import android.graphics.Bitmap
import ru.kpfu.itis.wwhelper.model.weather.Weather


/*
*** Created by Bulat Murtazin on 29.08.2018 ***
*/

    var lastTakenPhotoBitmap: Bitmap? = null
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var weatherResponseBody: Weather? = null