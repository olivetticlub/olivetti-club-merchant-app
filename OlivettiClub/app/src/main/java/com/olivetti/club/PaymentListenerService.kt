package com.olivetti.club

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.olivetti.club.repositories.MerchantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentListenerService : IntentService("PAYMENT_COMPLETED_LISTENER") {
    private val service = OlivettiClubBackendServiceFactory.create()
    lateinit var merchantRepository: MerchantRepository
    private val TAG = this::class.java.simpleName


    @ExperimentalCoroutinesApi
    override fun onHandleIntent(intent: Intent?) {
      //  val printerService = PrinterService(this)
        merchantRepository = MerchantRepository(this)
        val consumeCouponRequest = CouponConsumeRequest(merchantRepository.loadMerchant()!!)


        service.consumeCoupon(consumeCouponRequest)
            .enqueue(object : Callback<CouponConsumeResponse> {
                override fun onResponse(
                    call: Call<CouponConsumeResponse>,
                    response: Response<CouponConsumeResponse>
                ) {
                    Log.d(TAG, response.body().toString())
                  //  printerService.printCoupon(response.body()!!.deal)
                }

                override fun onFailure(call: Call<CouponConsumeResponse>, t: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
    }
}
