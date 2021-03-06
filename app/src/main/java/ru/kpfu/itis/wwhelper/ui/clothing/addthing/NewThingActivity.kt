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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    private var currentUUID = UUID.randomUUID().toString()

    private var currentType: String = "Shirt"

    val currentUser = UserProvider.getCurrentUser()

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
                        when (which) {
                            0 -> AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.chestClothes) { dialog, which ->
                                        btn_choose_type.text = Clothes.chestClothes[which]
                                        currentType = Clothes.chestClothes[which]
                                    }.create().show()
                            1 -> AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.legsClothes) {dialog, which ->
                                        btn_choose_type.text = Clothes.legsClothes[which]
                                        currentType = Clothes.legsClothes[which]
                                    }.create().show()
                            else -> AlertDialog.Builder(this).setTitle("Choose type")
                                    .setItems(Clothes.outerClothes) {dialog, which ->
                                        btn_choose_type.text = Clothes.outerClothes[which]
                                        currentType = Clothes.outerClothes[which]
                                    }.create().show()
                        }
                    }.create().show()
        }

        btn_save.setOnClickListener {
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
                val downloadUri = urlTask.result.toString()

                //Bitmap to PNG
                val imgCompressed = File(baseContext.filesDir, "lastTakenImage")
                val fOut = FileOutputStream(imgCompressed)
                lastTakenPhotoBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.close()
                fOut.flush()

                getColors(imgCompressed, "simple", "weight", downloadUri)
            }
        }
    }

    private fun getColors(file: File, palette: String, sort: String, imgUrl: String) {
        Log.e("getColors()", "invoked!")

        val filePart = MultipartBody.Part.createFormData("image", file.name, RequestBody.create(MediaType.parse("image/*"), file))
        val paletteTemp = RequestBody.create(MediaType.parse("text/plain"), palette)
        val sortTemp = RequestBody.create(MediaType.parse("text/plain"), sort)

        val callColors = api.getColors(filePart, paletteTemp, sortTemp)
        callColors.enqueue(object : Callback<ColorTags> {
            override fun onFailure(call: Call<ColorTags>, t: Throwable) {
                Log.e("callColors", "onFailure")
            }

            override fun onResponse(call: Call<ColorTags>, response: Response<ColorTags>) {
                val data = response.body()
                Log.e("colorRecognition", data?.tags?.get(0)?.label)
                val currentColor = data!!.tags.get(0).label
                val dbRef = FirebaseDatabase.getInstance().reference
                dbRef.child("things")
                        .child(currentUser?.uid.toString())
                        .child(currentUUID)
                        .setValue(Thing(
                                currentUUID,
                                currentColor,
                                currentType,
                                imgUrl))
                finishActivityWithOKResult()
            }

        })
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
