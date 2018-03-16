package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import com.example.bluetoothinterface.bluetooth_module.BTFactory;
import com.example.bluetoothinterface.bluetooth_module.SENSOR_STATE;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Prashant on 3/6/2018.
 * Portabiles Healthcare Technologies
 */
@RunWith(AndroidJUnit4.class)
public class IBluetoothTest {
    private IBluetooth BTInterface = BTFactory.getInstance();
    private BluetoothAdapter mmAdapter = BluetoothAdapter.getDefaultAdapter();
    private IDataHolder dataStore = DataHolder.getInstance();

    @Test
    public void testIsEnabled() throws Exception {
        boolean actualResult = BTInterface.isEnabled();
        assertEquals(mmAdapter.isEnabled(), actualResult);
    }

    @Test
    public void testEnableTest() throws Exception {
        BTInterface.enable();
        SystemClock.sleep(500);
        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
    }

//    @Test
//    public void testDisableTest() throws Exception {
//        BTInterface.disable();
//        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
//    }

    @Test
    public void setPairedDevicesTest() {
        BTInterface.setPairedDevices();
        ArrayList<String> pairedDevices = dataStore.getAvailableSensors();
        String knownPairedSensor = "miPod3-6FBC3B000BA30400";

        assertEquals(true, pairedDevices.contains(knownPairedSensor));
    }

    @Test
    public void connectToMiPodTest() {
        String knownPairedSensor = "miPod3-6FBC3B000BA30400";
        BTInterface.connectToMiPod(knownPairedSensor);

        // Check if ISensor object was created
        ArrayList<ISensor> sensors = dataStore.getISensor();
        assertEquals(knownPairedSensor, sensors.get(0).getName());

        // Check if Sensor state was changed
        SENSOR_STATE sensorState = SENSOR_STATE.READING;
        assertEquals(sensorState, sensors.get(0).getState());
    }

    @Test
    public void stopReadingTest() {
        ArrayList<ISensor> sensors = dataStore.getISensor();
        BTInterface.stopReading(sensors.get(0));

        // Check if Sensor state was changed
        SENSOR_STATE sensorState = SENSOR_STATE.CONNECTED;
        assertEquals(sensorState, sensors.get(0).getState());

    }

//    @Test
//    public void closeSocketAndStreamTest() {
//
//        ArrayList<BluetoothSocket> sockets = new ArrayList<>();
//        sockets = BTInterface.getSockets();
//        System.out.println("Size of bluetooth sockets " + sockets.size());
//        ArrayList<ISensor> sensors = dataStore.getISensor();
//
////        BTInterface.closeSocketAndStream(sockets.get(0), sensors.get(0));
//
////        // Check if Sensor state was changed
////        SENSOR_STATE sensorState = SENSOR_STATE.CONNECTED;
////        assertEquals(sensorState, sensors.get(0).getState());
////
////        // After removing socket, BTInterface.getSockets() should be empty again
//        System.out.println("Size of BTManager bluetooth sockets " + BTInterface.getSockets().size());
//    }

}