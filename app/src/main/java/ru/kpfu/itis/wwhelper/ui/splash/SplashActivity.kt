package ru.kpfu.itis.wwhelper.ui.splash

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.kpfu.itis.wwhelper.ui.MainActivity
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.model.provider.WeatherProvider
import ru.kpfu.itis.wwhelper.ui.login.LoginActivity
import ru.kpfu.itis.wwhelper.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val user = FirebaseAuth.getInstance().currentUser
        createLocationRequest()
        routeToAppropriatePage(user)
    }

    private fun routeToAppropriatePage(user: FirebaseUser?) {
        when (user) {
            null -> LoginActivity.start(this)
            else -> loadUser()
        }
    }

    private fun loadUser() {
        UserProvider.provideUser().addOnCompleteListener {
            MainActivity.start(this)
        }
    }

    private fun checkAccessLocationPermission() {
        if (ContextCompat.checkSelfPermission(baseContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

            }
            else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_COARSE_LOCATION)
            }
        }
        else {
            //permission granted already
        }
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(baseContext)
        val task = client.checkLocationSettings(builder.build())
        checkAccessLocationPermission()

        task.addOnSuccessListener {

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(baseContext)
            mFusedLocationProviderClient.lastLocation
                    .addOnSuccessListener {
                        location: Location? ->
                        if (location != null) {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude
                        }
                    }
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS)
                }
                catch (sendEx: IntentSender.SendIntentException) {
                    //Ignore the error
                }
            }
        }
    }
}
