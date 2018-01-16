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

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    IBluetooth myInterface = new BTManager(MainActivity.this);

    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();

    Button btn, discoveryBtn;
    TextView tv, discoverytv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        discoveryBtn = findViewById(R.id.discoverBtn); discoveryBtn.setEnabled(false);
        tv = findViewById(R.id.textView);
        discoverytv = findViewById(R.id.discoverytv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup(view);
            }
        });

        discoverytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery(view);
            }
        });
    }

    public void setup (View view) {

        if (!myInterface.checkBluetooth()) {
            String result = myInterface.setupBluetooth();
            tv.setText(result);
            discoveryBtn.setEnabled(true);
        }
        else {
            myInterface.disableBluetooth();
            tv.setText("Bluetooth disabled");
        }
    }

    public void startDiscovery(View view) {
        myInterface.discoverDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "Paired device" + device.getName() + ", " + device.getAddress());
            }
        }
        else {
            Log.d(TAG, "No Paired devices found ");
        }
    }
}
