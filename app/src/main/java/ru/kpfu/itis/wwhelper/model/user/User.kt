package ru.kpfu.itis.wwhelper.model.user

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import ru.kpfu.itis.wwhelper.model.clothing.Thing


/*
*** Created by Bulat Murtazin on 24.08.2018 ***
*/

@IgnoreExtraProperties
data class User(@Exclude var uid: String? = null,
                var username: String? = null,
                var avatarUrl: String? = null)