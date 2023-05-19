package com.jght.mybleapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.jght.mybleapp.ui.BleScanScreen
import com.jght.mybleapp.ui.theme.MyBLEAppTheme

class MainActivity : ComponentActivity() {

    private val RUNTIME_PERMISSION_REQUEST_CODE = 2

    @RequiresApi(Build.VERSION_CODES.S)
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            promptEnableBluetooth()
        }

    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyBLEAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BleScanScreen(
                        modifier = Modifier,
                        hasRequiredRuntimePermissions = this.hasRequiredRuntimePermissions(),
                        performScan = { /*TODO()*/ },
                        isAndroidVersionCodeS = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S),
                        onAcceptAlertLocationPermission = {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                RUNTIME_PERMISSION_REQUEST_CODE
                            )
                        },
                        onAcceptAlertBluetoothPermissions = {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    Manifest.permission.BLUETOOTH_SCAN,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ),
                                RUNTIME_PERMISSION_REQUEST_CODE
                            )
                        }
                    )
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (/*ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED*/
                !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
            ) {
                responseLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                /*ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    RUNTIME_PERMISSION_REQUEST_CODE
                )
                return*/
            } else {
                //responseLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //todo deprecated
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RUNTIME_PERMISSION_REQUEST_CODE -> {
                val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
                    it.second == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
                }
                val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                when {
                    containsPermanentDenial -> {
                        // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
                        // Note: The user will need to navigate to App Settings and manually grant
                        // permissions that were permanently denied
                    }

                    containsDenial -> {
                        //TODO FIX
                        //RequestRelevantRuntimePermissions()
                    }

                    allGranted && hasRequiredRuntimePermissions() -> {
                        //TODO FIX
                        //StartBleScan()
                    }

                    else -> {
                        // Unexpected scenario encountered when handling permissions
                        recreate()
                    }
                }
            }
        }
    }
}

