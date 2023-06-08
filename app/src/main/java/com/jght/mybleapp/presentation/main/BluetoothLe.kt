package com.jght.mybleapp.presentation.main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import java.nio.charset.Charset

class BluetoothLe(context: Context) {
    private val TAG = BluetoothLe::class.java.simpleName

    private var contextBle: Context = context


    fun isBleSupported(): Boolean {
        return getManager() != null
                && contextBle.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    private fun getManager(): BluetoothManager? =
        contextBle.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private fun getAdapter(): BluetoothAdapter? = getManager()?.adapter

    fun isAdapterEnabled(): Boolean {
        return getAdapter()?.isEnabled?:false
    }
    fun promptEnableBluetooth(resultLauncher: ActivityResultLauncher<Intent>) {

            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
    }

    fun getScanner(): BluetoothLeScanner? =
        getManager()?.adapter?.bluetoothLeScanner

    fun isScannerEnabled(): Boolean? = getAdapter()?.isEnabled

    fun registerReceiver(receiver: BroadcastReceiver) {
        contextBle.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    //BLE State
    /*
    Treat STATE_ON or STATE_BLE_ON as on, others as off
    Check state aggressively
    Consider Blocking Activity
    Prompt using BluetpootAdapter.ACTION_REQUEST_ENABLE
     */

    //After checking that Bluetooth is on...
    //Scanning for Devices
    @SuppressLint("MissingPermission")
    fun startScan(
        filters: List<ScanFilter>?,  //Describe relevant peripheals
        settings: ScanSettings?     //Specify power profile
        /*callback: ScanCallback      //Process scan results*/
    ) {
        getScanner()?.startScan(filters, settings, scanCallback)
    }

    private val scanCallback = object:ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "onScanResult...")
            super.onScanResult(callbackType, result)

            if (result?.device == null || TextUtils.isEmpty(result.device?.name)
            ) return

            result.apply {

            }

            val builder = result.device?.name?.let { StringBuilder(it) }
            builder?.append("\n")?.append(
                result.scanRecord
                    ?.getServiceData(result.scanRecord!!.serviceUuids.get(0)),
                Charset.forName("UTF-8")
            )
            //Observed result
            Log.d(TAG, builder.toString())
        }

        override fun onBatchScanResults(results:List<ScanResult>?){
            Log.d(TAG, "Scanning...")
            super.onBatchScanResults(results)

            results?.forEach { scanResult ->
                val device = scanResult.device
                val rssi = scanResult.rssi
                val data = scanResult.scanRecord?.bytes
                // Perform any required operations with the scan data
                Log.d(TAG, "onBatchScanResults-  device: $device, rssi: $rssi, data: $data")
            }

        }

        override fun onScanFailed(errorCode: Int) {
            Log.d(TAG, "Discovery onScanFailed: $errorCode")
            super.onScanFailed(errorCode)
        }
    }
}


