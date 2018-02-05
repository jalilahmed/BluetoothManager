package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IDiscoveryCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    Button startDiscoveryScanBtn, connectBtn, enableBTBtn;
    ListView btDevicesListView;

    // ListView variables
    List<String> displayDevices = new ArrayList<>();
    ArrayAdapter<String> btDevicesListViewAdapter;

    // Bluetooth devices list
    List<BluetoothDevice> allDevicesWithinRange = new ArrayList<>();
    ArrayList<BluetoothDevice> clickedSensors = new ArrayList<>();

    // Bluetooth objects
    IBluetooth myInterface = BTManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing all views
        enableBTBtn = findViewById(R.id.enableBTBtn);
        startDiscoveryScanBtn = findViewById(R.id.startDiscoveryScanBtn);
        connectBtn = findViewById(R.id.connectBtn);
        btDevicesListView  = findViewById(R.id.btDevicesListView);

        // Enable Bluetooth Button if disabled
        enableBTBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
            }
        });

        // Load the list view with paired devices before discovering
        allDevicesWithinRange = myInterface.getPairedDevices();
        defaultListView(allDevicesWithinRange);

        btDevicesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayDevices);
        btDevicesListView.setAdapter(btDevicesListViewAdapter);

        // Start discovery button onClick
        startDiscoveryScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myInterface.isEnabled()) { startDiscovery(); }
                else { Toast.makeText(getApplicationContext(), "Enable Bluetooth to discover",  Toast.LENGTH_SHORT).show(); }
            }
        });

        btDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = displayDevices.get(i);

                for (BluetoothDevice device : allDevicesWithinRange) {
                    if (device.getName().equals(clickedItem)) {
                        clickedSensors.add(device);
                        btDevicesListView.getChildAt(i).setBackgroundColor(Color.GREEN);
                    }
                }
            }
        });

        connectBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectSensors();
            }
        } );
    }

    public void enableBluetooth () {
        if (!myInterface.isEnabled()) {
            try {
                myInterface.enable(MainActivity.this);
                System.out.println("Bluetooth enabled");
            }
            catch (Exception e) {
                System.out.println( "Bluetooth cannot be enabled" );
            }
        }
    }

    public void defaultListView(List<BluetoothDevice> pairedDevices) {
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                displayDevices.add(device.getName());
            }
        }
    }

    public void startDiscovery() {

        myInterface.setDiscoveryCB(new IDiscoveryCallback() {
            @Override
            public void onDevice(BluetoothDevice device) {
                try {
                    if (device.getName() != null) {
                        if (!displayDevices.contains(device.getName())) {
                            allDevicesWithinRange.add(device);
                            displayDevices.add(device.getName());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception for getting device name, " + e.toString());
                }
                btDevicesListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                myInterface.removeDiscoveryCallback(); //Always remove the discovery callback
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        myInterface.discoverDevices(MainActivity.this);
    }

    public void connectSensors() {
        myInterface.removeDiscoveryCallback(); //Always remove the discovery callback

        myInterface.setCommunicationCB( new ICommunicationCallback() {
            @Override
            public void onConnect(BluetoothDevice device) {
                Toast.makeText(getApplicationContext(), "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                System.out.println( "Main Activity :: onConnect successful with " + device.getName());
            }

            @Override
            public void onError(String message) {
                System.out.println( "Main Activity :: onError " + message);
                myInterface.removeCommunicationCallback();
            }

            @Override
            public void onConnectError(String message) {
                System.out.println( "Main Activity :: onConnectError " + message );
                myInterface.removeCommunicationCallback();
            }

            @Override
            public void onDisconnect(String message) {
                System.out.println( "Main Activity :: onDisconnect " + message );
                myInterface.removeCommunicationCallback();
            }
        });

        myInterface.connectToMiPods( clickedSensors, MainActivity.this );
    }
}
