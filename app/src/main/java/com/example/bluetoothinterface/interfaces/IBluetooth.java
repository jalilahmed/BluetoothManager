package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    Boolean checkBluetooth();
    String setupBluetooth();
    void disableBluetooth();
    ArrayList<BluetoothDevice> discoverDevices();
}
