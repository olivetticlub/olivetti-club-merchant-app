package com.olivetti.club

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_printer_demo.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class PrinterDemoActivity : AppCompatActivity() {

    lateinit var printerService: PrinterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_demo)

        printerService = PrinterService(this)

        printDemo.setOnClickListener {
            printerService.printCoupon(
                Deal(
                    "mercanName",
                    "descripion",
                    "via ciao ciao, 1",
                    "2",
                    "4",
                    1
                )
            )
        }

        connectButton.setOnClickListener {
            printerService.connectToPrinter()
        }

        disconnectButton.setOnClickListener {
            printerService.disconnectToPrinter()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        printerService.deinit()
    }

}
