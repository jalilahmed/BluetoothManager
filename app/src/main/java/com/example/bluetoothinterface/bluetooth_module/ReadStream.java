package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothSocket;

import com.example.bluetoothinterface.interfaces.ISensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Prashant on 05/02/2018.
 */

public class ReadStream implements Runnable{
        private Thread readStreamThread;
        private String threadName;
        private ISensor sensor;
        private BluetoothSocket socket;
        public List<Integer> data = new ArrayList<>();

        ReadStream(ISensor mySensor, BluetoothSocket mySocket){
            threadName = mySensor.getName();
            sensor = mySensor;
            socket = mySocket;
            System.out.println("Creating " + threadName);
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (sensor.getState() == SENSOR_STATE.READING) {
                    String data = input.readLine();
                    Date date = Calendar.getInstance().getTime();
                    sensor.setLastReadTime(date);
                    // SystemClock.sleep(2000);
                }
            } catch (IOException e) {
                sensor.setState(SENSOR_STATE.CONNECTED);
                System.out.println("In " + threadName + "exception occurred");
            }
            System.out.println("in ReadStream " + threadName + " " + sensor);
        }

        public void start () {
            System.out.println("Starting Thread" + threadName );
            if (readStreamThread == null) {
                readStreamThread = new Thread (this, threadName);
                sensor.setState(SENSOR_STATE.READING);
                readStreamThread.start();
            }
        }
}
