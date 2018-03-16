package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IQMSensor;
import com.example.bluetoothinterface.interfaces.IQualityCheckCallback;
import com.example.bluetoothinterface.interfaces.ISensor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Prashant on 05/02/2018.
 */

class ReadStream implements Runnable {

        //  Private attributes
        private Thread readStreamThread;
        private String threadName;
        private ISensor sensor;
        private InputStream mInputStream;
        private IQMSensor QMSensor;
        private PackageToolbox packageToolbox = PackageToolbox.getInstance();
        private ICommunicationCallback communicationCB;
        private Thread.UncaughtExceptionHandler handler;

        private IQualityCheckCallback qualityCheckCB;

        //  Public Attributes
        public List<Integer> data = new ArrayList<>();

        ReadStream(Sensor mySensor,
                   BluetoothSocket mySocket,
                   Thread.UncaughtExceptionHandler onConnectionLostHandler){
            InputStream stream = null;
            BluetoothSocket socket;
            IBluetooth IBTManager = BTFactory.getInstance();
            threadName = mySensor.getName();
            sensor = mySensor;
            socket = mySocket;
            QMSensor = new QMSensor();
            communicationCB = IBTManager.getCommunicationCB();
            handler = onConnectionLostHandler;
            qualityCheckCB = IBTManager.getQualityCheckCB();
            try{
                stream = socket.getInputStream();
            }catch(Exception e) {
                System.out.println("error while getting Input stream: " + e.getMessage());
            }
            mInputStream = stream;
        }

        @Override
        public void run() {
            byte [] buffer = new byte[16384];
            int lastReadIndex;
            int notProcessedLength = 0;
            // int loopCount = 0;
            ArrayList<DataFrameFactory> localData = new ArrayList<>();
            //Date startTime = new Date();
            while (sensor.getCanRead()) {
                try {
                    SystemClock.sleep(100);
                    byte[] tmpBuffer = new byte[8192];
                    int bytes = mInputStream.read(tmpBuffer, 0, 8192);

                    System.arraycopy(tmpBuffer, 0, buffer, notProcessedLength, bytes);

                    if (bytes > 0) {
                        sensor.setLastReadTime(Calendar.getInstance().getTime());

                        lastReadIndex = packageToolbox.findDataPackages(buffer, bytes + notProcessedLength, localData);

                        if (lastReadIndex == bytes + notProcessedLength -1) {
                            notProcessedLength = 0;
                        } else {
                            byte[] notProcessed = new byte[0];
                            notProcessedLength = bytes + notProcessedLength - lastReadIndex - 1;

                            try {
                                notProcessed = Arrays.copyOfRange(buffer, lastReadIndex + 1, lastReadIndex + notProcessedLength + 1);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                            System.arraycopy(notProcessed, 0, buffer, 0, notProcessed.length);
                        }
                    }
                    /*
                    * Cross-check log with QMSensor buffer
                    * ReadStream first count should be 1 plus the last read count of QMSensor buffer
                    * This means all frames are checked in real-time (no lag)
                    * */
                    //System.out.println("ReadStream :: First read count: " + localData.get(0).getCount());

                    sortLocalDataFrames(localData);

                    checkForQuality(localData, qualityCheckCB, sensor);
                    sensor.setData(localData);
                    localData.clear();

                } catch (IOException e) {
                    //Comes here when Sensor is Out of Charge, or Out of Range!
//                    sensor.setCanRead(false);
                    try {
                        mInputStream.close();
                    } catch (IOException e1) {
                        System.out.println("Unable to Close Input Stream for thread: " + threadName);
                    }
                    // Todo: is throwing RunTimeException Good.
                    throw new RuntimeException("Sensor is Off/ Out of Charge/ Out of Range!");
                }
            }
            // Thread has stopped reading, callback for UI Thread
            if (communicationCB != null) {
                communicationCB.onStopReading(sensor.getDevice());
            }
            if(sensor.getState() != SENSOR_STATE.CONNECTED) {
                sensor.setState(SENSOR_STATE.CONNECTED);
            }
            // Clearing Quality Check buffer
            QMSensor.clearAllBuffer();
            System.out.println("Stopping Thread: " + threadName);
        }

        public void start (){
            if (readStreamThread == null) {
                readStreamThread = new Thread(this, threadName);
                sensor.setCanRead(true);
                readStreamThread.setUncaughtExceptionHandler(handler);
                readStreamThread.start();
                sensor.setState(SENSOR_STATE.READING);
                System.out.println("ReadStream :: read thread start for sensor " + sensor.getName());
                if (communicationCB != null) {
                    communicationCB.onStartReading(sensor.getDevice());
                }
            } else {
                readStreamThread.start();
            }
        }

        public Thread.State getState() {
            return readStreamThread.getState();
        }

        public void joinThread() {
            System.out.println("Call came in ReadStream.joinThread()");
            try {
                readStreamThread.join(200);
                System.out.println("I Killed the Thread: " + readStreamThread.isAlive());

            } catch (InterruptedException e) {
                System.out.println("Exception in ReadStream.joinThread for thread: " + threadName);
            }
        }

        private void checkForQuality(ArrayList<DataFrameFactory> Data, IQualityCheckCallback qualityCheckCB, ISensor sensor) {
            try {
                QMSensor.qualityCheck(Data, qualityCheckCB, sensor.getDevice());
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        private void sortLocalDataFrames(ArrayList<DataFrameFactory> unsortedLocalData) {
            Collections.sort( unsortedLocalData, new Comparator<DataFrameFactory>() {
                @Override
                public int compare(DataFrameFactory dataFrameFactory, DataFrameFactory t1) {
                    return Integer.compare(dataFrameFactory.getCount(), t1.getCount());
                }
            });
        }
}
