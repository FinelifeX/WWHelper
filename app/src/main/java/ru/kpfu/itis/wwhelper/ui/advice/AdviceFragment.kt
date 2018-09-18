package ru.kpfu.itis.wwhelper.ui.advice

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
import kotlinx.android.synthetic.main.fragment_advice.*
import kotlinx.android.synthetic.main.fragment_advice.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.Thing
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.model.weather.Weather
import ru.kpfu.itis.wwhelper.util.Clothes


/*
*** Created by Bulat Murtazin on 01.09.2018 ***
*/

class AdviceFragment : Fragment() {

    var suggestedItems = mutableListOf<Thing>()
    var chestItems = mutableListOf<Thing>()
    var legsItems = mutableListOf<Thing>()
    var outerItems = mutableListOf<Thing>()

    companion object {
        fun newInstance() : AdviceFragment {
            return AdviceFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_advice, container, false)
        view.rv_advice.layoutManager = LinearLayoutManager(activity?.baseContext)
        view.rv_advice.adapter = AdviceRecyclerViewAdapter(suggestedItems, activity!!.baseContext)
        return view
    }

    private fun getSuggestedItemsList() {
        suggestedItems = mutableListOf()
        chestItems = mutableListOf()
        legsItems = mutableListOf()
        outerItems = mutableListOf()
        FirebaseDatabase.getInstance().reference
                .child("things")
                .child(UserProvider.getCurrentUser()?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.e("forHotRainlessWeather", "Error")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (child in p0.children) {
                            val item = child.getValue(Thing::class.java)
                            if (item != null) {
                                when {
                                    item.type in Clothes.chestClothes -> chestItems.add(item)
                                    item.type in Clothes.legsClothes -> legsItems.add(item)
                                    else -> outerItems.add(item)
                                }
                            }
                        }
                    }
                })
    }

    private fun getItemsForWeather(weather: Weather) {

        var characteristic: List<String>
        var warningMessage: String

        if (weather.tempInteger.toInt() > 23) {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forHotEasy
                    warningMessage = "It's hot today. Think about wearing a cap!"
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forHotMedium
                    warningMessage = "It's hot today. Think about wearing a cap!"
                }
                else -> {
                    characteristic = Clothes.forHotMedium
                    warningMessage = "It's hot but windy today. May be you'd like to wear some kind of a shawl?"
                }
            }
        }
        else if (weather.tempInteger.toInt() >= 18) {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forWarmEasy
                    warningMessage = "It's warm today."
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forWarmMedium
                    warningMessage = "It's warm today."
                }
                else -> {
                    characteristic = Clothes.forHotMedium
                    warningMessage = "It's warm and windy today."
                }
            }

        }
        else if (weather.tempInteger.toInt() >= 14) {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forNormalEasy
                    warningMessage = "It's not really warm today."
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forNormalEasy
                    warningMessage = "It's not really warm today."
                }
                else -> {
                    characteristic = Clothes.forNormalHard
                    warningMessage = "It could be cold today, be careful!"
                }

            }
        }
        else if (weather.tempInteger.toInt() >= 8) {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forColdEasy
                    warningMessage = "It's cold today."
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forColdEasy
                    warningMessage = "It's cold today."
                }
                else -> {
                    characteristic = Clothes.forColdEasy
                    warningMessage = "It's really cold and windy today. Be careful and wear warm clothes!"
                }

            }
        }
        else if (weather.tempInteger.toInt() >= 0) {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forFreezingEasy
                    warningMessage = "It's freezing!"
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forFreezingEasy
                    warningMessage = "It's freezing with medium speed wind today."
                }
                else -> {
                    characteristic = Clothes.forFreezingEasy
                    warningMessage = "It's freezing and... FREEZING!"
                }

            }
        }
        else {
            when {
                weather.windText!!.toInt() <= 2 -> {
                    characteristic = Clothes.forWinter
                    warningMessage = "It's winter!"
                }
                weather.windText!!.toInt() <= 4 -> {
                    characteristic = Clothes.forWinter
                    warningMessage = "It's winter! Make sure to cover your neck with scarf."
                }
                else -> {
                    characteristic = Clothes.forWinter
                    warningMessage = "It's winter! We highly recommend you to wear a scarf."
                }

            }
        }

        when {
            weather.rainText!!.toInt() == 0 -> {}
            weather.rainText!!.toInt() < 3 -> warningMessage += " Also, it looks like there can be a rain!"
            weather.rainText!!.toInt() >= 3 -> warningMessage += " It looks rainy too... Umbrella is recommended!"
        }

        when {
            weather.snowText!!.toInt() == 0 -> {}
            weather.snowText!!.toInt() < 3 -> warningMessage += " Also, it looks like there can be a snow today!"
            weather.snowText!!.toInt() >= 3 -> warningMessage += " It looks like really snowy day!"

        }

        fb_warning.setOnClickListener {
            AlertDialog.Builder(activity!!.baseContext)
                    .setMessage(warningMessage)
                    .setNeutralButton("Okay") {dialog, which -> dialog.dismiss() }
                    .create().show()
        }

        for (item in chestItems) {
            if (item.type in characteristic) {
                suggestedItems.add(item)
            }
        }
        for (item in legsItems) {
            if (item.type in characteristic) {
                suggestedItems.add(item)
            }
        }
        for (item in outerItems) {
            if (item.type in characteristic) {
                suggestedItems.add(item)
            }
        }

    }
}