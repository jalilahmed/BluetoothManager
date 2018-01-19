package com.example.bluetoothinterface.interfaces;

import java.util.ArrayList;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    Boolean isEnabled();
    void enable();
    void disable();
    ArrayList<String> getPairedDevices();
    void discoverDevices();
}
