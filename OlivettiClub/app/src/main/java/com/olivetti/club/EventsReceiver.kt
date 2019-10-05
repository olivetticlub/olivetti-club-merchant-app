package com.olivetti.club

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class EventsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "EVENT RECEIVED", Toast.LENGTH_LONG).show()

        context.startService(Intent(context, PaymentListenerService::class.java).apply {
            action = intent.action
        })
    }
}