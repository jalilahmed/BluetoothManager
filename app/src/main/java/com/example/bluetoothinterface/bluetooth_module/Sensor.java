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

    private String name;
    private String macAddress;
    private SENSOR_STATE state;
    private String position;
    private ReadStream thread;
    private boolean canRead;
    private Thread.UncaughtExceptionHandler onConnectionLostHandler;
    private BluetoothDevice device;
    private List<DataFrameFactory> last5SecondsData;
    private Date timeOfLastRead;
    private ICommunicationCallback communicationCallback;

    Sensor(BluetoothDevice miPodSensor, String pos, ICommunicationCallback communicationCB) {
        name = miPodSensor.getName();
        macAddress = miPodSensor.getAddress();
        state = SENSOR_STATE.NOT_CONNECTED;
        position = pos;
        device = miPodSensor;
        canRead = false;
        communicationCallback = communicationCB;
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
            if (state == SENSOR_STATE.CONNECTED){
                canRead = true;
                setOnConnectionLostHandler();
                thread = new ReadStream(this, socket, CommunicationCB);
                thread.start();
            }
        } catch(Exception e){
            System.out.println("Exception in ISensor :" + name + " : " + e.toString());
        }

    }

    public Thread.State getThreadState() {
        return thread.getState();
    }

    private void setOnConnectionLostHandler () {
        onConnectionLostHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exception) {
                //TODO: Change ISensor canRead to False.
                //Todo: Callblack to change reading to read
                //todo: Check if thread is alive
                //todo: if dead then change state to not_connected
                //todo: closeSocket.
                System.out.print("Got Exception in Thread: " + thread.getName() + "Exception is: " + exception.toString());
                communicationCallback.onConnectionLost(device);
                communicationCallback.onStopReading(device);
                // TODO: Socket should be a private attribute of ISensor or it should be passed here.
                //IBTManager.closeSocket(socket, device);
            }
        };
    }

}
