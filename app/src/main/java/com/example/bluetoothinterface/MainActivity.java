package com.example.bluetoothinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;

public class MainActivity extends AppCompatActivity {

    Button btn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup(view);
            }
        });
    }

    public void setup (View view) {
        IBluetooth myInterface = new BTManager(MainActivity.this);
        if (!myInterface.checkBluetooth()) {
            String result = myInterface.setupBluetooth(this);
            tv.setText(result);
        }
        else {
            myInterface.disableBluetooth();
            tv.setText("Bluetooth disabled");
        }
    }
}
