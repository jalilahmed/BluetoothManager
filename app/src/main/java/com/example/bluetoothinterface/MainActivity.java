package com.example.bluetoothinterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    IBluetooth myInterface = new BTManager(MainActivity.this);

    Button btn, discoveryBtn;
    TextView tv, discoverytv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        discoveryBtn = findViewById(R.id.discoverBtn);
        tv = findViewById(R.id.textView);
        discoverytv = findViewById(R.id.discoverytv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup(view);
            }
        });

        discoveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gotBack = startDiscovery();
                discoverytv.setText(gotBack);
            }
        });
    }

    public void setup (View view) {
        if (!myInterface.checkBluetooth()) {
            String result = myInterface.setupBluetooth();
            tv.setText(result);
        }
        else {
            myInterface.disableBluetooth();
            tv.setText("Bluetooth disabled");
        }
    }

    public String startDiscovery() {
        ArrayList<BluetoothDevice> bondedDevices =  myInterface.discoverDevices();
        System.out.println("startDiscovery" + bondedDevices.toString());
        return "Went into startDiscovery";
    }
}
