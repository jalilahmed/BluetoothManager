package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothDevice;

import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by jalil on 1/23/2018.
 */

public class Sensor implements ISensor {

    // ISensor interface members
    private String name;
    private String macAddress;
    private SENSOR_STATE state;
    private String position;


    //
    private BluetoothDevice device;
    private List<DataFrame> last5SecondsData;
    private Date timeOfLastRead;

    Sensor(BluetoothDevice miPodSensor, String pos) {
        name = miPodSensor.getName();
        macAddress = miPodSensor.getAddress();
        state = SENSOR_STATE.NOT_CONNECTED;
        position = pos;
        device = miPodSensor;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getName() {
        return name;
    }

    public SENSOR_STATE getState() {
        return state;
    }

    public void setState(SENSOR_STATE newState) {
        state = newState;
    }

    public String getPosition() {
        return position;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setData(List<DataFrame> data5Seconds) {
        last5SecondsData = data5Seconds;
    }

    public void setLastReadTime(Date dateTimeNow) {
        timeOfLastRead = dateTimeNow;
    }

}