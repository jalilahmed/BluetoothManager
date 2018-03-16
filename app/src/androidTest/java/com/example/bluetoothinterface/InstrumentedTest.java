//package com.example.bluetoothinterface;
//
//import android.bluetooth.BluetoothAdapter;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.example.bluetoothinterface.bluetooth_module.BTFactory;
//import com.example.bluetoothinterface.interfaces.IBluetooth;
//import com.example.bluetoothinterface.interfaces.IDataHolder;
//import com.example.bluetoothinterface.interfaces.IDiscoveryCallback;
//import com.example.bluetoothinterface.interfaces.IUICallback;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by Prashant on 3/6/2018.
// * Portabiles Healthcare Technologies
// */
//@RunWith(AndroidJUnit4.class)
//public class InstrumentedTest {
//
//    private IBluetooth BTInterface = BTFactory.getInstance();
//    private BluetoothAdapter mmAdapter = BluetoothAdapter.getDefaultAdapter();
//    private IDataHolder dataStore = DataHolder.getInstance();
//    private IUICallback UICallback;
//    private IDiscoveryCallback DiscoveryCallback;
//
//    @Test
//    public void testIsEnabled() throws Exception {
//        boolean actualResult = BTInterface.isEnabled();
//        assertEquals(mmAdapter.isEnabled(), actualResult);
//    }
//
//    @Test
//    public void testEnableTest() throws Exception {
//        BTInterface.enable();
//        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
//    }
//
////    @Test
////    public void testDisableTest() throws Exception {
////        BTInterface.disable();
////        assertEquals(mmAdapter.isEnabled(), BTInterface.isEnabled());
////    }
//
//    @Test
//    public void discoverDevicesTest() {
//        BTInterface.setUICallback(UICallback);
//        BTInterface.setDiscoveryCB(DiscoveryCallback);
//
//        BTInterface.discoverDevices();
//    }
//
////    @Test
////    public void setPairedDevicesTest() throws Exception {
////        String dummySensor = "miPod3-xxxx-xxxx-xxxx";
////        dataStore.setAvailableSensors(dummySensor);
////
////        // TODO: How do I pass this dummyDevice to bondedDevices of Bluetooth Adapter?
////
////        // BTInterface.setPairedDevices();
////
////    }
//
//
//}
