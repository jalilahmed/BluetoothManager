package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.Date;
import java.util.List;


/**
 * Created by jalil on 1/23/2018.
 */

class Sensor implements ISensor {

    // ISensor interface members
    private String name;
    private String macAddress;
    private SENSOR_STATE state;
    private String position;
    private ReadStream thread;
    private BluetoothSocket socket;

    //
    private BluetoothDevice device;
    private List<DataFrameFactory> last5SecondsData;
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

    public void setData(List<DataFrameFactory> data5Seconds) {
        last5SecondsData = data5Seconds;
    }

    public List<DataFrameFactory> getData() {
        return last5SecondsData;
    }

    public void setLastReadTime(Date dateTimeNow) {
        timeOfLastRead = dateTimeNow;
    }

    public Date getLastReadTime() {
        return timeOfLastRead;
    }

    public void startReadISensor(BluetoothSocket socket, ICommunicationCallback CommunicationCB) {
        //Todo: Handle Callback for exception
        try{
            thread = new ReadStream(this, socket, CommunicationCB );
            if (state == SENSOR_STATE.CONNECTED){
                // Todo: if state is different
                thread.start();
            }
        } catch(Exception e){
            System.out.println("Exception in ISensor :" + name + " : " + e.toString());
        }

    }

    public Thread.State getThreadStateISensor() {
        return thread.getState();
    }

}
