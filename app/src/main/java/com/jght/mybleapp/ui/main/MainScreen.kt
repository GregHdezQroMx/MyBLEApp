package com.jght.mybleapp.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jght.mybleapp.ui.permissions.RequestPermissions
import com.jght.mybleapp.ui.permissions.PermissionDenied
import com.jght.mybleapp.ui.scan.BleScanScreen
import com.jght.mybleapp.ui.states.PermissionsState

@Composable
fun MainScreen(
    modifier: Modifier,
    onBluetoothResultLauncherLaunch: () -> Unit,
    onScanDevices: () -> Unit
) {

    ManagePermissions(
        modifier = modifier,
        onBluetoothResultLauncherLaunch = onBluetoothResultLauncherLaunch,
        onScanDevices = onScanDevices
    )
}

@Composable
fun ManagePermissions(
    modifier: Modifier,
    onBluetoothResultLauncherLaunch: () -> Unit,
    onScanDevices: () -> Unit
) {
    var permissionsState by remember { mutableStateOf<PermissionsState>(PermissionsState.Unknown) }

    RequestPermissions(
        onPermissionsGranted = { permissionsState = PermissionsState.Granted },
        onPermissionsDenied = { permissionsState = PermissionsState.Denied(it) },
        onPermissionsPermanentlyDenied = {
            permissionsState = PermissionsState.PermanentlyDenied(it)
        },
    )

    ValidatePermissionsState(
        modifier = modifier,
        permissionState = permissionsState,
        onBluetoothResultLauncherLaunch = onBluetoothResultLauncherLaunch,
        onScanDevices = onScanDevices
    )
}

@Composable
private fun ValidatePermissionsState(
    permissionState: PermissionsState,
    modifier: Modifier,
    onBluetoothResultLauncherLaunch: () -> Unit,
    onScanDevices: () -> Unit
) {

    when (permissionState) {
        is PermissionsState.Granted -> {

            PermissionsGranted(
                modifier = modifier,
                onScanDevices = { onScanDevices() },
            )
        }

        is PermissionsState.Denied -> {

            PermissionsDenied(
                permission = permissionState.permission,
                modifier = modifier,
                onBluetoothResultLauncherLaunch = onBluetoothResultLauncherLaunch,
                onScanDevices = { onScanDevices() },
            )
        }

        is PermissionsState.PermanentlyDenied -> {

            PermissionsPermanentlyDenied(
                permission = permissionState.permission,
                modifier = modifier
            )
        }

        else -> {}
    }
}

@Composable
private fun PermissionsGranted(
    modifier: Modifier,
    onScanDevices: () -> Unit,
) {
    BleScanScreen(
        modifier = modifier,
        onScanDevices = onScanDevices
    )
}

@Composable
private fun PermissionsDenied(
    permission: String,
    modifier: Modifier,
    onBluetoothResultLauncherLaunch: () -> Unit,
    onScanDevices: () -> Unit
) {

    PermissionDenied(
        permission = permission,
        modifier = modifier,
        permanentDenied = false
    )

    ManagePermissions(
        modifier = modifier,
        onBluetoothResultLauncherLaunch = onBluetoothResultLauncherLaunch,
        onScanDevices = onScanDevices
    )
}

@Composable
private fun PermissionsPermanentlyDenied(permission: String, modifier: Modifier) {

    PermissionDenied(
        permission = permission,
        modifier = modifier,
        permanentDenied = true
    )
}