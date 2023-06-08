package com.jght.mybleapp.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import com.jght.mybleapp.ui.states.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel: ViewModel() {


    private val _bluetoothStateFlow = MutableStateFlow<BluetoothState>(BluetoothState.Empty)
    val bluetoothStateFlow: StateFlow<BluetoothState> = _bluetoothStateFlow

    fun onBluetoothEnabled() {
        _bluetoothStateFlow.value = BluetoothState.Enabled
    }

    fun onBluetoothDisabled() {
        _bluetoothStateFlow.value = BluetoothState.Disabled
    }
}