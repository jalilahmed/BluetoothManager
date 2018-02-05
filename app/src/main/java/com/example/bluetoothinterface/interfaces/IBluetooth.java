package com.example.bluetoothinterface.interfaces;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

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
    void connectToMiPods(ArrayList<String> miPodsDevicesNames, Activity someActivity);

    // Discovery Callback methods
    void setDiscoveryCB(IDiscoveryCallback discoveryCB);
    void removeDiscoveryCallback();

    // Communication Callback methods
    void setCommunicationCB(ICommunicationCallback communicationCB);
    void removeCommunicationCallback();
}
