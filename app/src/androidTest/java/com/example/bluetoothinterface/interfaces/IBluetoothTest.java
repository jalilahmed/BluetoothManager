package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;


import com.phct.btmanagerlibrary.interfaces.IBluetooth;
import com.phct.btmanagerlibrary.interfaces.IDataHolder;
import com.phct.btmanagerlibrary.interfaces.ISensor;
import com.phct.btmanagerlibrary.module.BTFactory;
import com.phct.btmanagerlibrary.module.DataHolder;
import com.phct.btmanagerlibrary.module.SENSOR_STATE;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Prashant on 3/6/2018.
 * Portabiles Healthcare Technologies
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class IBluetoothTest {
    private IBluetooth BTInterface = BTFactory.getInstance();
    private BluetoothAdapter mmAdapter = BluetoothAdapter.getDefaultAdapter();
    private IDataHolder dataStore = DataHolder.getInstance();
    private ArrayList<ISensor> sensors;
    private static String knownPairedSensor = "miPod3-6FBC3B000BA30400";

    @Test
    public void testIsEnabled() throws Exception {
        // System.out.println("IBluetoothTest :: testIsEnabled started");
        boolean actualResult = BTInterface.isEnabled();
        assertEquals(mmAdapter.isEnabled(), actualResult);
        // System.out.println("IBluetoothTest :: testIsEnabled finished");
    }

    @Test
    public void testEnableTest() throws Exception {
        // System.out.println("IBluetoothTest :: testEnableTest started");
        BTInterface.enable();
        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
        // System.out.println("IBluetoothTest :: testEnableTest finished");
    }

    @Test
    public void tesStopDiscoveringDevices() throws Exception {
        // System.out.println("IBluetoothTest :: tesStopDiscoveringDevices started");
        BTInterface.stopDiscoverDevices();
        assertEquals(false, mmAdapter.isDiscovering());
        // System.out.println("IBluetoothTest :: tesStopDiscoveringDevices finished");
    }

    @Test
    public void testSetPairedDevices() throws Exception {
        // System.out.println("IBluetoothTest :: testSetPairedDevices started");
        ArrayList<String> pairedDevices = dataStore.getAvailableSensors();
        System.out.println("testSetPairedDevices :: paired devices in data store : " + pairedDevices);

        assertEquals(true, pairedDevices.contains(knownPairedSensor));
        // System.out.println("IBluetoothTest :: testSetPairedDevices finished");
    }

    @Test
    public void testConnectToMiPod() throws Exception {
        // System.out.println("IBluetoothTest :: testConnectToMiPod started");

        BTInterface.setPairedDevices();
        sensors = dataStore.getISensor();
        BTInterface.connectToMiPod(knownPairedSensor);

        // Check if ISensor object was created
        if (sensors.size() != 0) {
            assertEquals(knownPairedSensor, sensors.get(0).getName());
        } else {
            throw new Exception("ISensors are not set correctly");
        }

        // Check if Sensor state was changed
        SENSOR_STATE sensorState = SENSOR_STATE.READING;
        assertEquals(sensorState, sensors.get(0).getState());
        assertEquals(true, sensors.get(0).getCanRead());

        ArrayList<BluetoothSocket> sockets = BTInterface.getSockets();
        BTInterface.closeSocketAndStream(sockets.get(0), sensors.get(0));

        // System.out.println("IBluetoothTest :: testConnectToMiPod finished");
    }

    @Test
    public void testStopReadingTest() throws Exception {
        // System.out.println("IBluetoothTest :: testStopReadingTest started");

        BTInterface.setPairedDevices();
        sensors = dataStore.getISensor();
        BTInterface.connectToMiPod(knownPairedSensor);

        BTInterface.stopReading(sensors.get(0));

        // Check if Sensor state was changed
        SENSOR_STATE sensorState = SENSOR_STATE.CONNECTED;
        assertEquals(sensorState, sensors.get(0).getState());
        assertEquals(false, sensors.get(0).getCanRead());
        System.out.println("testStopReadingTest :: Sensor in dataStore:: " + sensors);

        ArrayList<BluetoothSocket> sockets = BTInterface.getSockets();
        BTInterface.closeSocketAndStream(sockets.get(0), sensors.get(0));

        // System.out.println("IBluetoothTest :: testStopReadingTest finished");
    }

    @Test
    public void testCloseSocketAndStreamTest() throws Exception {
        // System.out.println("IBluetoothTest :: testCloseSocketAndStreamTest started");

        BTInterface.setPairedDevices();
        sensors = dataStore.getISensor();
        BTInterface.connectToMiPod(knownPairedSensor);

        ArrayList<BluetoothSocket> sockets = BTInterface.getSockets();
        BTInterface.closeSocketAndStream(sockets.get(0), sensors.get(0));

        // After removing socket, BTInterface.getSockets() should be empty again
        assertEquals(0, BTInterface.getSockets().size());

        // System.out.println("IBluetoothTest :: testCloseSocketAndStreamTest finished");
    }
}
