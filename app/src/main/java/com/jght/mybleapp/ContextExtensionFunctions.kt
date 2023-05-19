package com.jght.mybleapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.hasPermission(permissionType: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permissionType) ==
            PackageManager.PERMISSION_GRANTED
}
fun Context.hasRequiredRuntimePermissions(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        hasPermission( android.Manifest.permission.BLUETOOTH_SCAN) &&
                hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}