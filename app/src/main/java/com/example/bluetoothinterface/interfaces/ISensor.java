package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;

import com.example.bluetoothinterface.bluetooth_module.DataFrame;
import com.example.bluetoothinterface.bluetooth_module.SENSOR_STATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Prashant on 05/02/2018.
 */

public interface ISensor {
    String getName();
    String getMacAddress();
    SENSOR_STATE getState();
    void setState(SENSOR_STATE someState);
    String getPosition();
    BluetoothDevice getDevice();

    void setData(List<DataFrame> data5Seconds);
    void setLastReadTime(Date dateTimeNow);
}
