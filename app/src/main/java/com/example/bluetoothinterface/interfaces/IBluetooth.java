package com.example.bluetoothinterface.interfaces;

import android.app.Activity;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    //Method Declarations
    Boolean checkBluetooth();
    String setupBluetooth(Activity someActivity);
    void disableBluetooth();
}
