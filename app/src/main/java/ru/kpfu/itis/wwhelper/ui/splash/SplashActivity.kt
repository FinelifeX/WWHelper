package ru.kpfu.itis.wwhelper.ui.splash

import android.Manifest
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
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

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkAccessLocationPermission()
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
            createLocationRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_COARSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    createLocationRequest()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("The weather forecast will be incorrect without access to device's location!" +
                                    " This leads to incorrect working. You can always allow it in the Settings.")
                            .setNeutralButton("Okay") { dialog, which ->
                                dialog.dismiss()
                                routeToAppropriatePage(user)
                            }.create().show()
                }
            }
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

        task.addOnSuccessListener {

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(baseContext)
            try {
                mFusedLocationProviderClient.lastLocation
                        .addOnSuccessListener {
                            location: Location? ->
                            if (location != null) {
                                currentLatitude = location.latitude
                                currentLongitude = location.longitude
                                routeToAppropriatePage(user)
                            }
                        }
            } catch (e: SecurityException) {
                Log.e("RequestLocation", "SecurityException")
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
            routeToAppropriatePage(user)
        }
    }
}
