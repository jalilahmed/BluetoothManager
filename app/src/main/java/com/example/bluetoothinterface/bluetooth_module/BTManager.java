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

public class BTManager implements IBluetooth, Cloneable {
    private static final String TAG = "BTManager";
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Declarations
//    private BluetoothSocket mySocket;
//    private BluetoothDevice myDevice;
    private DiscoveryCallback discoveryCallback;

    //Definitions
    private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> allBluetoothDevices = new ArrayList<>();

    /*
    * Default constructor for passing calling Activity class
    * */
    //Creating BTManger as Singleton
    private BTManager () {}

    public static BTManager getInstance () {
        return Holder.INSTANCE;
    }

    // Singleton Holder Idiom
    private static class Holder{
        static final BTManager INSTANCE = new BTManager();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /* Functions */
    /* Checks if bluetooth is already on */
    public Boolean isEnabled() {
        return myBluetoothAdapter.isEnabled();
    }

    /* Enable bluetooth on user request */
    public void enable(Activity someActivity) {
        if (myBluetoothAdapter != null) {
            try {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                someActivity.startActivityForResult(enableBtIntent, 1);
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
    private BroadcastReceiver discoverDevicesReceiver;

    public void discoverDevices(final Activity someActivity) {
        discoverDevicesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null) {

                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    allBluetoothDevices.add(device);

                    if (discoveryCallback != null) {
                        someActivity.runOnUiThread(new Runnable() {
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
                        someActivity.runOnUiThread(new Runnable() {
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

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }

        //check BT permissions in manifest
        checkBTPermissions(someActivity);

        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent.addAction(BluetoothDevice.ACTION_FOUND);
        discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        someActivity.registerReceiver(discoverDevicesReceiver, discoverDevicesIntent);
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
        System.out.println("In BTManager::connectByDevice: " + device.getName());
        new ConnectThread(device).start();
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCallback = null;
    }

    private void checkBTPermissions(Activity someActivity) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = someActivity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += someActivity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                someActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice myDevice;
        private BluetoothSocket mySocket;
        private final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        ConnectThread(BluetoothDevice device) {
            System.out.println("Connect Thread class called: " + device.getAddress());
            myDevice = device;
            BluetoothSocket tmp = null;
            try {
                System.out.println("trying to open socket");
                tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
            } catch (IOException e) {
                System.out.println("Catch exception " + e.toString());
            }
            mySocket = tmp;
        }

        public void run() {
            myBluetoothAdapter.cancelDiscovery();
            try {
                mySocket.connect();
                System.out.println("Connected to socket without errors");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    System.out.println("trying fallback...");
                    mySocket = (BluetoothSocket) myDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(myDevice,1);
                    mySocket.connect();
                    System.out.println("Connected");
                }
                catch (Exception e2) {
                    System.out.println("Couldn't establish Bluetooth connection!");
                }
            }
        }
    }
}
