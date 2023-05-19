package com.jght.mybleapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BleScanScreen(
    modifier: Modifier = Modifier,
    hasRequiredRuntimePermissions: Boolean = false,
    performScan: () -> Unit,
    isAndroidVersionCodeS: Boolean,
    onAcceptAlertLocationPermission: () -> Unit,
    onAcceptAlertBluetoothPermissions: () -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isClicked by remember { mutableStateOf(false) }

        BleScanScreen(
            onClick = { isClicked = true },
            isClicked = isClicked,
            hasRequiredRuntimePermissions = hasRequiredRuntimePermissions,
            performScan = performScan,
            isAndroidVersionCodeS = isAndroidVersionCodeS,
            onAcceptAlertLocationPermission = onAcceptAlertLocationPermission,
            onAcceptAlertBluetoothPermissions = onAcceptAlertBluetoothPermissions
        )
    }
}

@Composable
private fun BleScanScreen(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isClicked: Boolean,
    hasRequiredRuntimePermissions: Boolean = false,
    performScan: () -> Unit,
    isAndroidVersionCodeS: Boolean,
    onAcceptAlertLocationPermission: () -> Unit,
    onAcceptAlertBluetoothPermissions: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { onClick() }
        ) {
            Text(
                text = "Enable Bluetooth",
                modifier = modifier
            )
        }
        if (isClicked) {
            StartBleScan(
                hasRequiredRuntimePermissions = hasRequiredRuntimePermissions,
                performScan = performScan,
                isAndroidVersionCodeS = isAndroidVersionCodeS,
                onAcceptAlertLocationPermission = onAcceptAlertLocationPermission,
                onAcceptAlertBluetoothPermissions = onAcceptAlertBluetoothPermissions
            )
        }

    }
}


@Composable
private fun StartBleScan(
    hasRequiredRuntimePermissions: Boolean = false,
    performScan: () -> Unit,
    isAndroidVersionCodeS: Boolean,
    onAcceptAlertLocationPermission: () -> Unit,
    onAcceptAlertBluetoothPermissions: () -> Unit
) {
    if (!hasRequiredRuntimePermissions) {
        RequestRelevantRuntimePermissions(
            isAndroidVersionCodeS,
            onAcceptAlertLocationPermission,
            onAcceptAlertBluetoothPermissions
        )
    } else {
        /* TODO: Actually perform scan */
        performScan()
    }
}

@Composable
fun RequestRelevantRuntimePermissions(
    isAndroidVersionCodeS: Boolean,
    onAcceptAlertLocationPermission: () -> Unit,
    onAcceptAlertBluetoothPermissions: () -> Unit,
) {
    if (isAndroidVersionCodeS) {
        RequestBluetoothPermissions(onAcceptAlertBluetoothPermissions)
    } else {
        RequestLocationPermission(onAcceptAlertLocationPermission)
    }
}

@Composable
private fun RequestLocationPermission(onAccept: () -> Unit) {

    //runOnUiThread {
    RequestLocationPermissionAlert(
        onAccept = { onAccept() }
    )
    //}
}

@Composable
private fun RequestBluetoothPermissions(onAccept: () -> Unit) {

    RequestBluetoothPermissionsAlert(
        onAccept = { onAccept() }
    )
}

@Composable
private fun RequestLocationPermissionAlert(onAccept: () -> Unit) {

    var openDialog by remember { mutableStateOf(true) }

    if (openDialog) {
        Alert(title = "Location permission required",
            text = "Starting from Android M (6.0), the system requires apps to be granted " +
                    "location access in order to scan for BLE devices.",
            onDismiss = { openDialog = false },
            onAccept = {
                onAccept()
                openDialog = false
            }
        )
    }
}

@Composable
private fun RequestBluetoothPermissionsAlert(onAccept: () -> Unit) {

    var openDialog by remember { mutableStateOf(true) }

    if (openDialog) {
        Alert(title = "Bluetooth permissions required",
            text = "Starting from Android 12, the system requires apps to be granted " +
                    "Bluetooth access in order to scan for and connect to BLE devices.",
            onDismiss = { openDialog = false },
            onAccept = {
                onAccept()
                openDialog = false
            }
        )
    }

}

@Composable
private fun Alert(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onAccept() }) {
                Text(text = "Ok")
            }
        }
    )
}


