package com.example.bluetoothinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;

public class MainActivity extends AppCompatActivity {
    //  WHY DO WE NEED TO INSTANTIATE BTMANAGER HERE?????
    private IBluetooth myInterface = new BTManager();
    Button btn;
    TextView tv;

    public void setMyInterface(IBluetooth myInterface) {
        this.myInterface = myInterface;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = myInterface.setupBluetooth();
                tv.setText(result);
            }
        });
    }
}
