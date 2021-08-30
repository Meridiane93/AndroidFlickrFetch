package com.bignerdranch.android.photogallery.api

import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem> // добавление свойства galleryItems для хранения списка галерейных обьектов

}