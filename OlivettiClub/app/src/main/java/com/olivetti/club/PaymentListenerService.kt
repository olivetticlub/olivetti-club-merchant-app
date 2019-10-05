package com.olivetti.club

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.olivetti.club.repositories.MerchantRepository
import com.olivetti.club.utils.CallbackHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentListenerService : IntentService("PAYMENT_COMPLETED_LISTENER") {
    private val service = OlivettiClubBackendServiceFactory.create()
    lateinit var merchantRepository: MerchantRepository
    private val TAG = this::class.java.simpleName

    override fun onHandleIntent(intent: Intent?) {

        val consumeCouponRequest = CouponConsumeRequest(merchantRepository.loadMerchant()!!)

        val callbackHandler = CallbackHandler<CouponConsumeResponse>(applicationContext, TAG)
        service.consumeCoupon(consumeCouponRequest).enqueue(callbackHandler)
    }
}
