@file:OptIn(ExperimentalPermissionsApi::class)

package com.jght.mybleapp.ui.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

private val permissionsToRequestBeforeOreo = listOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@SuppressLint("InlinedApi")
private val permissionsToRequestAfterOreo = listOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT
)

@Composable
fun RequestPermissions(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: (String) -> Unit,
    onPermissionsPermanentlyDenied: (String) -> Unit
) {

    val permissionStates = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequestAfterOreo
        } else {
            permissionsToRequestBeforeOreo
        },
        onPermissionsResult = { permissions ->
            permissions.forEach { permission ->
                println("${permission.key} = ${permission.value}")
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionStates.launchMultiplePermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    ValidatePermissions(
        permissionStates = permissionStates,
        onPermissionsGranted = onPermissionsGranted,
        onPermissionsDenied = onPermissionsDenied,
        onPermissionsPermanentlyDenied = onPermissionsPermanentlyDenied
    )
}

@Composable
fun ValidatePermissions(
    permissionStates: MultiplePermissionsState,
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: (String) -> Unit,
    onPermissionsPermanentlyDenied: (String) -> Unit
) {

    permissionStates.permissions.forEach { permissions ->
        println("permissionStates after request - ${permissions.permission} = ${permissions.status.isGranted}")
        when {

            permissions.status.isGranted -> {

                onPermissionsGranted()
            }

            permissions.status.shouldShowRationale -> {

                LaunchedEffect(Unit) {
                    permissionStates.launchMultiplePermissionRequest()
                }
                onPermissionsDenied(permissions.permission)
            }

            !permissions.status.isGranted && !permissions.status.shouldShowRationale -> {

                /* If the permission is denied and the should not show rationale
                    You can only allow the permission manually through app settings
                 */
                onPermissionsPermanentlyDenied(permissions.permission)

            }
        }
    }
}


