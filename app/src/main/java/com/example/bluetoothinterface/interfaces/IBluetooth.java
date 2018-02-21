package com.example.bluetoothinterface.interfaces;

import java.util.ArrayList;

/**
 * Created by jalil on 1/12/2018.
 */

public interface IBluetooth {

    // BT Module methods
    boolean isEnabled();
    void enable();
    void setPairedDevices();
    void discoverDevices();
    void stopDiscoverDevices();
    void connectToMiPods(ArrayList<String> miPodsDevicesNames);
    void test();
    //void closeSocket(BluetoothSocket socket);
    //void removeSensor(ISensor sensor);
    void stopReading(String sensor);


    // Discovery Callback methods
    void setDiscoveryCB(IDiscoveryCallback discoveryCB);
    void removeDiscoveryCallback();
    void setUICallback(IUICallback UICallback);
    //void removeUICallback();

    // Communication Callback methods
    void setCommunicationCB(ICommunicationCallback communicationCB);
    void removeCommunicationCallback();
}
