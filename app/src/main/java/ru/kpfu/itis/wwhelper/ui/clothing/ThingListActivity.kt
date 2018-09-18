package ru.kpfu.itis.wwhelper.ui.clothing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_list_things.*
import ru.kpfu.itis.wwhelper.R
import ru.kpfu.itis.wwhelper.model.clothing.Thing
import ru.kpfu.itis.wwhelper.model.provider.UserProvider
import ru.kpfu.itis.wwhelper.ui.clothing.addthing.NewThingActivity
import ru.kpfu.itis.wwhelper.util.RC_NEW_THING
import ru.kpfu.itis.wwhelper.util.RC_TAKE_PICTURE
import ru.kpfu.itis.wwhelper.util.REQUEST_PERMISSION_CAMERA
import ru.kpfu.itis.wwhelper.util.lastTakenPhotoBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ThingListActivity : AppCompatActivity() {

    private val listOfThings = mutableListOf<Thing>()

//    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_things)

        setSupportActionBar(toolbar_list_things)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        getData()
        checkAccessCameraPermission()

        val viewManager = LinearLayoutManager(this)
        val viewAdapter = ThingListAdapter(listOfThings, this)
        rv_list_things.layoutManager = viewManager
        rv_list_things.adapter = viewAdapter
        fb_list_things.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
//                var photoFile: File? = null
//                try {
//                    photoFile = createImageFile()
//                } catch (e: IOException) {
//                    Log.e("takePhoto", "ERROR")
//                }
//
//                if (photoFile != null) {
//                    val photoUri = FileProvider.getUriForFile(this,
//                            "ru.kpfu.itis.wwhelper.fileprovider",
//                            photoFile)
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
//                }
                startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
            }
        }
    }

    private fun getData() {
        FirebaseDatabase.getInstance().reference
                .child("things")
                .child(UserProvider.getCurrentUser()?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.e("TLA -> onCancelled()", "onCancelled()")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (thing in p0.children) {
                            listOfThings.add(thing.getValue(Thing::class.java)!!)
                            rv_list_things.adapter.notifyDataSetChanged()
                        }
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            val addThingIntent = Intent(this, NewThingActivity::class.java)

//            val bm = BitmapFactory.decodeFile(currentPhotoPath)
//            val baos = ByteArrayOutputStream()
//            bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
//            val byteArray = baos.toByteArray()

//            addThingIntent.putExtra("imageLocalPath", currentPhotoPath)
            lastTakenPhotoBitmap = data?.extras?.get("data") as Bitmap
            startActivityForResult(addThingIntent, RC_NEW_THING)
        }

    }

//    private fun createImageFile() : File {
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "PNG_${timestamp}_"
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//                imageFileName, ".png", storageDir
//        )
//        currentPhotoPath = image.absolutePath
//        return image
//    }

    private fun checkAccessCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {

            }
            else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }
        }
        else {
            //permission granted already
        }
    }
}
