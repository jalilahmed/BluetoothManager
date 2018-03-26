package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.Date;
import java.util.List;


/**
 * Created by jalil on 1/23/2018.
 */

class MiPodSensor implements ISensor {

    private String name;
    private String macAddress;
    private SENSOR_STATE state;
    private String position;
    private ReadStream thread;
    private boolean canRead;
    private Thread.UncaughtExceptionHandler onConnectionLostHandler;
    private BluetoothDevice device;
    private List<DataFrameFactory> latestData;
    private Date timeOfLastRead;

    // BTManger Instance
    // private IBluetooth IBTManager = BTFactory.getInstance();

    MiPodSensor(BluetoothDevice miPodSensor, String pos) {
        name = miPodSensor.getName();
        macAddress = miPodSensor.getAddress();
        state = SENSOR_STATE.NOT_CONNECTED;
        position = pos;
        device = miPodSensor;
        canRead = false;
    }

    public boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
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

    public void setData(List<DataFrameFactory> localData) {
        latestData = localData;
    }

    public List<DataFrameFactory> getData() {
        return latestData;
    }

    public void setLastReadTime(Date dateTimeNow) {
        timeOfLastRead = dateTimeNow;
    }

    public Date getLastReadTime() {
        return timeOfLastRead;
    }

    public void startReadISensor(BluetoothSocket socket) throws Exception {
        try{
            if (state == SENSOR_STATE.CONNECTED){
                thread = new ReadStream(this, socket, onConnectionLostHandler);
                thread.start();
            }
        } catch(Exception e){
            throw new Exception("Exception in ISensor :" + name + " : " + e.toString());
        }
    }

    public Thread.State getThreadState() {
        return thread.getState();
    }

    public void setOnConnectionLostHandler (Thread.UncaughtExceptionHandler handler) {
        onConnectionLostHandler = handler;
    }

    public void joinThread(){
        thread.joinThread();
    }
}
