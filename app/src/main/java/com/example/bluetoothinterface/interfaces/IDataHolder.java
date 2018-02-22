package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by jalil on 2/5/2018.
 */

public interface IDataHolder {
    ArrayList<String> getAvailableSensors();
    void setAvailableSensors(String newDevice);

    ArrayList<String> getSelectedSensors();
    void setSelectedSensors(String newSensor);

    ArrayList<ISensor> getISensor();
    void setISensor(ISensor sensor);
}
