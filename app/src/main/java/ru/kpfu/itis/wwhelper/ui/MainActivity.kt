package ru.kpfu.itis.wwhelper.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.android.synthetic.main.activity_main.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.provider.WeatherProvider
import ru.kpfu.itis.wwhelper.ui.advice.AdviceFragment
import ru.kpfu.itis.wwhelper.ui.profile.ProfileFragment
import ru.kpfu.itis.wwhelper.ui.weather.WeatherForecastFragment
import ru.kpfu.itis.wwhelper.util.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }

    var selectedItemId : Int = 0
    var previousSelectedItemId: Int = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_weather -> {
                Log.d("BOTTOM_NAVIGATION", "Weather is chosen")
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, WeatherForecastFragment.newInstance())
                        .commit()
                selectedItemId = item.itemId
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_clothing -> {
                selectedItemId = item.itemId
                Log.d("BOTTOM_NAVIGATION", "Clothing is chosen")
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, AdviceFragment.newInstance())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ProfileFragment.newInstance())
                        .commit()
                selectedItemId = item.itemId
                Log.d("BOTTOM_NAVIGATION", "Profile is chosen")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_weather
        previousSelectedItemId = selectedItemId

        refresher.setOnRefreshListener(this)
        refresher.setColorSchemeColors(
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorPrimaryDark),
                resources.getColor(R.color.colorAccent))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.toolbar_settings -> {
            //TODO Add SettingsFragment and handle transition
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        refresher.isRefreshing = true
        when (selectedItemId) {
            R.id.navigation_weather -> {
                WeatherProvider.getWeather()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, WeatherForecastFragment.newInstance())
                        .commit()
            }
            R.id.navigation_profile ->
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ProfileFragment.newInstance())
                        .commit()
        }

        refresher.isRefreshing = false
    }

    override fun onBackPressed() {
        if (selectedItemId == R.id.navigation_clothing) {
            super.onBackPressed()
        }
        else {
            navigation.selectedItemId = R.id.navigation_clothing
        }
    }
}
