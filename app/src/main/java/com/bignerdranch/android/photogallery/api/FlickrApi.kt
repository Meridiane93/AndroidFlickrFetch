package com.bignerdranch.android.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

// каждая функция в интерфейсе привязана к конкретному HTTP запросу и должна быть аннотирована HTTP запроса
// Аннотация метода HTTP сообщает Retrofit тип HTTP запроса
// @GET("/") // настраивает Call (запрос) возвращаемый функцией fetchContents(), на выполнение Get запроса. "/" - означает на какой адрес будет отправлен запрос

interface FlickrApi { // определение запроса ( получить недавние интересные фотографии )
    // определение поискового запроса
    @GET("services/rest?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String):Call<ResponseBody>

    @GET("services/rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query:String): Call<FlickrResponse> // аннотация @Query позволяет динамически добавлять к URL параметры запроса
}