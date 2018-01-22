package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    Boolean isEnabled();
    void enable();
    void disable();
    List<BluetoothDevice> getPairedDevices();
    void discoverDevices();
//    void connectByName(String bluetoothName);
    void connectByDevice(BluetoothDevice device);
}
