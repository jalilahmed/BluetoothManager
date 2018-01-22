package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.DiscoveryCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // ListView variables
    List<String> displayDevices = new ArrayList<>();
    ArrayAdapter<String> btDevicesListViewAdapter;

    // Bluetooth devices list
    List<BluetoothDevice> allDevicesWithinRange = new ArrayList<>();

    // Bluetooth objects
    BTManager myInterface = new BTManager(MainActivity.this);

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
                myInterface.enable();
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

//        btDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                String clickedItem = displayDevices.get(i);
//                for (BluetoothDevice device : allDevicesWithinRange) {
//                    if (device.getName().equals(clickedItem)) {
//                        myInterface.connectByDevice(device);
//                    }
//                    Log.d(TAG, device.getName());
//                }
//            }
//        });
    }

    public void startDiscovery() {
        displayDevices.clear();
        allDevicesWithinRange = myInterface.getPairedDevices();

        if (!allDevicesWithinRange.isEmpty()) {
            for (BluetoothDevice device : allDevicesWithinRange) {
                displayDevices.add(device.getName());
            }
        }

        btDevicesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayDevices);
        btDevicesListView.setAdapter(btDevicesListViewAdapter);

        myInterface.discoverDevices();
        // Is working fine until here

        myInterface.discoverDevices();
        startDiscoveryScanBtn.setEnabled(false);

        myInterface.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDevice(BluetoothDevice device) {
                allDevicesWithinRange.add(device);
                try {
                    Log.d(TAG, "Device name is " + device.getName() + ",  " + device.getAddress());
//                    if (device.getName() != null) {
//                        displayDevices.add(device.getName());
//                    } else {
//                        Log.d(TAG, "Error in getting device name ");
//                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception for getting device name, " + e.toString());
                }
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
