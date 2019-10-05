package com.olivetti.club.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallbackHandler<T>(private val context: Context, private val className: String) : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        Toast.makeText(context, "errore", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(
        call: Call<T>,
        response: Response<T>
    ) {
        Log.d(className, response.body().toString())
    }

}