package com.example.bluetoothinterface.bluetooth_module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;

import com.example.bluetoothinterface.DataHolder;
import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IDataHolder;
import com.example.bluetoothinterface.interfaces.IDiscoveryCallback;
import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by jalil on 1/12/2018.
 */

public class BTManager implements IBluetooth, Cloneable {
    private enum BT_STATES {
        ON, OFF, DISCOVERING
    }

    // Bluetooth declarations
    private BT_STATES STATE = BT_STATES.OFF;
    private BroadcastReceiver discoverDevicesReceiver;
    private ArrayList<BluetoothDevice> miPods = new ArrayList<>();
    private ArrayList<ISensor> sensorList = new ArrayList<>();
    private ArrayList<BluetoothSocket> bluetoothSockets = new ArrayList<>();
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private IDataHolder dataStore = DataHolder.getInstance();

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
                STATE = BT_STATES.ON;
            } catch (Exception e) {
                // TODO: Handle the exception ??
                System.out.println(e.toString());
            }
        }
    }

    /* Get all paired devices */
    public List<String> getPairedDevices(){
        Set<BluetoothDevice> temp = myBluetoothAdapter.getBondedDevices();
        if (temp.size()  > 0 ) {
            for (BluetoothDevice device : temp) {
                if (device.getName().contains("miPod3")) {
                    dataStore.setAvailableDevices(device.getName());
                    miPods.add(device);
                }
            }
        }
        return dataStore.getAvailableDevices();
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
                            //System.out.println("BTManager :: Found a device " + device.getName());

                            if (discoveryCB != null && device.getName() != null && device.getName().contains("miPod3")) {
                                someActivity.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        miPods.add(device);
                                        dataStore.setAvailableDevices(device.getName());
                                        discoveryCB.onDevice();
                                    }
                                });
                            }
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            STATE = BT_STATES.ON;
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
        if (STATE != BT_STATES.OFF) {
            STATE = BT_STATES.DISCOVERING;
            myBluetoothAdapter.startDiscovery();
        }

    }

    public void connectToMiPods(ArrayList<String> miPodsDevicesNames, Activity someActivity) {
        myBluetoothAdapter.cancelDiscovery();

        setISensorList(miPodsDevicesNames);

        createSockets(someActivity);

        connectISensors(someActivity);

        startRead();

    }

    private void setISensorList(ArrayList<String> miPodsDevicesNames) {
        for (BluetoothDevice device : miPods) {
            if (miPodsDevicesNames.contains( device.getName() )) {
                //listSensorToConnect.add(device);
                ISensor sensor = new Sensor( device, "left" );
                sensorList.add(sensor);
            }
        }
    }

    private void createSockets(Activity activity){
        for (final ISensor sensor : sensorList) {
            BluetoothDevice device = sensor.getDevice();
            try {
                if (device != null) {
                    // TODO: Get different UUIDs for sensors
                    ParcelUuid[] uuids = device.getUuids();
                    String uuid = uuids[0].toString();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                    bluetoothSockets.add( socket );
                }
            } catch (final Exception e) {
                activity.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        communicationCB.onError( e.getMessage() );
                    }
                } );
            }
        }
    }

    private void connectISensors(Activity activity) {
        for (final ISensor sensor : sensorList) {
            final BluetoothDevice device = sensor.getDevice();
            try {
                BluetoothSocket socket = findSocket(device.getName());
                socket.connect();
                sensor.setState(SENSOR_STATE.CONNECTED);

                // Callback for successful connection
                if (communicationCB != null) {

                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onConnect( device );
                        }
                    } );
                }
            } catch (Exception e) {
                final String errorMessage = e.toString();
                if (communicationCB != null) {
                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onConnectError(errorMessage);
                            sensor.setState(SENSOR_STATE.NOT_CONNECTED);
                        }
                    });
                }
            }
        }
    }

    private void startRead() {
        // List<ISensor> from sensorList
        for (ISensor sensor: sensorList) {
            //Create a thread and start reading
            BluetoothSocket mySocket = findSocket(sensor.getName());
            ReadStream thread = new ReadStream(sensor, mySocket);
            if (sensor.getState() == SENSOR_STATE.CONNECTED) {
                thread.start();
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

    public void test() {
        sensorList.get(0).setState(SENSOR_STATE.CONNECTED);
        sensorList.get(1).setState(SENSOR_STATE.CONNECTED);
    }

}
