package com.jght.mybleapp.ui.states

sealed class BluetoothState {
    object Empty: BluetoothState()
    object Enabled: BluetoothState()
    object Disabled: BluetoothState()
}