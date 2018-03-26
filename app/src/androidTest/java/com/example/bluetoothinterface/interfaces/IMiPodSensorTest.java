package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.test.runner.AndroidJUnit4;

import com.example.bluetoothinterface.DataHolder;
import com.example.bluetoothinterface.bluetooth_module.BTFactory;
import com.example.bluetoothinterface.bluetooth_module.SENSOR_STATE;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Prashant on 3/16/2018.
 * Portabiles Healthcare Technologies
 */
@RunWith(AndroidJUnit4.class)
public class IMiPodSensorTest {
    private IBluetooth BTInterface = BTFactory.getInstance();
    private IDataHolder dataStore = DataHolder.getInstance();
    private ArrayList<ISensor> sensors = new ArrayList<>();
    private String knownPairedSensorName = "miPod3-6FBC3B000BA30400";
    private String getKnownPairedSensorMac = "10:00:E8:D5:7E:DB";
    private ISensor connectedISensor;

    @Before
    public void setUpConnection () throws Exception{
        BTInterface.setPairedDevices();
        BTInterface.connectToMiPod(knownPairedSensorName);
        sensors = dataStore.getISensor();
        System.out.println("IMiPodSensorTest :: setUpConnection(), ISensors are : " + sensors);
        connectedISensor = sensors.get(0);
    }

    @Test
    public void getName() throws Exception {
        assertEquals(knownPairedSensorName, connectedISensor.getName());
    }

    @Test
    public void getMacAddress() throws Exception {
        assertEquals(getKnownPairedSensorMac, connectedISensor.getMacAddress());
    }

    @Test
    public void getState() throws Exception {
        assertEquals(SENSOR_STATE.READING, connectedISensor.getState());
    }

    //TODO: Change the state in a valid connection test ?
    @Test
    public void setState() throws Exception {
        connectedISensor.setState(SENSOR_STATE.CONNECTED);
        assertEquals(SENSOR_STATE.CONNECTED, connectedISensor.getState());
    }

    @Test
    public void getPosition() throws Exception {
        String position = "left";
        assertEquals(position, connectedISensor.getPosition());
    }

    @Test
    public void getDevice() throws Exception {
        BluetoothDevice device = sensors.get(0).getDevice();
        assertEquals(knownPairedSensorName, device.getName());
        assertEquals(getKnownPairedSensorMac, device.getAddress());

        System.out.println("Thread state is " + connectedISensor.getThreadState());
    }

    @Test
    public void getLastReadTime() throws Exception {
        System.out.println("IMiPodSensorTest :: getLastReadTime() " + connectedISensor.getLastReadTime());
    }

    @Test
    public void getThreadState() throws Exception {
        assertEquals(Thread.State.RUNNABLE, connectedISensor.getThreadState());
    }

    @After
    public void clearConnection () throws  Exception {
        System.out.println("ISensor test :: clearing connection with " + knownPairedSensorName);
        ArrayList<BluetoothSocket> sockets = BTInterface.getSockets();
        BTInterface.closeSocketAndStream(sockets.get(0), sensors.get(0));
    }

}