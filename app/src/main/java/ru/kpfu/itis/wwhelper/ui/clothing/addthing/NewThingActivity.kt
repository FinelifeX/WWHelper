package ru.kpfu.itis.wwhelper.ui.clothing.addthing

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_thing.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.ColorTags
import ru.kpfu.itis.wwhelper.model.clothing.Thing
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.util.Clothes
import ru.kpfu.itis.wwhelper.util.colortag.ColorTagApi
import ru.kpfu.itis.wwhelper.util.lastTakenPhotoBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class NewThingActivity : AppCompatActivity() {

    private lateinit var api: ColorTagApi.ApiInterface

    private lateinit var currentUUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_thing)

        setSupportActionBar(toolbar_new_thing)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        api = ColorTagApi.getClient().create(ColorTagApi.ApiInterface::class.java)

        Glide.with(baseContext)
                .load(lastTakenPhotoBitmap)
                .into(iv_new_thing)

        btn_choose_type.setOnClickListener {
            val items = arrayOf("Chest", "Legs", "Outer")
            AlertDialog.Builder(this).setTitle("Choose type")
                    .setItems(items) { dialog, which ->
                        dialog.dismiss()
                        if (which == 0) {
                            AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.chestClothes) { dialog, which ->
                                        btn_choose_type.text = Clothes.chestClothes[which]
                                    }.create().show()
                        }
                        else if (which == 1) {
                            AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.legsClothes) {dialog, which ->
                                        btn_choose_type.text = Clothes.chestClothes[which]
                                    }.create().show()
                        }
                        else {
                            AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.outerClothes) {dialog, which ->
                                        btn_choose_type.text = Clothes.outerClothes[which]
                                    }.create().show()
                        }
                    }.create().show()
        }

        btn_save.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().reference
            val currentUser = UserProvider.getCurrentUser()
            currentUUID = UUID.randomUUID().toString()
            var downloadUri: String
            val storageRef =
                    FirebaseStorage.getInstance().reference
                            .child("images")
                            .child(currentUser?.uid.toString())
                            .child("$currentUUID.png")

            val imgByteArray = convertImageToByteArray(lastTakenPhotoBitmap!!)
            val uploadTask = storageRef.putBytes(imgByteArray)

            uploadTask.addOnSuccessListener { p0 ->
                Log.e("Upload picture", "Success!")
                val urlTask = p0!!.storage.downloadUrl
                while (!urlTask.isSuccessful) { }
                downloadUri = urlTask.result.toString()
                val currentType: String = if (btn_choose_type.text.toString() == resources.getString(R.string.choose_type)) {
                    "Shirt"
                } else {
                    btn_choose_type.text.toString()
                }
                dbRef.child("things")
                        .child(currentUser?.uid.toString())
                        .child(currentUUID)
                        .setValue(Thing(
                                currentUUID,
                                tv_thing_color.text.toString(),
                                currentType,
                                downloadUri))
                getColors(Uri.decode(downloadUri))
            }
        }
    }

    private fun getColors(pictureUrl: String) {
        Log.e("getColors()", "invoked!")

        val callColors = api.getColors("simple", "weight", pictureUrl)
        callColors.enqueue(object : Callback<ColorTags> {
            override fun onFailure(call: Call<ColorTags>, t: Throwable) {
                Log.e("callColors", "onFailure")
            }

            override fun onResponse(call: Call<ColorTags>, response: Response<ColorTags>) {
                val data = response.body()
                Log.e("colorRecognition", data?.tags?.get(0)?.label)
                tv_thing_color.text = data?.tags?.get(0)?.label
                finishActivityWithOKResult()
            }

        })

        //Bitmap to PNG
        val imgCompressed = File(baseContext.filesDir, "lastTakenImage")
        val fOut = FileOutputStream(imgCompressed)
        lastTakenPhotoBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.close()
        fOut.flush()



    }

    private fun convertImageToByteArray(bitmap: Bitmap) : ByteArray {
        val bm = bitmap
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    private fun finishActivityWithOKResult() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
