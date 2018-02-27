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
    void closeSocketAndStream(BluetoothSocket socket, ISensor sensor);
    //void removeSensor(ISensor sensor);
    void startReadingManually(ISensor sensor);


    // Discovery Callback methods
    void setDiscoveryCB(IDiscoveryCallback discoveryCB);
    void removeDiscoveryCallback();
    void setUICallback(IUICallback UICallback);
    void removeUICallback();

    // Communication Callback methods
    void setCommunicationCB(ICommunicationCallback communicationCB);
    ICommunicationCallback getCommunicationCB();
    void removeCommunicationCallback();

    // Quality Check callback methods
    void setQualityCheckCB(IQualityCheckCallback qualityCB);
    IQualityCheckCallback getQualityCheckCB();
    void removeQualityCheckCallback();
}
