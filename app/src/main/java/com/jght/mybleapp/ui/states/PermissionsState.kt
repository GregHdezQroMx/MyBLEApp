package com.jght.mybleapp.ui.states

sealed class PermissionsState {
    object Unknown: PermissionsState()
    object Granted: PermissionsState()
    data class Denied(val permission: String): PermissionsState()
    data class PermanentlyDenied(val permission: String): PermissionsState()
}