package com.bignerdranch.android.photogallery.api

import android.app.Application
import androidx.lifecycle.*
import com.bignerdranch.android.photogallery.QueryPreferences

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    private val flickrFetch = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) flickrFetch.fetchPhotos()
            else flickrFetch.searchPhotos(searchTerm)
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app,query)
        mutableSearchTerm.value = query
    }
}