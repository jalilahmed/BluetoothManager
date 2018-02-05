package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Prashant on 22/01/2018.
 */

public interface DiscoveryCallback {
    void onDevice();
    void onFinish();
}
