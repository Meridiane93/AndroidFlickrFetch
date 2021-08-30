package com.bignerdranch.android.photogallery.api

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0 // будет использоваться для идентификации сообщений ( как запросов на загрузку )

// загрузка и передачаизображения в PhotoGalleryFragment
// реализация LifecycleObserver даёт возожность получать обратные вызовы жизненного цикла от любого владельца
// для этого используются аннотации @OnLifecycleEvent
class ThumbnailDownloader<in T>(private val responseHandler: Handler,private val onThumbnailDownloader: (T, Bitmap)->Unit):
    HandlerThread(TAG), LifecycleObserver {  // передаётся один обобщенный аргумент T

    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        // связывание данного класса с жизненным циклом приложения. Класс сам себя будет запускать,
        // как только будет запущена функция ON_CREATE и останавливать при ON_DESTROY
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup(){
            start()
            looper
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown(){
            quit()
        }
    }

    val viewLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue(){
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    private var hasQuit = false // поток запущен
    private lateinit var requestHandler: Handler // хранится ссылка на объект Handler ( твечает за постановку в очередь запросов на загрузку в фоновом потоке )
    private val requestMap = ConcurrentHashMap<T,String>() // хранит и загружает конкретный URL адрес связанный с запросом
    private val flickrFetchr = FlickrFetchr()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(){
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD){
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit() //  quit() говорит о завершении потока
    }

    // ожидает получить обобщённый аргумент (выполняющий функии идентификатора загрузки) и String с URL адресом для загрузки
    fun queueThimbnail(target:T, url:String){
        Log.i(TAG,"Got a URL: $url")
        requestMap[target] = url // обновление переменной requestMap
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)  // сообщение берётся из  requestHandler
            .sendToTarget() // отправить сообщение
    }

    private fun handleRequest(target: T){
        val url = requestMap[target] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return
        responseHandler.post(Runnable { if (requestMap[target] != url || hasQuit){
            return@Runnable
        }
            requestMap.remove(target)
            onThumbnailDownloader(target,bitmap)

        })
    }
}