package com.example.bluetoothinterface.Objects;

import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * Created by jalil on 1/23/2018.
 */

public class Sensor {
    String name;
    String macAddress;
    String state;

    public Sensor(String inputName, String inputAddress) {
        //TODO: Here a Bluetooth Device should be passed.
        name = inputName;
        macAddress = inputAddress;
        state = "CONNECTED";
    }


    //TODO: Functions to getState and setState should be defined to change the state of sensor and obtain it when needed.
    public String getName() {
        return name;
    }

    public boolean read(String input) {
        if (input == "success"){
            ReadStream readSensor = new ReadStream("readSensorThread");
            readSensor.start();
            Handler mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    System.out.println("Data Received in Main Thread" + msg.obj);
                }
            };
            Message msg = mHandler.obtainMessage();
            msg.obj = readSensor.data;
            mHandler.sendMessage(msg);
            return true;
        }
        else {
            return false;
        }
    }


    //This is a private class that creates a new thread to read data.
    private class ReadStream implements Runnable {
        private Thread t;
        private String threadName;
        public List<Integer> data = new ArrayList<>();

        ReadStream(String name){
            threadName = name;
            System.out.println("Creating " + threadName);
        }

        @Override
        public void run() {
            Looper.prepare();
            for(int x = 0; x <= 5; x++){
                data.add(1);
            }
            Looper.loop();
        }

        public void start () {
            System.out.println("Starting Thread" + threadName );
            if (t == null) {
                t = new Thread (this, threadName);
                t.start();
            }
        }
    }
}
