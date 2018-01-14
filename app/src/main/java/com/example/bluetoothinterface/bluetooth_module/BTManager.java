package com.example.bluetoothinterface.bluetooth_module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.bluetoothinterface.MainActivity;
import com.example.bluetoothinterface.interfaces.IBluetooth;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager extends Activity implements IBluetooth {

    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onCreate() {

        MainActivity myActivity = new MainActivity();
        myActivity.setMyInterface(this);
    }

    @Override
    public String setupBluetooth() {
        // Turn on Bluetooth of Device Here
        if (myBluetooth == null) {
            return "Device has no Bluetooth";
        }
        try {
            if (!myBluetooth.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //TODO: The intent is trying to access a null object.
                startActivityForResult(enableBtIntent, 1);
            }
        } catch (Exception e) {
            return e.toString();
        }

        return "in setupBluetooth()";
    }
}
