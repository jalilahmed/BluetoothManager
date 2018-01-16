package com.example.bluetoothinterface.interfaces;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    Boolean checkBluetooth();
    String setupBluetooth();
    void disableBluetooth();
    void discoverDevices();
}
