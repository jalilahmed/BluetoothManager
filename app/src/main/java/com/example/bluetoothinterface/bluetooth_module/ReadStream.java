package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import com.example.bluetoothinterface.interfaces.ISensor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
        private byte [] buffer;
        private InputStream mInputStream;

        ReadStream(ISensor mySensor, BluetoothSocket mySocket){
            InputStream stream = null;
            threadName = mySensor.getName();
            sensor = mySensor;
            socket = mySocket;
            try{
                stream = socket.getInputStream();
            }catch(Exception e) {
                System.out.println("error while getting Input stream: " + e.getMessage());
            }
            mInputStream = stream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[4096];
            int lastReadIndex = 0;
            int notProcessedLength = 0;
            int loopCount = 0;
            ArrayList<DataFrame> localData = new ArrayList<DataFrame>();

            try {
                Date startTime = new Date();

                while (sensor.getState() == SENSOR_STATE.READING) {

                    //TODO: Stefan Wrote this in try catch block
                    SystemClock.sleep(250);
                    byte[] tmpBuffer = new byte[2048];
                    int bytes = mInputStream.read(tmpBuffer, 0, 2048);

                    System.arraycopy(tmpBuffer, 0, buffer, notProcessedLength, bytes);

                    if (bytes > 0) {
                        lastReadIndex = findDataPackages(buffer, bytes + notProcessedLength, localData);

                        if (lastReadIndex == bytes + notProcessedLength -1) {
                            notProcessedLength = 0;
                        } else {
                            byte[] notProcessed = new byte[0];
                            notProcessedLength = bytes + notProcessedLength - lastReadIndex - 1;

                            try {
                                notProcessed = Arrays.copyOfRange(buffer, lastReadIndex + 1, lastReadIndex + notProcessedLength + 1);
                            } catch(Exception e) {
                                System.out.println(e.toString());
                            }

                            for (int i = 0; i < notProcessed.length; i++) {
                                buffer[i] = notProcessed[i];
                            }
                        }
                    }

                    Date nowTime = new Date();
                    if((nowTime.getTime() - startTime.getTime())/1000 >= 5){
                        sensor.setData(localData);
                        startTime = nowTime;
                    }
                    // Till here, localData contains List<DataFrame>: each DataFrame has count and frame(ax,ay,az,gx,gy,gz)
                    sensor.setLastReadTime(Calendar.getInstance().getTime());
                }
            } catch (IOException e) {
                System.out.println("In ReadStream Thread " + threadName + "exception occurred");
            }
        }

        public void start () {
            if (readStreamThread == null) {
                readStreamThread = new Thread (this, threadName);
                sensor.setState(SENSOR_STATE.READING);
                readStreamThread.start();
            }
        }

        //TODO: Shift to a new file
        private int findDataPackages(byte[] buffer, int endIndex, ArrayList<DataFrame>localData) {
        int pos;
        if (endIndex < 0 || endIndex > buffer.length - 1) {
            System.out.println("findNextDataStartPosition: Index out of Range.");
        }

        int lastFrameStart = 0;

        for (int currentIndex = 0; currentIndex < endIndex - 27; currentIndex++) {
            pos=0;

            if (buffer[currentIndex] != 2) {
                continue;
            }

            if (buffer[currentIndex + 1] != 82) {
                continue;
            }

            if (buffer[currentIndex + 2] != 15) {
                continue;
            }

            int d = (97 + buffer[currentIndex + 3] + buffer[currentIndex + 4]) % 256 ;
            if (buffer[currentIndex + 5] != d) {
                continue;
            }

            if (buffer[currentIndex+27] != 3) {
                continue;
            }

            byte[] DATA = Arrays.copyOfRange(buffer, currentIndex, currentIndex + 28);

            try {
                DataFrame frame = new DataFrame(DATA);
                localData.add(frame);
            } catch(Exception e) {
                System.out.println("findDataPackages::Error sendData: " + e.getMessage());
            }
            lastFrameStart = currentIndex;

            currentIndex += 27;
        }

        return lastFrameStart + 27;
    }
}
