package ru.kpfu.itis.wwhelper.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.provider.DataProvider
import ru.kpfu.itis.wwhelper.util.RC_SIGN_IN

/*
*** Created by Bulat Murtazin on 24.08.2018 ***
*/

class LoginActivity : Activity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent, null)
        }
    }

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initGoogleSignInButton();
    }

    private fun initGoogleSignInButton() {
        btn_sign_with_google.setOnClickListener {
            val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_SIGN_IN -> {
                    loadData(this, auth)
                }
            }
        }
    }

    private fun loadData(context: Context, auth: FirebaseAuth?) {
        DataProvider.provideData(context, auth)
    }
}