package com.example.nutritrack.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.nutritrack.viewmodels.DiaryViewModel
import timber.log.Timber

class DateReceiver(
//    private val diaryViewModel: DiaryViewModel
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        StringBuilder().apply {
            if (intent != null) {
                append("Action: ${intent.action}\n")
            }
            if (intent != null) {
                append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            }
            toString().also { log ->
                Log.d("DateReceiver", log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }
}