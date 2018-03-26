package com.example.bluetoothinterface.bluetooth_module;

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
import com.example.bluetoothinterface.interfaces.IQualityCheckCallback;
import com.example.bluetoothinterface.interfaces.ISensor;
import com.example.bluetoothinterface.interfaces.IUICallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by jalil and prashant on 1/12/2018.
 */

class BTManager implements IBluetooth, Cloneable {
    private enum BT_STATES {
        ON, OFF, DISCOVERING
    }

    // Bluetooth declarations
    private BT_STATES STATE = BT_STATES.OFF;
    private BroadcastReceiver discoverDevicesReceiver;
    private ArrayList<BluetoothDevice> miPods = new ArrayList<>();
    private ArrayList<ISensor> sensorList = new ArrayList<>();
    private ArrayList<BluetoothSocket> bluetoothSockets = new ArrayList<>();
    private IDataHolder dataStore = DataHolder.getInstance();

    // Callback Interface Declarations
    private IDiscoveryCallback discoveryCB;
    private ICommunicationCallback communicationCB;
    private IUICallback UICallback;
    private IQualityCheckCallback qualityCheckCB;


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
        if (myBluetoothAdapter.isEnabled()){
            STATE = BT_STATES.ON;
        }
        return myBluetoothAdapter.isEnabled();
    }

    /* Enable bluetooth on user request */
    public void enable() {
        if (myBluetoothAdapter != null) {
            if (!isEnabled()) {
                myBluetoothAdapter.enable();
                STATE = BT_STATES.ON;
            }
        }
    }

    public void disable() {
        if (myBluetoothAdapter != null) {
            if (isEnabled()) {
                myBluetoothAdapter.disable();
                STATE = BT_STATES.OFF;
            }
        }
    }

    /* Set all paired devices in dataStore */
    public void setPairedDevices() throws Exception{
        Set<BluetoothDevice> temp = myBluetoothAdapter.getBondedDevices();
        if (temp.size() == 0) {
            throw new Exception("There are no paired sensors");
        }
        System.out.println("BTManager :: setPairedDevices :: bonded devices are : " + temp);
        if (temp.size()  > 0 ) {
            for (BluetoothDevice device : temp) {
                if (device.getName().contains("miPod3") && !miPods.contains(device)) {
                    dataStore.setAvailableSensors(device.getName());
                    miPods.add(device);
                }
            }
        }
    }

    public void discoverDevices() {
        discoverDevicesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action != null) {

                    switch (action) {
                        case BluetoothDevice.ACTION_FOUND:
                            final BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                            System.out.println("BTManager :: Found a device " + device.getName());

                            if (device.getName() != null && device.getName().contains("miPod3")) {
                                miPods.add(device);
                                dataStore.setAvailableSensors(device.getName());
                                discoveryCB.onDevice();
                            }
                            break;
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            STATE = BT_STATES.ON;
                            context.unregisterReceiver( discoverDevicesReceiver );
                            System.out.println("BTManager :: Discovery finished ");
                            discoveryCB.onFinish();
                            break;
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            final int state = intent.getIntExtra( BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR );

                            if (state == BluetoothAdapter.STATE_OFF) {
                                discoveryCB.onError( "Bluetooth switched off" );
                            }
                            break;
                    }
                }
            }
        };
        System.out.println("discoverDevices Started!!");

//        if (myBluetoothAdapter.isDiscovering()) {
//            myBluetoothAdapter.cancelDiscovery();
//        }

        IntentFilter discoverDevicesIntent = new IntentFilter();
        discoverDevicesIntent.addAction( BluetoothDevice.ACTION_FOUND );
        discoverDevicesIntent.addAction( BluetoothAdapter.ACTION_STATE_CHANGED );
        discoverDevicesIntent.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );

        UICallback.registerDiscoveryReceiver(discoverDevicesIntent, discoverDevicesReceiver);

        if (STATE == BT_STATES.ON) {
            STATE = BT_STATES.DISCOVERING;
            myBluetoothAdapter.startDiscovery();
        }
    }

    public void stopDiscoverDevices() {
        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }
    }

    public void connectToMiPod(String miPodSensorName) {
        myBluetoothAdapter.cancelDiscovery();

        ISensor mySensor = setISensorList(miPodSensorName);

        createSocket(mySensor);

        connectISensor(mySensor);
    }

    private ISensor setISensorList(String miPodSensorName) {
        System.out.println("setISensorList :: miPods array : " + miPods);
        ISensor sensor = null;
        for (BluetoothDevice device : miPods) {
            if (miPodSensorName.equals(device.getName())) {
                sensor = new MiPodSensor( device, "left");
                sensorList.add(sensor);
                dataStore.setISensor(sensor);
            }
        }
        return sensor;
    }

    private void createSocket(final ISensor sensor){

        System.out.println("in BTManager::createSockets sensorList is:  " + sensorList);
        for (ISensor sensor_ : sensorList) {
            System.out.println("Individual sensor in sensorList :: name : " + sensor_.getName());
        }
        System.out.println("in BTManager:: createSockets sensor is : " + sensor);
        final BluetoothDevice device = sensor.getDevice();

        try {
            if (device != null) {
                ParcelUuid[] uuids = device.getUuids();
                String uuid = uuids[0].toString();
                final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                bluetoothSockets.add(socket);
                System.out.println("in BTManager: bluetoothSockets are: " + bluetoothSockets.toString());
            }
        } catch (final Exception e) {
            communicationCB.onError(e.getMessage());
        }
    }

    public ArrayList<BluetoothSocket> getSockets() {
        return bluetoothSockets;
    }

    private void connectISensor(ISensor sensor) {
        BluetoothDevice device = sensor.getDevice();

        BluetoothSocket socket = null;
        try {
            socket = findSocket(device.getName());
            setOnConnectionLostHandler(socket, sensor);
            if (socket != null) {
                socket.connect();
                sensor.setState(SENSOR_STATE.CONNECTED);

                startReading(sensor);
                sensor.getState();

                // Callback for successful connection
                if (communicationCB != null) {
                    communicationCB.onConnect(device);
                }
            }
        } catch (Exception e) {
            sensor.setState(SENSOR_STATE.NOT_CONNECTED);
            try {
                if (socket != null) {
                    socket.close();
                    bluetoothSockets.remove(socket);
                    sensorList.remove(sensor);
                    dataStore.removeISensor(sensor);
                }
            } catch (IOException exception) {
                System.out.println("in BTManager::connectISensors could not close socket");
            }
            final String errorMessage = e.toString();
            if (communicationCB != null) {
                communicationCB.onConnectError(errorMessage);
            }
        }
    }

    private void startReading(ISensor sensor) throws Exception {
        try {
            //Create a thread and start reading
            BluetoothSocket mySocket = findSocket(sensor.getName());
            sensor.startReadISensor(mySocket);
        } catch (Exception e) {
            throw new Exception(e.toString()); // Exception coming from MiPodSensor startReadISensor()
        }
    }

    public void setDiscoveryCB(IDiscoveryCallback inputDiscoveryCB) {
        discoveryCB = inputDiscoveryCB;
    }

    public void setCommunicationCB(ICommunicationCallback communicationCB) {
        this.communicationCB = communicationCB;
    }

    public ICommunicationCallback getCommunicationCB() {
        return this.communicationCB;
    }

    public void setQualityCheckCB(IQualityCheckCallback qualityCB) {
        this.qualityCheckCB = qualityCB;
    }

    public IQualityCheckCallback getQualityCheckCB() {
        return qualityCheckCB;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCB = null;
    }

    public void removeCommunicationCallback() {
        this.communicationCB = null;
    }

    public void removeQualityCheckCallback() {
        this.qualityCheckCB = null;
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

    public void stopReading(ISensor sensor) {
        sensor.setState(SENSOR_STATE.CONNECTED);
        sensor.setCanRead(false);
    }

    public void closeSocketAndStream(BluetoothSocket socket, ISensor sensor){
        System.out.println("Call came in BTManager::closeSocket.");
        try {
            sensor.setState(SENSOR_STATE.NOT_CONNECTED);
            socket.close();
            bluetoothSockets.remove(socket);
            sensorList.remove(sensor);
            dataStore.removeISensor(sensor);
        } catch(IOException e){
            System.out.println("Exception occurred in BTManager::closeSockets while closing socket: " + socket.toString());
        }
        System.out.println("BTManager::closeSocket, bluetoothSockets " + bluetoothSockets.toString());
        System.out.println("BTManager::closeSocket, sensorList " + sensorList.toString());
    }

    public void setUICallback(IUICallback inputUICallback){
        UICallback = inputUICallback;
    }

    public void removeUICallback() {
        UICallback = null;
    }

    public void startReadingManually(ISensor sensor) {
        try {
            //Create a thread and start reading
            BluetoothSocket mySocket = findSocket(sensor.getName());
            sensor.startReadISensor(mySocket);
            System.out.println("in BTManager::startReadingManually State of thread of sensor: " + sensor.getName() + " is : " + sensor.getThreadState().toString());
        } catch (Exception e) {
            System.out.println("BTManager :startRead exception for sensor " + e.toString());
        }
    }

    private void setOnConnectionLostHandler(final BluetoothSocket socket, final ISensor sensor){
        final BluetoothDevice device = sensor.getDevice();
        sensor.setOnConnectionLostHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exception) {
                System.out.println("Got Exception in Thread: " + thread.getName() + " Exception is: " + exception.getMessage());
                sensor.setCanRead(false);
                sensor.setState(SENSOR_STATE.CONNECTED);
                //TODO: Restarting Thread
                try {
                    thread.start();
                } catch (Exception e) {
                    System.out.println("Exception " + e.toString());
                }

                try {
                    thread.join(200);
                    System.out.println("thread.join executed");
                } catch (InterruptedException e) {
                    System.out.println("Exception occurred for thread.join() in: " + thread.getName());
                }
                if (!thread.isAlive()){
                    closeSocketAndStream(socket, sensor);
                    sensor.setState(SENSOR_STATE.NOT_CONNECTED);
                    communicationCB.onConnectionLost(device);
                } else {
                    System.out.println("Thread is still Alive ");
                }
            }
        });
    }
}
