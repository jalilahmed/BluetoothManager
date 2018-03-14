package com.example.bluetoothinterface.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.support.test.runner.AndroidJUnit4;

import com.example.bluetoothinterface.DataHolder;
import com.example.bluetoothinterface.bluetooth_module.BTFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
    }

    @Test
    public void testDisableTest() throws Exception {
        BTInterface.disable();
        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
    }

}