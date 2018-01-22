package com.example.bluetoothinterface.interfaces;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    void setDiscoveryCallback(DiscoveryCallback discoveryCallback);
    void removeDiscoveryCallback();
    Boolean isEnabled();
    void enable(Activity someActivity);
    void disable();
    List<BluetoothDevice> getPairedDevices();
    void discoverDevices(Activity someActivity);
//    void connectByName(String bluetoothName);
    void connectByDevice(BluetoothDevice device);
}
