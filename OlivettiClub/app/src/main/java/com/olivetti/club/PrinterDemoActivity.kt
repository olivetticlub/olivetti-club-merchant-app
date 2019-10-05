package com.olivetti.club

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.olivetti.club.enums.CouponType
import com.olivetti.club.utils.ConfigurationManager
import com.olivetti.club.utils.Utils
import it.jolmi.elaconnector.messages.enums.*
import it.jolmi.elaconnector.messages.Barcode
import it.jolmi.elaconnector.messages.ElaResponse
import it.jolmi.elaconnector.service.BroadcastValues
import it.jolmi.elaconnector.service.BroadcastValues.SOCKET_ACTION
import it.jolmi.elaconnector.service.BroadcastValues.SOCKET_STATUS
import it.jolmi.elaconnector.service.IElaResponseListener
import it.jolmi.elaconnector.service.printer.ElaPrinterLocalBinder
import it.jolmi.elaconnector.service.printer.IElaPrinter
import it.jolmi.elaconnector.work.ElaService
import kotlinx.android.synthetic.main.activity_printer_demo.*
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class PrinterDemoActivity : AppCompatActivity() {

    private val TAG = PrinterDemoActivity::class.java.simpleName
    private var mSocketConnected = false
    private var mElaConnectorServiceIntent: Intent? = null
    private var elaConnectorService: IElaPrinter? = null
    private var invokeElaConnectorServiceCallback: (IElaPrinter) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_demo)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mSocketBroadcast, IntentFilter(SOCKET_ACTION))
        printDemo.setOnClickListener(printCoupon())


        mElaConnectorServiceIntent = Intent(this@PrinterDemoActivity, ElaService::class.java)
        startService(mElaConnectorServiceIntent)
        bindService(mElaConnectorServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)

        connectPrinter.setOnClickListener {
            invokeElaConnectorService { service: IElaPrinter ->
                service.setHost("192.168.68.209")
                service.setPort(9100)
            }


            invokeElaConnectorService { service: IElaPrinter ->
                manageElaConnectorConnection(service.getHost(), service.getPort())
            }
        }


    }

    private fun manageElaConnectorConnection(host: String, port: Int) {
        invokeElaConnectorService {
            if (it.getConnectionStatus() == (ConnectionStatus.STATE_DISCONNECTED)) {
                it.connect(host, port)
            } else {
                it.disconnect()
            }
        }
    }

    private fun printCoupon(): (View) -> Unit {
        return {
            Log.d("demo print", "printing...")

            val configManager = ConfigurationManager.getInstance(this)

            invokeElaConnectorService {
                try {
                    if (CouponType.fromInt(configManager.couponType) == CouponType.BARCODE) {
                        //Print Barcode
                        val headerList = arrayListOf<String>()
                        headerList.add("")
                        headerList.add("")
                        headerList.add("hello")
                        headerList.add("world")
                        headerList.add("")

                        val footerList = arrayListOf<String>()
                        footerList.add("")
                        footerList.add("")
                        footerList.add("")
                        footerList.add("")
                        footerList.add("")

                        val barcodeString =
                            ConfigurationManager.getInstance(this)
                                .storeId /*3 Chars for StoreID*/
                                .plus(/*Special Char*/"-")
                                .plus(Random.nextInt())

                        val barcode = Barcode(
                            headerList,
                            footerList,
                            CodeType.COD_39,
                            barcodeString,
                            StationType.RICEVUTA
                        )

                        it.printCoupon(barcode)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    e.message?.let { message ->
                        Utils.showDialog(
                            this@PrinterDemoActivity,
                            ("errore stampa"),
                            message
                        )
                    }
                }
            }

        }
    }

    private var mSocketBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action?.equals(SOCKET_ACTION) == true) {
                    mSocketConnected = it.getBooleanExtra(SOCKET_STATUS, false)
                    val socketConnected: String = if (mSocketConnected) {
                        "CONNECTED"
                    } else {
                        "DISCONNECTED"
                    }
                    //Utils.showSnackBar(rootLayout, socketConnected)
                    Log.d(TAG, socketConnected)
                    invalidateOptionsMenu()
                }
            }
        }
    }

    private fun invokeElaConnectorService(invoke: (IElaPrinter) -> Unit) {
        if (elaConnectorService != null) {
            invoke(elaConnectorService!!)
        } else {
            invokeElaConnectorServiceCallback = invoke
            bindService(mElaConnectorServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val mServiceConnection = object : ServiceConnection, IElaResponseListener {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            elaConnectorService = (service as ElaPrinterLocalBinder).getService()
            invokeElaConnectorServiceCallback(elaConnectorService!!)
            invokeElaConnectorService { it.attachListener(this) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected $name")
            elaConnectorService = null
        }

        override fun onElaResponse(elaResponse: ElaResponse) {
            Log.d(TAG, "elaResponse --> $elaResponse")
            elaResponse.status?.let {
                if (it == Status.KO) {
                    //Only show popup when KO occurs
                    runOnUiThread {
                        Utils.showDialog(
                            this@PrinterDemoActivity,
                            elaResponse::class.java.simpleName,
                            elaResponse.toString()
                        )
                    }
                }
                //mResponseList.add(elaResponse)
            }
        }

        override fun onEmptyQueue() {
            Log.d(TAG, "onEmptyQueue")
            //mCanOpenMenu = true
            //mOperationsInterface.onOperationFinished()
        }

    }

    override fun onDestroy() {
        stopService(mElaConnectorServiceIntent)
        unbindService(mServiceConnection)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSocketBroadcast)
        super.onDestroy()
    }


}
