package com.bignerdranch.android.photogallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        // проверка разместился ли фрагмент в контейнере ( если нет то добавляем его в контейнер )
        val isFragmentContainerEmpty = savedInstanceState == null
        if(isFragmentContainerEmpty ){ // если фрагмента в контейнере ( в пакете savedInstanceState )   нет savedInstanceState == null, тогда:
            supportFragmentManager // класс, отвечающий за выполнение действий с фрагментами вашего приложения
                .beginTransaction() // перед транзакцией . Фрагментная транзакция может быть создана / зафиксирована только до того, как действие сохранит свое состояние
                .add(R.id.fragmentContainer,PhotoGalleryFragment.newInstance()) //добавление фрагмента в контейнер через add уназывается (id xml контейнера и id kt контейнера)
                .commit() //позволяет выполнить фиксацию, после этого состояние активности сохраняется.
        }
    }
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoGalleryActivity::class.java)
        }
    }
}