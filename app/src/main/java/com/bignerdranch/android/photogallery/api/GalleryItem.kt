package com.bignerdranch.android.photogallery.api

import android.net.Uri
import androidx.core.net.UriCompat
import com.google.gson.annotations.SerializedName

// модель для фотографий ( модель изображения с названием, идентификатором и url адресом )
data class GalleryItem (
        var title: String = "",
        var id: String = "",
        @SerializedName("url_s") var url: String = "", // чтобы Gson добавлял в свойство url, полученные данные из свойства url_s ( формата JSON )
        @SerializedName("owner") var owner: String = ""
        ){
        val photoPageUri: Uri
        get(){
                return Uri.parse("https://www.flickr.com/photos/")
                        .buildUpon()
                        .appendPath(owner)
                        .appendPath(id)
                        .build()
        }
}