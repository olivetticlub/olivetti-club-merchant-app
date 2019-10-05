package com.olivetti.club

import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.olivetti.club.enums.CouponType
import com.olivetti.club.utils.ConfigurationManager
import it.jolmi.elaconnector.messages.Barcode
import it.jolmi.elaconnector.messages.ElaResponse
import it.jolmi.elaconnector.messages.enums.CodeType
import it.jolmi.elaconnector.messages.enums.StationType
import it.jolmi.elaconnector.messages.enums.Status
import it.jolmi.elaconnector.service.BroadcastValues.SOCKET_ACTION
import it.jolmi.elaconnector.service.BroadcastValues.SOCKET_STATUS
import it.jolmi.elaconnector.service.IElaResponseListener
import it.jolmi.elaconnector.service.printer.ElaPrinterLocalBinder
import it.jolmi.elaconnector.service.printer.IElaPrinter
import it.jolmi.elaconnector.work.ElaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random


@ExperimentalCoroutinesApi
class PrinterService(private val context: Context) {

    private val TAG = PrinterService::class.java.simpleName
    private var mSocketConnected = false
    private var mElaConnectorServiceIntent: Intent? = null
    private var elaConnectorService: IElaPrinter? = null
    private var invokeElaConnectorServiceCallback: (IElaPrinter) -> Unit = {}

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

                    Toast.makeText(context!!, socketConnected, Toast.LENGTH_LONG).show()
                    Log.d(TAG, socketConnected)
                }
            }
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
                    Log.d(TAG, elaResponse.toString())

                }
            }
        }

        override fun onEmptyQueue() {
            Log.d(TAG, "onEmptyQueue")
        }

    }

    init {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(mSocketBroadcast, IntentFilter(SOCKET_ACTION))

        mElaConnectorServiceIntent = Intent(context, ElaService::class.java)
        context.startService(mElaConnectorServiceIntent)
        context.bindService(
            mElaConnectorServiceIntent,
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }


    fun connectToPrinter() {
        invokeElaConnectorService { service: IElaPrinter ->
            service.connect("192.168.68.209", 9100)
        }
    }

    fun disconnectToPrinter() {
        elaDisconnect()
    }

    fun printCoupon(deal: Deal): (View) -> Unit {
        return {
            Log.d("demo print", "printing...")

            val configManager = ConfigurationManager.getInstance(context)

            invokeElaConnectorService {
                try {
                    if (CouponType.fromInt(configManager.couponType) == CouponType.BARCODE) {
                        //Print Barcode
                        val headerList = arrayListOf<String>()
                        headerList.add("")
                        headerList.add("")
                        headerList.add("You got a coupon:")
                        headerList.add("${deal.description}")
                        headerList.add("")

                        val footerList = arrayListOf<String>()
                        footerList.add("")
                        footerList.add("use this coupon at")
                        footerList.add("${deal.merchant_address}")
                        footerList.add("")
                        footerList.add("")

                        val barcodeString =
                            ConfigurationManager.getInstance(context)
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
                } finally {
                    elaDisconnect()
                }
            }

        }
    }

    private fun elaDisconnect() {
        invokeElaConnectorService {
            it.disconnect()
        }
    }


    private fun invokeElaConnectorService(invoke: (IElaPrinter) -> Unit) {
        if (elaConnectorService != null) {
            invoke(elaConnectorService!!)
        } else {
            invokeElaConnectorServiceCallback = invoke
            context.bindService(
                mElaConnectorServiceIntent,
                mServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }


    fun deinit() {
        context.stopService(mElaConnectorServiceIntent)
        context.unbindService(mServiceConnection)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mSocketBroadcast)
    }


}
