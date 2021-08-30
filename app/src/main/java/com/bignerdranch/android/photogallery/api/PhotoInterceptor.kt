package com.bignerdranch.android.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "e0f9210c031f9b5ec327f2c9271c8280"

    // перехватчик ( перехватывает запрос или ответ и позволяет манипулироватьсодержимым содержимым)
class PhotoInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request() // извлекает исходный URL из запроса

        // добавление параметров запроса
        val newUrl: HttpUrl = originalRequest.url().newBuilder() // создаёт новый запрос на оснонове оригинального
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format","json")
            .addQueryParameter("nojsoncallback","1")
            .addQueryParameter("extras","url_s")
            .addQueryParameter("safesearch","1")
            .build()

        // заменяет исходный URL на новый
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest) // создание ответа
    }
}