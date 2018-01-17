package com.example.bluetoothinterface.bluetooth_module;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Build;
import android.util.Log;

import com.example.bluetoothinterface.interfaces.IBluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth {
    private static final String TAG = "BTManager";

    private Activity myMainActivity;
    private BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> foundBTDevices = new ArrayList<>();

    private BroadcastReceiver discoverDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // Checking if any action is found by the IntentFilter
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundBTDevices.add(device);
                Log.d(TAG, "discoverDevicesReceiver :: onReceive" + device.getName() + ", " + device.getAddress());
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                myMainActivity.unregisterReceiver(discoverDevicesReceiver);
            }
        }
    };

    /*
    * Default constructor for passing calling Activity class
    * params(Activity) - The activity class creating instance of BTManager
    * */
    public BTManager (Activity callingActivity) {
        super();
        this.myMainActivity = callingActivity;
        Log.d(TAG, "Creating an instance of BTManager");
    }

    /* Checks if bluetooth is already on */
    public Boolean checkBluetooth () {
        Log.d(TAG, "Checking if bluetooth is already on");
        return myBluetooth.isEnabled();
    }

    /* Initialize bluetooth on user request */
    public String setupBluetooth() {
        Log.i(TAG, "setupBluetooth :: started");
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

    public ArrayList<BluetoothDevice> discoverDevices() {
        Log.i(TAG, "discoverDevices :: started");

        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }

        Log.d(TAG, "discoverDevices :: checking permission");
        //check BT permissions in manifest
        checkBTPermissions();

        Log.d(TAG, "discoverDevices :: starting Discovery");
        myBluetooth.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent .addAction(BluetoothDevice.ACTION_FOUND);
        discoverDevicesIntent .addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        myMainActivity.registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);

        return foundBTDevices;
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = myMainActivity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += myMainActivity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                myMainActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
