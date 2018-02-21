package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothSocket;

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
    void connectToMiPod(String miPodSensorName);
    void stopReading(ISensor sensor);
    void closeSocket(BluetoothSocket socket, ISensor sensor);
    //void removeSensor(ISensor sensor);
    void startReading(ISensor sensor);


    // Discovery Callback methods
    void setDiscoveryCB(IDiscoveryCallback discoveryCB);
    void removeDiscoveryCallback();
    void setUICallback(IUICallback UICallback);
    void removeUICallback();

    // Communication Callback methods
    void setCommunicationCB(ICommunicationCallback communicationCB);
    void removeCommunicationCallback();
}
