package com.example.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.phct.btmanagerlibrary.interfaces.IBluetooth;
import com.phct.btmanagerlibrary.interfaces.ICommunicationCallback;
import com.phct.btmanagerlibrary.interfaces.IDataHolder;
import com.phct.btmanagerlibrary.interfaces.IQualityCheckCallback;
import com.phct.btmanagerlibrary.interfaces.ISensor;
import com.phct.btmanagerlibrary.module.BTFactory;
import com.phct.btmanagerlibrary.module.DataHolder;

import java.util.ArrayList;

public class ISensorActivity extends AppCompatActivity implements ICommunicationCallback, IQualityCheckCallback {

    // UI Elements
    TextView LeftNameTV, RightNameTV, LeftConnectedTV, RightConnectedTV, LeftReadingTV, RightReadingTV, QualityCheckLeft, QualityCheckRight;
    TextView LeftFramesLost, RightFramesLost, LeftLossPercent, RightLossPercent;
    Button ConnectLeftBtn, ConnectRightBtn, LeftStartReading, RightStartReading, LeftDisconnect, RightDisconnect;

    // Sensors Names
    ArrayList<String> selectedSensors = new ArrayList<>();

    // Bluetooth objects
    IBluetooth myInterface = BTFactory.getInstance();
    private IDataHolder dataStore = DataHolder.getInstance();

    // ISensor Object
    ArrayList<ISensor> iSensorList = dataStore.getISensor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_isensor );
        // Setting Callback
        myInterface.setCommunicationCB(this);
        System.out.println("ISensor activity :: this : " + this);
        myInterface.setQualityCheckCB(this);

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
                myInterface.startReadingManually( iSensorList.get(0));
            }
        });

        RightStartReading.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.startReadingManually( iSensorList.get(1));
            }
        });

        LeftDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading( iSensorList.get(0));
            }
        });

        RightDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.stopReading( iSensorList.get(1));
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

        QualityCheckLeft = findViewById(R.id.LeftQualityCheckTV);
        QualityCheckRight = findViewById(R.id.RightQualityCheckTV);

        LeftFramesLost = findViewById(R.id.LeftFramesLostTV);
        RightFramesLost = findViewById(R.id.RightFramesLostTV);
        LeftLossPercent = findViewById(R.id.LeftLossPercentTV);
        RightLossPercent = findViewById(R.id.RightLossPercentTV);

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
        System.out.println("ISensor Activity :: on Connection lost");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedSensors.size() == 1) {
                    LeftConnectedTV.setTextColor(Color.RED);
                    LeftReadingTV.setTextColor(Color.RED);
                    LeftFramesLost.setText(0);
                } else {
                    if (device.getName().equals(selectedSensors.get(0))) {
                        LeftConnectedTV.setTextColor(Color.RED);
                        LeftReadingTV.setTextColor(Color.RED);
                        LeftFramesLost.setText(0);
                    } else if (device.getName().equals(selectedSensors.get(1))) {
                        RightConnectedTV.setTextColor(Color.RED);
                        RightReadingTV.setTextColor(Color.RED);
                        RightFramesLost.setText(0);
                    }
                }
            }
        });

        System.out.println("\n" + "ISensorActivity::Lost Communication with " + device.getName());
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
        System.out.println("\n in ISensorActivity::onStopReading ");
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

    @Override
    public void framesLost(final int framesLost, final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String updateTextView = framesLost + "";
                if (selectedSensors.size() == 1) {
                    LeftFramesLost.setText(updateTextView);
                    if (framesLost < 5) {
                        QualityCheckLeft.setTextColor(Color.GREEN);
                    } else if (framesLost < 10){
                        QualityCheckLeft.setTextColor(Color.YELLOW);
                    } else {
                        QualityCheckLeft.setTextColor(Color.RED);
                    }
                } else {
                    if (device.getName().equals(selectedSensors.get(0))) {
                        LeftFramesLost.setText(updateTextView);
                        if (framesLost < 5) {
                            QualityCheckLeft.setTextColor(Color.GREEN);
                        } else if (framesLost < 10){
                            QualityCheckLeft.setTextColor(Color.YELLOW);
                        } else {
                            QualityCheckLeft.setTextColor(Color.RED);
                        }
                    } else if (device.getName().equals(selectedSensors.get(1))) {
                        RightFramesLost.setText(updateTextView);
                        if (framesLost < 5) {
                            QualityCheckRight.setTextColor(Color.GREEN);
                        } else if (framesLost < 10){
                            QualityCheckRight.setTextColor(Color.YELLOW);
                        } else {
                            QualityCheckRight.setTextColor(Color.RED);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void framesLostPercentage(final float percentage, final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String updateTextView = percentage + "";
                if (selectedSensors.size() == 1) {
                    LeftLossPercent.setText(updateTextView);
                } else {
                    if (device.getName().equals(selectedSensors.get(0))) {
                        LeftLossPercent.setText(updateTextView);
                    } else if (device.getName().equals(selectedSensors.get(1))) {
                        RightLossPercent.setText(updateTextView);
                    }
                }
            }
        });
    }

    @Override
    public void framesLostInARow(boolean inARowLoss, BluetoothDevice device) {}

}
