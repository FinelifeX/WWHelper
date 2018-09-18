package ru.kpfu.itis.wwhelper.model.clothing

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable


/*
*** Created by Bulat Murtazin on 24.08.2018 ***
*/

@IgnoreExtraProperties
data class Thing(@Exclude var uid: String? = null,
                 var color: String? = null,
                 var type: String? = null,
                 var imgUrl: String? = null) : Serializable {
}