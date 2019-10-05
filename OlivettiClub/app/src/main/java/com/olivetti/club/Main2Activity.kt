package com.olivetti.club


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class Main2Activity : AppCompatActivity() {

    val service = OlivettiClubBackendServiceFactory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        create_coupons.setOnClickListener {
            service.createCoupon(
                CouponCreationRequest(
                    "danielefongo",
                    "descrizione bella",
                    2
                )
            )
                .enqueue(object :
                    Callback<Deal> {
                    override fun onFailure(call: Call<Deal>, t: Throwable) {
                        Toast.makeText(applicationContext, "errore", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<Deal>,
                        response: Response<Deal>
                    ) {
                        Log.d("Main2Activity", response.body().toString())
                    }

                })
        }


        create_merchant.setOnClickListener {
            service.createMerchant(
                MerchantCreationRequest(
                    "danielefogna"+ Random.nextInt(0,100),
                    "123123123"+ Random.nextInt(0,100),
                    "31.43.13",
                    "via del cazzettino"
                )
            )
                .enqueue(object :
                    Callback<MerchantCreationResponse> {
                    override fun onFailure(call: Call<MerchantCreationResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "errore", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<MerchantCreationResponse>,
                        response: Response<MerchantCreationResponse>
                    ) {
                        Log.d("Main2Activity", response.body().toString())
                    }

                })
        }
    }


}
