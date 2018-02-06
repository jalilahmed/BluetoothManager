package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Prashant on 23/01/2018.
 */

public interface ICommunicationCallback {
    void onConnect(BluetoothDevice device);
    void onError(String message);
    void onConnectError(String message);
    void onDisconnect(String message);
    void onConnectionLost(BluetoothDevice device);
    void onStopReading(BluetoothDevice device);
}
