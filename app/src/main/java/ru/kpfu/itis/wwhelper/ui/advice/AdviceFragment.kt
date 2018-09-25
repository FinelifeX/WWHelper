package ru.kpfu.itis.wwhelper.ui.advice

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_advice.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.Thing
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.model.weather.Weather
import ru.kpfu.itis.wwhelper.util.Clothes
import ru.kpfu.itis.wwhelper.util.weatherResponseBody


/*
*** Created by Bulat Murtazin on 01.09.2018 ***
*/

class   AdviceFragment : Fragment() {

    var suggestedItems = mutableListOf<Thing>()
//    var chestItems = mutableListOf<Thing>()
//    var legsItems = mutableListOf<Thing>()
//    var outerItems = mutableListOf<Thing>()
    var weatherCharacteristic = listOf<String>()

    private lateinit var adapter: AdviceRecyclerViewAdapter;

    companion object {
        fun newInstance() : AdviceFragment {
            return AdviceFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_advice, container, false)
        view.rv_advice.layoutManager = LinearLayoutManager(activity?.baseContext)
        adapter = AdviceRecyclerViewAdapter(suggestedItems, activity!!.baseContext)
        view.rv_advice.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val checker = WeatherDataChecker(view)
        weatherCharacteristic =  checker
                .execute()
                .get()

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                if (weatherCharacteristic.isEmpty()) {
                    while (weatherCharacteristic.isEmpty()) {

                    }
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                getSuggestedItemsList()
            }
        }.execute()
    }

    //TODO Refactor method
    private fun getSuggestedItemsList() {
        FirebaseDatabase.getInstance().reference
                .child("things")
                .child(UserProvider.getCurrentUser()?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.e("getSuggestedItems", "Error")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (child in p0.children) {
                            val item = child.getValue(Thing::class.java)
                            if (item != null) {
                                filterItem(item)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                })
    }

    private fun filterItem(thing: Thing) {
        if (thing.type in weatherCharacteristic) {
            suggestedItems.add(thing)
        }
    }

    class WeatherDataChecker(val view: View) : AsyncTask<Void, List<String>, List<String>>() {

        lateinit var message : String

        override fun onPreExecute() {
            Log.e("WeatherDataChecker", "Starting")
        }

        override fun doInBackground(vararg p0: Void?): List<String>? {
            if (weatherResponseBody == null) {
                while (weatherResponseBody == null) {
                    //Just wait
                }
            }
            return defineWeather(weatherResponseBody)
        }

        override fun onPostExecute(result: List<String>?) {

            view.fb_warning.setOnClickListener {
                AlertDialog.Builder(view.context)
                        .setTitle("Achtung!")
                        .setMessage(message)
                        .setNeutralButton("Understood") { p0, p1 -> p0!!.dismiss() }
                        .create().show()
            }
        }

        private fun defineWeather(weather: Weather?) : List<String> {

            lateinit var weatherCharacteristic: List<String>

            if (weather != null) {
                if (weather.tempInteger.toInt() > 23) {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forHotEasy
                            message = "It's hot today. Think about wearing a cap!"
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forHotMedium
                            message = "It's hot today. Think about wearing a cap!"
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forHotMedium
                            message = "It's hot but windy today. May be you'd like to wear some kind of a shawl?"
                        }
                    }
                } else if (weather.tempInteger.toInt() >= 18) {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forWarmEasy
                            message = "It's warm today."
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forWarmMedium
                            message = "It's warm today."
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forHotMedium
                            message = "It's warm and windy today."
                        }
                    }

                } else if (weather.tempInteger.toInt() >= 14) {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forNormalEasy
                            message = "It's not really warm today."
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forNormalEasy
                            message = "It's not really warm today."
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forNormalHard
                            message = "It could be cold today, be careful!"
                        }

                    }
                } else if (weather.tempInteger.toInt() >= 8) {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forColdEasy
                            message = "It's cold today."
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forColdEasy
                            message = "It's cold today."
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forColdEasy
                            message = "It's really cold and windy today. Be careful and wear warm clothes!"
                        }

                    }
                } else if (weather.tempInteger.toInt() >= 0) {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forFreezingEasy
                            message = "It's freezing!"
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forFreezingEasy
                            message = "It's freezing with medium speed wind today."
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forFreezingEasy
                            message = "It's freezing and... FREEZING!"
                        }

                    }
                } else {
                    when {
                        weather.windDouble!!.toInt() <= 2 -> {
                            weatherCharacteristic = Clothes.forWinter
                            message = "It's winter!"
                        }
                        weather.windDouble!!.toInt() <= 4 -> {
                            weatherCharacteristic = Clothes.forWinter
                            message = "It's winter! Make sure to cover your neck with scarf."
                        }
                        else -> {
                            weatherCharacteristic = Clothes.forWinter
                            message = "It's winter! We highly recommend you to wear a scarf."
                        }

                    }
                }
            }
            else {
                weatherCharacteristic = Clothes.forHotEasy
                message = "Dunno???"
            }

            if (weather != null) {
                val rainValue : Int = if (weather.rainText == "null") {
                    0
                } else weather.rainText!!.toInt()
                when {
                    rainValue == 0 -> {}
                    rainValue < 3 -> message += " Also, it looks like there can be a rain!"
                    rainValue >= 3 -> message += " It looks rainy too... Umbrella is recommended!"
                }
            }

            if (weather != null) {
                val snowValue = if (weather.snowText == "null") {
                    0
                } else weather.snowText!!.toInt()
                when {
                    snowValue == 0 -> {}
                    snowValue < 3 -> message += " Also, it looks like there can be a snow today!"
                    snowValue >= 3 -> message += " It looks like really snowy day!"

                }
            }

            return weatherCharacteristic;
        }

    }
}