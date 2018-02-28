package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

import com.example.bluetoothinterface.bluetooth_module.DataFrameFactory;

import java.util.ArrayList;

/**
 * Created by jalil on 2/6/2018.
 */

public interface IQMSensor {
    void qualityCheck(ArrayList<DataFrameFactory> latestData,
                      IQualityCheckCallback qualityCheckCallback,
                      BluetoothDevice sensor) throws Exception;
    void clearAllBuffer();
}
