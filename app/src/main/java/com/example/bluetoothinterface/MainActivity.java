package com.example.bluetoothinterface;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.phct.btmanagerlibrary.interfaces.IBluetooth;
import com.phct.btmanagerlibrary.interfaces.IDataHolder;
import com.phct.btmanagerlibrary.interfaces.IDiscoveryCallback;
import com.phct.btmanagerlibrary.interfaces.IUICallback;
import com.phct.btmanagerlibrary.module.BTFactory;
import com.phct.btmanagerlibrary.module.DataHolder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IUICallback, IDiscoveryCallback {
    // UI Elements
    Button startDiscoveryScanBtn, startBtn, enableBTBtn;
    ListView btDevicesListView;

    // ListView variables
    ArrayAdapter<String> btDevicesListViewAdapter;

    // Data source
    private IDataHolder dataStore = DataHolder.getInstance();

    // Bluetooth objects
    IBluetooth myInterface = BTFactory.getInstance();
    ArrayList<String> allSensors = dataStore.getAvailableSensors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting Callback
        setCallbacks();

        // Initializing all views
        enableBTBtn = findViewById(R.id.enableBTBtn);
        startDiscoveryScanBtn = findViewById(R.id.startDiscoveryScanBtn);
        startBtn = findViewById(R.id.selectAndStartBtn);
        btDevicesListView  = findViewById(R.id.btDevicesListView);

        // Enable Bluetooth Button if disabled
        enableBTBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
            }
        });

        btDevicesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSensors);
        btDevicesListView.setAdapter(btDevicesListViewAdapter);

        // Set all paired devices in DataStore
        try {
            myInterface.setPairedDevices();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

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
                String clickedItem = allSensors.get(i);
                btDevicesListView.getChildAt(i).setBackgroundColor(Color.GREEN);
                dataStore.setSelectedSensors(clickedItem);
                System.out.println("Main Activity :: selectedSensors " + dataStore.getSelectedSensors());
            }
        });

        startBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start();
            }
        } );
    }

    private void setCallbacks(){
        myInterface.setUICallback(this);
        myInterface.setDiscoveryCB(this);
    }

    public void enableBluetooth () {

        if (!myInterface.isEnabled()) {
            try {
                myInterface.enable();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else {
//            myInterface.disable();
//        }
    }

    public void startDiscovery() {
        myInterface.discoverDevices();
    }

    public void Start() {
        myInterface.removeDiscoveryCallback(); //Always remove the discovery callback
        myInterface.stopDiscoverDevices();// Stops the discovering Process if its not finished up till now.
        if (dataStore.getSelectedSensors().size() != 0) {
            Intent startISensorIntent = new Intent(getApplicationContext(), ISensorActivity.class);
            startActivity(startISensorIntent);
            myInterface.removeUICallback();
        } else {
            Toast.makeText(getApplicationContext(), "No sensors selected",  Toast.LENGTH_SHORT).show();
        }

        // Debugging for Frames sorting mock
        // ReadThreadMock runner = new ReadThreadMock();
        // Thread thread = new Thread(runner);
        // thread.start();
    }

    @Override
    public void registerDiscoveryReceiver(IntentFilter filter, BroadcastReceiver receiver) {
        System.out.println("MainActivity:: registerReceiver");
        this.registerReceiver( receiver, filter );
    }

    @Override
    public void onDevice() {
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
}
