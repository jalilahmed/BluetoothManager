package com.example.bluetoothinterface;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetoothinterface.Objects.Sensor;
import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.DiscoveryCallback;
import com.example.bluetoothinterface.interfaces.IBluetooth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DataHolder dataStore = DataHolder.getInstance();
    // ListView variables
    List<String> displayDevices = new ArrayList<>();
    ArrayAdapter<String> btDevicesListViewAdapter;

    // Bluetooth devices list
    List<BluetoothDevice> allDevicesWithinRange = new ArrayList<>();

    // Bluetooth objects
    IBluetooth myInterface = BTManager.getInstance();

    // UI Elements
    Button startDiscoveryScanBtn;
    ListView btDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing all views
        startDiscoveryScanBtn = findViewById(R.id.startDiscoveryScanBtn);
        btDevicesListView  = findViewById(R.id.btDevicesListView);

        if (!myInterface.isEnabled()) {
            try {
                System.out.println("SomethingSomething");
                myInterface.enable(MainActivity.this);
            }
            catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }

        startDiscoveryScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery();
            }
        });

        btDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startDiscoveryScanBtn.setEnabled(true);
                myInterface.removeDiscoveryCallback();
                String clickedItem = displayDevices.get(i);
                System.out.println("in onItemClick(): " + clickedItem);
                for (BluetoothDevice device : allDevicesWithinRange) {
                    if (device.getName().equals(clickedItem)) {
                        System.out.println("Trying to Connect to this: " + device.getName());
                        //myInterface.connectByDevice(device);
                    }
                }
            }
        });
    }

    public void startDiscovery() {
        Sensor mySensor = new Sensor("sensor1", "xx:xx:xx:xx");
        boolean result = mySensor.read("success");

        displayDevices.clear();
        allDevicesWithinRange = myInterface.getPairedDevices();

        if (!allDevicesWithinRange.isEmpty()) {
            for (BluetoothDevice device : allDevicesWithinRange) {
                displayDevices.add(device.getName());
            }
        }

        btDevicesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataStore.getAvailableDevices());
        btDevicesListView.setAdapter(btDevicesListViewAdapter);


        myInterface.discoverDevices(MainActivity.this);
        startDiscoveryScanBtn.setEnabled(false);

        {myInterface.setDiscoveryCallback(new DiscoveryCallback()
            @Override
            public void onDevice() {
                btDevicesListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Finished discovering for devices");
                startDiscoveryScanBtn.setEnabled(true);
                myInterface.removeDiscoveryCallback();
            }
        });
    }
}
