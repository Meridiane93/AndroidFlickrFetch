package com.bignerdranch.android.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val  TAG = "FlickrFetchr"

        // FlickrFetchr - своего рода простой реппозиторий ( инкапсулирует логику доступа к данным из одного источника или нескольких )
        // Определяет как получать и хранить определённый набор данных
        // Пользовательский интерфейс запрашивает все данные из реппозитория ( ему не важно как данные хранятся или извлекаются на самом деле )
class FlickrFetchr {

    private val flickrApi: FlickrApi

    init {  // содержит конфигурацию Retrofit  и создание экземпляра API интерфейса
        // перехватчик
        val client = OkHttpClient.Builder() // создаём экземпляр OkHttpClient
            .addInterceptor(PhotoInterceptor())  // добавляем PhotoInterceptor ( в качестве перехватчика )
            .build()

        val retrofit: Retrofit = Retrofit.Builder() // настройка и сборка экземпляра Retrofit
            .baseUrl("https://api.flickr.com/") // задание базового URL
            .addConverterFactory(GsonConverterFactory.create()) //добавлние конвертера(который декодирует объект ResponseBody в String,чтобы ретровит возвращал ответ типа String) возвращает экземпляр скалярного конвертера
            .client(client) // устанавливаем настроенный клиент на экземпляр Retrofit
            .build() // возвращает экземпляр Retrofit с настройками

        // получив экземпляр Retrofit(val retrofit) создаём экземпляр нашего API (FlickrApi)  указанными аннотациями
        flickrApi = retrofit.create(FlickrApi::class.java) // Retrofit использует информацию в указанном интерфецсе
    }
            fun fetchPhotosRequest(): Call<FlickrResponse>{
                return flickrApi.fetchPhotos()
            }

            // добавление функции поиска в FlickrFetch
            fun fetchPhotos(): LiveData<List<GalleryItem>>{
                return fetchPhotoMetadata(fetchPhotosRequest())
            }

            fun searchPhotosRequest(query: String):Call<FlickrResponse>{
                return flickrApi.searchPhotos(query)
            }

            fun searchPhotos(query: String):LiveData<List<GalleryItem>>{
                return fetchPhotoMetadata(searchPhotosRequest(query))
            }

            private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>): LiveData<List<GalleryItem>>{
                val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData() // присваиваем значение responseLiveData пустому обьекту MutableLiveData<String>
    // теперь другие компоненты приложения ( например ViewModel, PhotoGalleryFragment или activity могут создавать экземпляр FlickrFetchr и запрашивать данные фотографии
    // оборачиваем API Retrofit в LiveData и ставит в очередь веб-запрос и возвращаем responseLiveData


        flickrRequest.enqueue(object :Callback<FlickrResponse>{ // выполнение веб запроса и передача экземпляра retrofit2.Callback

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) { // если ответ от сервера  неполучен будет вызвана onFailure
                Log.d(TAG,"onFailure")
            }

            // если ответ от сервера получен будет вызвана onResponse
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot { it.url.isBlank() }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }
            @WorkerThread // аннотация указывает на то что эта функция должна вызываться только в фоновом потоке ( только даёт указания, но не создаёт фоновый поток)
            fun fetchPhoto(url: String): Bitmap? {
                val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute() // execute() синхронно выполняет веб-запрос, работа с сетью в основном потоке запрещена
                return response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
            }
}