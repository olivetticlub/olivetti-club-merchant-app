package com.olivetti.club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_merchant_on_boarding.*
import kotlinx.android.synthetic.main.activity_welcome.nextButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MerchantOnBoardingActivity : Activity() {
    val service = OlivettiClubBackendServiceFactory.create()
    val TAG = MerchantOnBoardingActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_on_boarding)

        nextButton.setOnClickListener {
            createMerchant(
                shopName.text.toString(),
                vat.text.toString(),
                shopAddresss.text.toString(),
                ateco.text.toString()
            )
            startActivity(Intent(this, GenerateCouponActivity::class.java))
            finish()
        }
    }

    private fun createMerchant(shopName: String, vat: String, shopAddress: String, ateco: String) {
        val request = MerchantCreationRequest(
            shopName,
            vat,
            ateco,
            shopAddress
        )
        Log.d(TAG, request.toString())
        service.createMerchant(request)
            .enqueue(object :
                Callback<MerchantCreationResponse> {
                override fun onFailure(call: Call<MerchantCreationResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "errore", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<MerchantCreationResponse>,
                    response: Response<MerchantCreationResponse>
                ) {
                    Log.d(TAG, response.body().toString())
                }

            })
    }


}
