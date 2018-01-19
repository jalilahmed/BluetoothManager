package com.example.bluetoothinterface.bluetooth_module;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.example.bluetoothinterface.interfaces.DiscoveryCallback;
import com.example.bluetoothinterface.interfaces.IBluetooth;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth {
    private static final String TAG = "BTManager";

    private Activity myMainActivity;
    private BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    private DiscoveryCallback discoveryCallback;

    /*
    * Default constructor for passing calling Activity class
    * params(Activity) - The activity class creating instance of BTManager
    * */
    public BTManager (Activity callingActivity) {
        this.myMainActivity = callingActivity;
        this.discoveryCallback = null;
    }

    /* Checks if bluetooth is already on */
    public Boolean isEnabled() {
        return myBluetooth.isEnabled();
    }

    /* Enable bluetooth on user request */
    public void enable() {
        if (myBluetooth != null) {
            try {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                myMainActivity.startActivityForResult(enableBtIntent, 1);
            } catch (Exception e) {
                // TODO: Handle the exception ??
                Log.d(TAG, e.toString());
            }
        }
    }

    /* Disable bluetooth on user request */
    public void disable(){
        if(myBluetooth != null) {
            if (isEnabled()) {
                myBluetooth.disable();
            }
        }
    }

    /* Get all paired devices */
    public ArrayList<String> getPairedDevices(){
        Set<BluetoothDevice> bondedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> pairedDevices = new ArrayList<>();

        for (BluetoothDevice device : bondedDevices) {
            pairedDevices.add(device.getName());
        }

        return pairedDevices;
    }

    private BroadcastReceiver discoverDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // Checking if any action is found by the IntentFilter
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                myMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discoveryCallback.onDevice(device);
                    }
                });
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                myMainActivity.unregisterReceiver(discoverDevicesReceiver);
            }
        }
    };

    public void discoverDevices() {

        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }

        //check BT permissions in manifest
        checkBTPermissions();

        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent.addAction(BluetoothDevice.ACTION_FOUND);
        discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        myMainActivity.registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
        myBluetooth.startDiscovery();
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback){
        this.discoveryCallback = discoveryCallback;
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
