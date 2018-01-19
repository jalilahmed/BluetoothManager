package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.DiscoveryCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // ListView variables
    ArrayList<String> pairedDevices = new ArrayList<>();
    ArrayAdapter<String> btDevicesListViewAdapter;

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
                System.out.println(e.toString());
            }
        }

        startDiscoveryScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery();
            }
        });
    }

    public void startDiscovery() {
        pairedDevices = myInterface.getPairedDevices();
        System.out.println("Already paired devices :: " + pairedDevices);
        btDevicesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,pairedDevices);
        btDevicesListView.setAdapter(btDevicesListViewAdapter);

        myInterface.discoverDevices();

        myInterface.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDevice(BluetoothDevice device) {
                System.out.println("New device discovered :: " + device.getName() + ", " + device.getAddress());
                pairedDevices.add(device.getName());
                btDevicesListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
