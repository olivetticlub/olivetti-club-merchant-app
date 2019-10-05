package com.olivetti.club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class MerchantOnBoardingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_on_boarding)

        nextButton.setOnClickListener {
            startActivity(Intent(this, GenerateCouponActivity::class.java))
            finish()
        }
    }


}
