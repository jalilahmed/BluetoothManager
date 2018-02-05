package com.example.bluetoothinterface.bluetooth_module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IDiscoveryCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth, Cloneable {

    // Bluetooth declarations
    private BroadcastReceiver discoverDevicesReceiver;
    private ArrayList<BluetoothDevice> miPods = new ArrayList<>();
    private ArrayList<BluetoothSocket> bluetoothSockets = new ArrayList<>();
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private DataHolder dataStore = DataHolder.getInstance();

    // Callback Interface Declarations
    private IDiscoveryCallback discoveryCB;
    private ICommunicationCallback communicationCB;

    // Definitions
    private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /* Creating BTManger as Singleton */
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

    /* Checks if bluetooth is already on */
    public boolean isEnabled() {
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
                System.out.println(e.toString());
            }
        }
    }

    /* Get all paired devices */
    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> bondedDevices = new ArrayList<>();
        bondedDevices.addAll(myBluetoothAdapter.getBondedDevices());
        return bondedDevices;
    }

    public void discoverDevices(final Activity someActivity) {

        discoverDevicesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action != null) {

                    switch (action) {
                        case BluetoothDevice.ACTION_FOUND:
                            final BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                            System.out.println("BTManager :: Found a device " + device.getName());

                            if (discoveryCB != null) {
                                someActivity.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        discoveryCB.onDevice( device );
                                    }
                                } );
                            }
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            context.unregisterReceiver( discoverDevicesReceiver );
                            System.out.println("BTManager :: Discovery finished ");

                            if (discoveryCB != null) {
                                someActivity.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        discoveryCB.onFinish();
                                    }
                                } );
                            }
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            final int state = intent.getIntExtra( BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR );

                            if (state == BluetoothAdapter.STATE_OFF) {
                                if (discoveryCB != null) {
                                    someActivity.runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            discoveryCB.onError( "Bluetooth switched off" );
                                        }
                                    } );
                                }
                            }
                            break;
                    }
                }
            }
        };

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }

        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent.addAction( BluetoothDevice.ACTION_FOUND );
        discoverDevicesIntent.addAction( BluetoothAdapter.ACTION_STATE_CHANGED );
        discoverDevicesIntent.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );

        someActivity.registerReceiver( discoverDevicesReceiver, discoverDevicesIntent );
        myBluetoothAdapter.startDiscovery();
    }

    public void connectToMiPods(ArrayList<BluetoothDevice> miPodsDevices, Activity someActivity) {

        myBluetoothAdapter.cancelDiscovery();
        miPods.addAll(miPodsDevices);

        for (final BluetoothDevice device : miPods) {

            try {
                if (device != null) {
                    // TODO: Get different UUIDs for sensors
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID_SPP);
                    bluetoothSockets.add( socket );
                }
            } catch (final Exception e) {
                someActivity.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        communicationCB.onError( e.getMessage() );
                    }
                } );
            }
        }

        for (final BluetoothDevice device : miPods) {
            try {
                BluetoothSocket socket = findSocket(device.getName());
                socket.connect();

                // Callback for successful connection
                if (communicationCB != null) {

                    someActivity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onConnect( device );
                        }
                    } );
                }
            } catch (Exception e) {
                final String errorMessage = e.toString();
                if (communicationCB != null) {
                    someActivity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onConnectError(errorMessage);
                        }
                    });
                }
            }
        }
    }

    public void setDiscoveryCB(IDiscoveryCallback discoveryCB) {
        this.discoveryCB = discoveryCB;
    }

    public void setCommunicationCB(ICommunicationCallback communicationCB) {
        this.communicationCB = communicationCB;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCB = null;
    }

    public void removeCommunicationCallback() {
        this.communicationCB = null;
    }

    private BluetoothSocket findSocket(String sensor) {
        for(BluetoothSocket socket : bluetoothSockets) {
            if (socket.getRemoteDevice().getName().toUpperCase().equals(sensor.toUpperCase()) ||
                    socket.getRemoteDevice().getAddress().toUpperCase().equals(sensor.toUpperCase())) {
                return socket;
            }
        }
        return null;
    }
}
