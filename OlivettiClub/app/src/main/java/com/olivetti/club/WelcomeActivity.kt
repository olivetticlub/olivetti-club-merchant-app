package com.olivetti.club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        nextButton.setOnClickListener {
            startActivity(Intent(this, MerchantOnboardingActivity::class.java))
            finish()
        }
    }

}
