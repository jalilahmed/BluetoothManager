package com.example.bluetoothinterface.bluetooth_module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.example.bluetoothinterface.interfaces.IBluetooth;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth {

    private Activity myMainActivity;
    private BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();

    /*
    * Default constructor for passing calling Activity class
    * params(Activity) - The activity class creating instance of BTManager
    * */
    public BTManager (Activity callingActivity) {
        super();
        this.myMainActivity = callingActivity;
    }

    /* Checks if bluetooth is already on */
    public Boolean checkBluetooth () {

        return myBluetooth.isEnabled();
    }

    /* Initialize bluetooth on user request */
    public String setupBluetooth(Activity someActivity) {

        // Turn on Bluetooth of Device Here
        if (myBluetooth == null) {
            return "Device has no Bluetooth";
        }
        try {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            myMainActivity.startActivityForResult(enableBtIntent, 1);
        } catch (Exception e) {
            return e.toString();
        }

        return "Bluetooth enabled";
    }

    /* Disable bluetooth on user request */
    public void disableBluetooth() {

        try {
            if (myBluetooth.isEnabled()) {
                myBluetooth.disable();
            }
        } catch (Exception e) {
            // TODO: Handle the exception ??
        }

    }
}
