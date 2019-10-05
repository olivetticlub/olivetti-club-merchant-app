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
            printerService.printCoupon()
        }

        connectPrinter.setOnClickListener {
            printerService.connectToPrinter()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        printerService.deinit()
    }

}
