package ru.kpfu.itis.wwhelper.model.provider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ru.kpfu.itis.wwhelper.model.user.User

object UserProvider {

    private var dbRef = FirebaseDatabase.getInstance().reference

    private var user: User? = null

    private lateinit var listener: () -> Unit

    fun addOnCompleteListener(listenerImpl: () -> Unit) {
        listener = listenerImpl
    }

    fun provideUser(): UserProvider {
        val uid = FirebaseAuth.getInstance().uid.toString()
        when (user) {
            null -> dbRef.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //Failed to read value
                }

                override fun onDataChange(p0: DataSnapshot) {
                    user = p0.getValue(User::class.java)
                    listener()
                }

            })
            else -> listener()
        }
        return UserProvider
    }

    fun createUser(user: User) {
        dbRef.child("users").child(user.uid.toString()).setValue(user)
        this.user = user
    }

    fun clear() {
        user = null
    }

    fun getCurrentUser() : User? = user

    fun updateUser(user: User) {
        dbRef.child("users").child(this.user?.uid.toString()).setValue(user)
    }
}