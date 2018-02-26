package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Prashant on 2/26/2018.
 */

public interface IQualityCheckCallback {
    void onFramesLost(int framesLost, BluetoothDevice device);
}
