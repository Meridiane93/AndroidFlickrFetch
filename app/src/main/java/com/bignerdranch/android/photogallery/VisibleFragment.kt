package com.bignerdranch.android.photogallery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bignerdranch.android.photogallery.api.PollWorker

private const val TAG ="VisibleFragment"

abstract class VisibleFragment:Fragment() {
    private val onShowNotification = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(requireContext(),"Есть трансляция:${intent.action}",Toast.LENGTH_LONG)
                .show()
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowNotification,
            filter,
            PollWorker.PERM_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}