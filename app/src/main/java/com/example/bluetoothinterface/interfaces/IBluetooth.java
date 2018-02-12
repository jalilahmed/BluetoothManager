package com.example.bluetoothinterface.interfaces;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    // BT Module methods
    boolean isEnabled();
    void enable(Activity someActivity);
    List<String> getPairedDevices();
    void discoverDevices(Activity someActivity);
    void stopDiscoverDevices();
    void connectToMiPods(ArrayList<String> miPodsDevicesNames, Activity someActivity);
    void test();
    //void closeSocket(BluetoothSocket socket);
    //void removeSensor(ISensor sensor);
    void stopReading(String sensor);


    // Discovery Callback methods
    void setDiscoveryCB(IDiscoveryCallback discoveryCB);
    void removeDiscoveryCallback();

    // Communication Callback methods
    void setCommunicationCB(ICommunicationCallback communicationCB);
    void removeCommunicationCallback();
}
