package com.olivetti.club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_generate_coupon.*

class GenerateCouponActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_coupon)


        couponNumberPicker.minValue = 1
        couponNumberPicker.maxValue = 10
        couponNumberPicker.displayedValues = (1..10).map { "${it * 10}" }.toTypedArray()
        couponNumberPicker.wrapSelectorWheel = false

        discountPicker.minValue = 1
        discountPicker.maxValue = 19
        discountPicker.displayedValues = (1..19).map { "${it * 5}" }.toTypedArray()
        discountPicker.wrapSelectorWheel = false

        generateButton.setOnClickListener {
            startActivity(Intent(this, CouponGenerationConfirmActivity::class.java).apply {
                putExtra(
                    CouponGenerationConfirmActivity.COUPON_NUMBER_INTENT_KEY,
                    couponNumberPicker.value * 10
                )
            })
            finish()
        }
    }
}
