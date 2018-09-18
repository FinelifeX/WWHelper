package ru.kpfu.itis.wwhelper.ui.profile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.ui.clothing.ThingListActivity
import ru.kpfu.itis.wwhelper.ui.login.LoginActivity


/*
*** Created by Bulat Murtazin on 27.08.2018 ***
*/

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() : ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initClickListeners(view)
        fillViews(view)
        return view
    }

    private fun initClickListeners(view: View) {
        view.button_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            UserProvider.clear()
            LoginActivity.start(activity!!.baseContext)
        }
        view.layout_open_clothes_list.setOnClickListener {
            startActivity(Intent(activity?.baseContext, ThingListActivity::class.java))
        }
    }

    private fun fillViews(view: View) {
        val auth = FirebaseAuth.getInstance()
        Glide
                .with(activity!!.baseContext)
                .load(auth.currentUser?.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(view.iv_user_avatar)
        view.tv_user_name.text = UserProvider.getCurrentUser()?.username
        view.tv_user_email.text = auth.currentUser?.email
    }
}