package com.olivetti.club

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_confirm_coupon_generation.*

class CouponGenerationConfirmActivity : Activity() {

    companion object {
        const val COUPON_NUMBER_INTENT_KEY = "COUPON_NUMBER_INTENT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_coupon_generation)

        val couponNumber = intent.getIntExtra(COUPON_NUMBER_INTENT_KEY, 0)

        textView.text =
            "Congrats, i tuoi $couponNumber coupon sono stati caricati e saranno mandati a potenziali clienti"
    }
}
