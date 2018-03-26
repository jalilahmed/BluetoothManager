package com.example.bluetoothinterface;

import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.phct.btmanagerlibrary.interfaces.IDataHolder;
import com.phct.btmanagerlibrary.module.DataHolder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Prashant on 3/24/2018.
 * Portabiles Healthcare Technologies
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private BluetoothAdapter mmAdapter = BluetoothAdapter.getDefaultAdapter();
    private IDataHolder dataStore = DataHolder.getInstance();
    private static String knownPairedSensor = "miPod3-6FBC3B000BA30400";
    private MainActivity mmActivity = null;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mmActivity = mainActivityRule.getActivity();
    }

    @Test
    public void enableBluetoothTest() throws Exception {
        mmActivity.enableBluetooth();
        SystemClock.sleep(200);
        // mmActivity.enableBluetooth();
        assertEquals(true, mmAdapter.isEnabled());
    }

    @Test
    public void startISensorActivityTest() throws Exception {
        assertNotNull(mmActivity.findViewById(R.id.selectAndStartBtn));

        onView(withId(R.id.selectAndStartBtn)).perform(click());

    }
//    @Test
//    public void startDiscoveryTest() throws Exception {
//        if (!mmAdapter.isEnabled()) {
//            mmActivity.enableBluetooth();
//            SystemClock.sleep(200);
//        }
//        mmActivity.startDiscovery();
//        //BTInterface.discoverDevices();
//
//        SystemClock.sleep(2000);
//        assertEquals(true, mmAdapter.isDiscovering());
//    }



//    @Test
//    public void registerReceiver() throws Exception {
//    }
//
//    @Test
//    public void onDevice() throws Exception {
//    }
//
//    @Test
//    public void onFinish() throws Exception {
//    }

    @After
    public void tearDown() throws Exception {
        mmActivity = null;
    }

}