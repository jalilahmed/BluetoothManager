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
            byte[] buffer = new byte[4096];
            int lastReadIndex = 0;
            int notProcessedLength = 0;

            int loopCount = 0;
            ArrayList<DataFrame> localData = new ArrayList<DataFrame>();

            try {
                while (sensor.getState() == SENSOR_STATE.READING) {
                    SystemClock.sleep(250);
                    byte[] tmpBuffer = new byte[2048];
                    int bytes = mInputStream.read(tmpBuffer, 0, 2048);
                    System.out.println("Read data" + tmpBuffer);
                    System.arraycopy(tmpBuffer, 0, buffer, notProcessedLength, bytes);
                    lastReadIndex = findDataPackages(buffer, bytes + notProcessedLength, localData);
                    // ToDo: Change Binary data to Readable format.
                    // ToDo: --- From Raw Data, Separate Single Packages
                    // ToDo: --- For every Package get a DataFrame
                    sensor.setLastReadTime(Calendar.getInstance().getTime());
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

    private int findDataPackages(byte[] buffer, int endIndex, ArrayList<DataFrame> localData) {
        //Log.d("BluetoothExample", "findDataPackages: endIndex: " + endIndex);
        // DebugBuffer("findDataPackages", buffer, endIndex + 1);
        int pos;
        if (endIndex < 0 || endIndex > buffer.length - 1) {
             System.out.println("findNextDataStartPosition: Index out of Range.");
        }

        int lastFrameStart = 0;

        // -27 because the last frame may not be complete
        for (int currentIndex = 0; currentIndex < endIndex - 27; currentIndex++) {
            pos=0;
            // find the possible start position
            // find 0x02
            if (buffer[currentIndex] != 2) {
                //Log.d("BluetoothExample", "findDataPackage: currentIndex: "+ currentIndex + ": no 02");
                continue;
            }
            // find 0x52 (82d)
            if (buffer[currentIndex + 1] != 82) {
                //Log.d("BluetoothExample", "findDataPackage: currentIndex: "+ currentIndex + ": no 82");
                continue;
            }
            // find 0x0F (15d)
            if (buffer[currentIndex + 2] != 15) {
                //Log.d("BluetoothExample", "findDataPackage: currentIndex: "+ currentIndex + ": no 15");
                continue;
            }

            // check for CRC
            int d = (97 + buffer[currentIndex + 3] + buffer[currentIndex + 4]) % 256 ;
            if (buffer[currentIndex + 5] != d) {
                //Log.d("BluetoothExample", "findDataPackage: currentIndex: "+ currentIndex + ": wrong CRC");
                continue;
            }

            // check for 0x03
            if (buffer[currentIndex+27] != 3) {
                //Log.d("BluetoothExample", "findDataPackage: currentIndex: "+ currentIndex + ": no 03");
                continue;
            }

            // currentIndex ist start index of new frame now
            // copyOfRange(source, from, to)
            byte[] DATA = Arrays.copyOfRange(buffer, currentIndex, currentIndex + 28);

            try {
                //mCallback.SendData(new SensorDataFrame(DATA));
                DataFrame frame = new DataFrame(DATA);
                System.out.println("chck data: " + frame);
                //Log.d("BluetoothExample", "currentIndex: " + String.valueOf(currentIndex) + ", count: " + String.valueOf(frame.count));
                localData.add(frame);
            } catch(Exception e) {
                Log.d("BluetoothExample", "Error sendData: " + e.getMessage());
            }
            lastFrameStart = currentIndex;

            // next possible start of frame position (for-loop will add 1 to currentIndex)
            currentIndex += 27;
        }

        // return the index of the end of the last read package.
        return lastFrameStart + 27;
    }
}
