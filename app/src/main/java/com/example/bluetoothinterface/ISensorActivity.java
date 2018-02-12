package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IDataHolder;

import java.util.ArrayList;

public class ISensorActivity extends AppCompatActivity {

    // UI Elements
    TextView LeftNameTV, RightNameTV, LeftConnectedTV, RightConnectedTV, LeftReadingTV, RightReadingTV;
    Button ConnectBtn, LeftStopReading, RightStopReading, LeftDisconnect, RightDisconnect;

    // Sensors
    ArrayList<String> selectedSensors = new ArrayList<>();

    // Data source
    private IDataHolder dataStore = DataHolder.getInstance();

    // Bluetooth objects
    IBluetooth myInterface = BTManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_isensor );

        // Initializing all views
        LeftNameTV = findViewById(R.id.ISensorLeftName);
        RightNameTV = findViewById(R.id.ISensorRightName);
        LeftConnectedTV = findViewById(R.id.ISensorLeftConnectedTV);
        RightConnectedTV = findViewById(R.id.ISensorRightConnectedTV);
        LeftReadingTV = findViewById(R.id.ISensorLeftReadingTV);
        RightReadingTV = findViewById(R.id.ISensorRightReadingTV);

        ConnectBtn = findViewById(R.id.ISensorsConnectBtn);
        LeftStopReading = findViewById(R.id.ISensorLeftStopReadingBtn);
        RightStopReading = findViewById(R.id.ISensorRightStopReadingBtn);
        LeftDisconnect = findViewById(R.id.ISensorLeftDisconnectBtn);
        RightDisconnect = findViewById(R.id.ISensorRightDisconnectBtn);

        // Initially set all TV as red
        LeftConnectedTV.setBackgroundColor(Color.RED);
        RightConnectedTV.setBackgroundColor(Color.RED);

        LeftReadingTV.setBackgroundColor(Color.RED);
        RightReadingTV.setBackgroundColor(Color.RED);

        try {
            selectedSensors = dataStore.getSelectedSensors();
            System.out.println("ISensorActivity :: selected Sensors " + selectedSensors);

            if (selectedSensors != null) {
                LeftNameTV.setText(selectedSensors.get(0));
                RightNameTV.setText(selectedSensors.get(1));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        ConnectBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectISensors();
            }
        });

        LeftStopReading.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading(selectedSensors.get(0));
            }
        });

        RightStopReading.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading(selectedSensors.get(1));
            }
        });
    }

    public void connectISensors() {

        myInterface.setCommunicationCB( new ICommunicationCallback() {
            @Override
            public void onConnect(BluetoothDevice device) {

                System.out.println("ISensor Activity :: onConnect " + device.getName());

                if (device.getName().equals(selectedSensors.get(0))) {
                    LeftConnectedTV.setBackgroundColor(Color.GREEN);
                    LeftReadingTV.setBackgroundColor(Color.GREEN);
                } else if (device.getName().equals(selectedSensors.get(1))) {
                    RightConnectedTV.setBackgroundColor(Color.GREEN);
                    RightReadingTV.setBackgroundColor(Color.GREEN);
                }

                Toast.makeText(getApplicationContext(), "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                System.out.println( "ISensorActivity :: onConnect successful with " + device.getName());
            }

            @Override
            public void onError(String message) {
                System.out.println( "ISensorActivity :: onError " + message);
                myInterface.removeCommunicationCallback();
            }

            @Override
            public void onConnectError(String message) {
                System.out.println( "ISensorActivity :: onConnectError " + message );
                myInterface.removeCommunicationCallback();
            }

            @Override
            public void onDisconnect(String message) {
                System.out.println( "ISensorActivity:: onDisconnect " + message );
                myInterface.removeCommunicationCallback();
            }

            @Override
            public void onConnectionLost(BluetoothDevice device) {
                System.out.println("ISensorActivity:: onConnectionLost :: device is " + device.getName());
                if (device.getName().equals(selectedSensors.get(0))) {
                    LeftConnectedTV.setBackgroundColor(Color.RED);
                } else if (device.getName().equals(selectedSensors.get(1))) {
                    RightConnectedTV.setBackgroundColor(Color.RED);
                }

                System.out.println("ISensorActivity::Lost Communication with " + device.getName());
            }

            @Override
            public void onStopReading(BluetoothDevice device) {
                if (device.getName().equals(selectedSensors.get(0))) {
                    LeftReadingTV.setBackgroundColor(Color.RED);
                } else if (device.getName().equals(selectedSensors.get(1))) {
                    RightReadingTV.setBackgroundColor(Color.RED);
                }
            }
        });

        myInterface.connectToMiPods( selectedSensors, ISensorActivity.this);
    }
}
