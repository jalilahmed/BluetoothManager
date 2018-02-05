package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.example.bluetoothinterface.interfaces.ISensor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
        private byte [] buffer;
        private InputStream mInputStream;

        ReadStream(ISensor mySensor, BluetoothSocket mySocket){
            InputStream stream = null;
            threadName = mySensor.getName();
            sensor = mySensor;
            socket = mySocket;
            System.out.println("Creating " + threadName);
            try{
                stream = socket.getInputStream();
            }catch(Exception e) {
                System.out.println("error while getting Input stream: " + e.getMessage());
            }
            mInputStream = stream;
        }

        @Override
        public void run() {
            try {
                while (sensor.getState() == SENSOR_STATE.READING) {
                    byte[] tmpBuffer = new byte[2048];
                    int bytes = mInputStream.read(tmpBuffer, 0, 2048);
                    System.out.println("Read data" + tmpBuffer);
                    // ToDo: Change Binary data to Readable format.
                    // ToDo: --- From Raw Data, Separate Single Packages
                    // ToDo: --- For every Package get a DataFrame
                    sensor.setLastReadTime(Calendar.getInstance().getTime());
                    SystemClock.sleep(2000);
                }
            } catch (IOException e) {
                System.out.println("In " + threadName + "exception occurred");
            }
            System.out.println("Stopping Thread:  " + threadName);
        }

        public void start () {
            System.out.println("Starting Thread " + threadName );
            if (readStreamThread == null) {
                readStreamThread = new Thread (this, threadName);
                sensor.setState(SENSOR_STATE.READING);
                readStreamThread.start();
            }
        }

        private void findDataPackage(byte [] buffer) {

        }
}
