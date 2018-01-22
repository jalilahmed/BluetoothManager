package com.example.bluetoothinterface.bluetooth_module;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.bluetoothinterface.interfaces.DiscoveryCallback;
import com.example.bluetoothinterface.interfaces.IBluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth {
    private static final String TAG = "BTManager";

    private Activity myMainActivity;
    private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket mySocket;
    private BluetoothDevice myDevice;
    private DiscoveryCallback discoveryCallback;
    private ArrayList<BluetoothDevice> allBluetoothDevices = new ArrayList<>();

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
        return myBluetoothAdapter.isEnabled();
    }

    /* Enable bluetooth on user request */
    public void enable() {
        if (myBluetoothAdapter != null) {
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
        if(myBluetoothAdapter != null) {
            if (isEnabled()) {
                myBluetoothAdapter.disable();
            }
        }
    }

    /* Get all paired devices */
    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> bondedDevices = new ArrayList<>();
        bondedDevices.addAll(myBluetoothAdapter.getBondedDevices());
        allBluetoothDevices.addAll(myBluetoothAdapter.getBondedDevices());

        return bondedDevices;
    }

    /*
    * This receiver is used for discovering bluetooth devices,
    * It provides the ability to set a callback for different actions by invoking setDiscoveryCallback()
    * Cases used,
    * Found a device - onDevice(device) callback to get the found bluetooth device
    * Discovery end  - unregister this broadcast receiver
    * */
    private BroadcastReceiver discoverDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null) {

                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    allBluetoothDevices.add(device);

                    if (discoveryCallback != null) {
                        myMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDevice(device);
                            }
                        });
                    }
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    context.unregisterReceiver(discoverDevicesReceiver);
                    if (discoveryCallback != null) {
                        myMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onFinish();
                            }
                        });
                    }
                }
            }
        }
    };

    public void discoverDevices() {

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }

        //check BT permissions in manifest
        checkBTPermissions();

        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent.addAction(BluetoothDevice.ACTION_FOUND);
        discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        myMainActivity.registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
        myBluetoothAdapter.startDiscovery();
    }

//    public void connectByName(String bluetoothName) {
//        for (BluetoothDevice device : allBluetoothDevices) {
//            if (device.getName().equals(bluetoothName)) {
//                Log.d(TAG, "Found the device in the list, " + device.getName());
//                connectByDevice(device);
//                return;
//            }
//        }
//    }

    public void connectByDevice(BluetoothDevice device) {
        new ConnectThread(device).start();
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCallback = null;
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

    private class ConnectThread extends Thread {

        ConnectThread(BluetoothDevice device) {
            Log.d(TAG,"Connect Thread class called");
            BTManager.this.myDevice = device;
            ParcelUuid deviceUUIDS[] = device.getUuids();
            String uuid = deviceUUIDS[0].toString();
            try {
                BTManager.this.mySocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
            } catch (IOException e) {
                // TODO
                Log.d(TAG, "Catch exception " + e.toString());
            }
        }

        public void run() {
            myBluetoothAdapter.cancelDiscovery();

            try {
                mySocket.connect();
                Log.d(TAG, "Connected to socket without errors");

//                if (discoveryCallback !=null) {
//                    myMainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() { discoveryCallback.onConnect(myDevice); }
//                    });
//                }
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mySocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }
        }
    }
}
