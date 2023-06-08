package com.jght.mybleapp.presentation.main.view

import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jght.mybleapp.presentation.main.BluetoothLe
import com.jght.mybleapp.presentation.main.viewmodel.MainViewModel
import com.jght.mybleapp.ui.main.MainScreen
import com.jght.mybleapp.ui.scan.BleScanScreen
import com.jght.mybleapp.ui.states.BluetoothState
import com.jght.mybleapp.ui.theme.MyBLEAppTheme
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val bluetoothLe = BluetoothLe(this)
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyBLEAppTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Manage permissions: set, establish, granted, not granted
                    //Request Bluetooth Enable: enabled, not enabled
                    //Scan Devices: set filter, scan, scan results
                    var onBluetoothResultLauncherLaunch by remember{ mutableStateOf(false) }

                    val bluetoothResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                        if (result.resultCode == RESULT_OK) {
                            viewModel.onBluetoothEnabled()
                        } else {
                            viewModel.onBluetoothDisabled()
                        }
                    }

                    val bluetoothState by viewModel.bluetoothStateFlow.collectAsState()

                    when (bluetoothState) {
                        is BluetoothState.Disabled -> {
                            onBluetoothResultLauncherLaunch = true
                        }

                        is BluetoothState.Enabled -> {
                            BleScanScreen(
                                modifier = Modifier,
                                onScanDevices = { scanDevices(bluetoothResultLauncher) }
                            )
                        }

                        else -> Unit
                    }

                    MainScreen(
                        modifier = Modifier,
                        onBluetoothResultLauncherLaunch = { onBluetoothResultLauncherLaunch = true},
                        onScanDevices = { scanDevices(bluetoothResultLauncher) }
                    )

                    if (onBluetoothResultLauncherLaunch) {
                        LaunchedEffect(Unit) {
                            onBleResultLauncherLaunch(resultLauncher = bluetoothResultLauncher)
                        }
                    }
                }
            }
        }
    }

    private fun onBleResultLauncherLaunch(resultLauncher: ActivityResultLauncher<Intent>) {


        bluetoothLe.promptEnableBluetooth(resultLauncher)
    }

    private fun scanDevices(resultLauncher: ActivityResultLauncher<Intent>) {
        if ( bluetoothLe.isAdapterEnabled().not()) {
            bluetoothLe.promptEnableBluetooth(resultLauncher)
        } else {
            val filters: ArrayList<ScanFilter> = ArrayList()
            val uuid = UUID.randomUUID()
            val filter: ScanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid(uuid)).build()
            filters.add(filter)
            val settings: ScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

            Log.d(TAG, "StartScan...")

            bluetoothLe.startScan(filters = filters, settings = settings)
        }
    }
}
