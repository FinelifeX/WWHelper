package ru.kpfu.itis.wwhelper.util.colortag

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.kpfu.itis.wwhelper.model.clothing.ColorTags


/*
*** Created by Bulat Murtazin on 29.08.2018 ***
*/

class ColorTagApi {

    companion object {
        const val KEY = "tkWiGgNmwXmshxHOwiZFCRsnXlaYp1otE6EjsnLxohSwKekCHQ"
        private var retrofit: Retrofit? = null

        fun getClient() : Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl("https://apicloud-colortag.p.mashape.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }
    }

    interface ApiInterface {
        @Multipart
        @POST("tag-file.json")
        @Headers("X-Mashape-Key: tkWiGgNmwXmshxHOwiZFCRsnXlaYp1otE6EjsnLxohSwKekCHQ")
        fun getColors(
                @Part("image") filePart: MultipartBody.Part,
                @Part("palette") palette: RequestBody,
                @Part("sort") sort: RequestBody
        ) : Call<ColorTags>
    }
}