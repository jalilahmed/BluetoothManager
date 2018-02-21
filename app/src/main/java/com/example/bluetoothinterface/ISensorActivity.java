package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothinterface.bluetooth_module.BTFactory;
import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IDataHolder;
import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.ArrayList;

public class ISensorActivity extends AppCompatActivity implements ICommunicationCallback{

    // UI Elements
    TextView LeftNameTV, RightNameTV, LeftConnectedTV, RightConnectedTV, LeftReadingTV, RightReadingTV;
    Button ConnectLeftBtn, ConnectRightBtn, LeftStartReading, RightStartReading, LeftDisconnect, RightDisconnect;

    // Sensors Names
    ArrayList<String> selectedSensors = new ArrayList<>();



    // Bluetooth objects
    IBluetooth myInterface = BTFactory.getInstance();
    private IDataHolder dataStore = DataHolder.getInstance();

    // ISensor Object
    ArrayList<ISensor> isensorList = dataStore.getISensor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_isensor );
        // Setting Callback
        myInterface.setCommunicationCB(this);

        // Initializing all views
        initializeAllViews();


        ConnectLeftBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectISensors(selectedSensors.get(0));
            }
        });

        ConnectRightBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectISensors(selectedSensors.get(1));
            }
        });

        LeftStartReading.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.startReading(isensorList.get(0));
            }
        });

        RightStartReading.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.startReading(isensorList.get(1));
            }
        });

        LeftDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading(isensorList.get(0));
            }
        });

        RightDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading(isensorList.get(0));
            }
        });
    }

    public void initializeAllViews() {
        LeftNameTV = findViewById(R.id.ISensorLeftName);
        RightNameTV = findViewById(R.id.ISensorRightName);
        LeftConnectedTV = findViewById(R.id.ISensorLeftConnectedTV);
        RightConnectedTV = findViewById(R.id.ISensorRightConnectedTV);
        LeftReadingTV = findViewById(R.id.ISensorLeftReadingTV);
        RightReadingTV = findViewById(R.id.ISensorRightReadingTV);

        ConnectLeftBtn = findViewById(R.id.ISensorsConnectLeftBtn);
        ConnectRightBtn = findViewById(R.id.ISensorsConnectRightBtn);
        LeftStartReading = findViewById(R.id.ISensorLeftStartReadingBtn);
        RightStartReading = findViewById(R.id.ISensorRightStartReadingBtn);
        LeftDisconnect = findViewById(R.id.ISensorLeftDisconnectBtn);
        RightDisconnect = findViewById(R.id.ISensorRightDisconnectBtn);

        // Initially set all TV as red
        LeftConnectedTV.setTextColor(Color.RED);
        RightConnectedTV.setTextColor(Color.RED);

        LeftReadingTV.setTextColor(Color.RED);
        RightReadingTV.setTextColor(Color.RED);

        try {
            selectedSensors = dataStore.getSelectedSensors();
            System.out.println("ISensorActivity :: selected Sensors " + selectedSensors);

            if (selectedSensors != null) {
                if (selectedSensors.size() == 1) {
                    LeftNameTV.setText(selectedSensors.get(0));
                } else {
                    LeftNameTV.setText(selectedSensors.get(0));
                    RightNameTV.setText(selectedSensors.get(1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void connectISensors(String sensorName) {
        myInterface.connectToMiPod(sensorName);
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        if (selectedSensors.size() == 1) {
            LeftConnectedTV.setTextColor(Color.GREEN);
        } else {
            if (device.getName().equals(selectedSensors.get(0))) {
                LeftConnectedTV.setTextColor(Color.GREEN);
            } else if (device.getName().equals(selectedSensors.get(1))) {
                RightConnectedTV.setTextColor(Color.GREEN);
            }
        }

        Toast.makeText(getApplicationContext(), "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
        System.out.println( "ISensorActivity :: onConnect successful with " + device.getName());
    }

    @Override
    public void onError(String message) {
        System.out.println( "ISensorActivity :: onError " + message);
//        myInterface.removeCommunicationCallback();
    }

    @Override
    public void onConnectError(String message) {
        System.out.println( "ISensorActivity :: onConnectError " + message );
//        myInterface.removeCommunicationCallback();
    }

    @Override
    public void onDisconnect(String message) {
        System.out.println( "ISensorActivity:: onDisconnect " + message );
//        myInterface.removeCommunicationCallback();
    }

    @Override
    public void onConnectionLost(final BluetoothDevice device) {
        System.out.println("ISensorActivity onConnectionLost :: device is " + device.getName());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedSensors.size() == 1) {
                    LeftConnectedTV.setTextColor(Color.RED);
                } else {
                    if (device.getName().equals(selectedSensors.get(0))) {
                        LeftConnectedTV.setTextColor(Color.RED);
                    } else if (device.getName().equals(selectedSensors.get(1))) {
                        RightConnectedTV.setTextColor(Color.RED);
                    }
                }
            }
        });

        System.out.println("ISensorActivity::Lost Communication with " + device.getName());
    }

    @Override
    public void onStartReading(BluetoothDevice device) {
        if (selectedSensors.size() == 1) {
            LeftReadingTV.setTextColor(Color.GREEN);
        } else {
            if (device.getName().equals(selectedSensors.get(0))) {
                LeftReadingTV.setTextColor(Color.GREEN);
            } else if (device.getName().equals(selectedSensors.get(1))) {
                RightReadingTV.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onStopReading(final BluetoothDevice device) {
        System.out.println("in ISensorActivity::onStopReading ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedSensors.size() == 1) {
                    LeftReadingTV.setTextColor(Color.RED);
                } else {
                    if (device.getName().equals(selectedSensors.get(0))) {
                        LeftReadingTV.setTextColor(Color.RED);
                    } else if (device.getName().equals(selectedSensors.get(1))) {
                        RightReadingTV.setTextColor(Color.RED);
                    }
                }
            }
        });

    }

}
