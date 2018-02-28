package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Prashant on 2/26/2018.
 */

public interface IQualityCheckCallback {
    void framesLost(int framesLost, BluetoothDevice device);
    void framesLostPercentage(float percentage, BluetoothDevice device);
}
