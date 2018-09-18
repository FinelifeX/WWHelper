package ru.kpfu.itis.wwhelper.model.provider

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import ru.kpfu.itis.wwhelper.ui.MainActivity
import ru.kpfu.itis.wwhelper.model.user.User

object DataProvider {

    fun provideData(context: Context, auth: FirebaseAuth?) {
        UserProvider.provideUser().addOnCompleteListener {
            if (auth != null) {
                googleLogin(auth)
            }
            MainActivity.start(context)
        }
    }

    private fun googleLogin(auth: FirebaseAuth) {
        val user = UserProvider.getCurrentUser()
        if (user == null) {
            UserProvider.createUser(
                    User(auth.uid, auth.currentUser?.displayName,
                            auth.currentUser?.photoUrl.toString())
            )
        }
    }
}