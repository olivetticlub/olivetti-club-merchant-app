package com.olivetti.club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.olivetti.club.repositories.MerchantRepository
import kotlinx.android.synthetic.main.activity_generate_coupon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateCouponActivity : Activity() {
    private val service = OlivettiClubBackendServiceFactory.create()
    lateinit var merchantRepository: MerchantRepository
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_coupon)

        merchantRepository = MerchantRepository(this)

        couponNumberPicker.minValue = 1
        couponNumberPicker.maxValue = 10
        couponNumberPicker.displayedValues = (1..10).map { "${it * 10}" }.toTypedArray()
        couponNumberPicker.wrapSelectorWheel = false

        discountPicker.minValue = 1
        discountPicker.maxValue = 19
        discountPicker.displayedValues = (1..19).map { "${it * 5}" }.toTypedArray()
        discountPicker.wrapSelectorWheel = false


        generateButton.setOnClickListener {
            val numberOfCoupons = couponNumberPicker.value * 10
            val discountAmount = discountPicker.value * 5
            generateCoupon(numberOfCoupons, discountAmount)
            startActivity(Intent(this, CouponGenerationConfirmActivity::class.java).apply {
                putExtra(
                    CouponGenerationConfirmActivity.COUPON_NUMBER_INTENT_KEY,
                    numberOfCoupons
                )
            })
            finish()
        }
    }

    private fun generateCoupon(numberOfCoupons: Int, discountAmount: Int) {
        val createCouponRequest = CouponCreationRequest(
            merchantRepository.loadMerchant()!!,
            "Get a product for ${discountAmount}% off!",
            numberOfCoupons
        )
        Log.d(TAG, createCouponRequest.toString())
        service.createCoupon(createCouponRequest)
            .enqueue(object :
                Callback<Deal> {
                override fun onFailure(call: Call<Deal>, t: Throwable) {
                    Toast.makeText(applicationContext, "errore", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<Deal>,
                    response: Response<Deal>
                ) {
                    Log.d(TAG, response.body().toString())
                }

            })
    }
}
