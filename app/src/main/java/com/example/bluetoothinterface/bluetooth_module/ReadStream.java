package com.example.bluetoothinterface.bluetooth_module;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.example.bluetoothinterface.interfaces.IBluetooth;
import com.example.bluetoothinterface.interfaces.ICommunicationCallback;
import com.example.bluetoothinterface.interfaces.IQMSensor;
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
        private IQMSensor QMSensor;
        private PackageToolbox packageToolbox = PackageToolbox.getInstance();
        private Activity UIActivity;

        private ICommunicationCallback communicationCB;

        private BTManager IBTManager = BTManager.getInstance();

        ReadStream(ISensor mySensor, BluetoothSocket mySocket, Activity activity, ICommunicationCallback BTManagerCommunicationCB){
            InputStream stream = null;
            UIActivity = activity;
            threadName = mySensor.getName();
            sensor = mySensor;
            socket = mySocket;
            QMSensor = new QMSensor();
            communicationCB  = BTManagerCommunicationCB;
            try{
                stream = socket.getInputStream();
            }catch(Exception e) {
                System.out.println("error while getting Input stream: " + e.getMessage());
            }
            mInputStream = stream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[16384];
            int lastReadIndex = 0;
            int notProcessedLength = 0;
            int loopCount = 0;
            ArrayList<DataFrame> localData = new ArrayList <DataFrame>();

            try {
                Date startTime = new Date();

                while (sensor.getState() == SENSOR_STATE.READING) {

                    //TODO: Stefan Wrote this in try catch block
                    SystemClock.sleep(250);
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
                                System.out.println(e.toString());
                            }

//                          TODO Check if the replaced method of System.arraycopy is working good
//                            for (int i = 0; i < notProcessed.length; i++) {
//                                buffer[i] = notProcessed[i];
//                            }
                            System.arraycopy(notProcessed, 0, buffer, 0, notProcessed.length);
                        }
                    }

                    Date nowTime = new Date();
                    if((nowTime.getTime() - startTime.getTime())/1000 >= 5){
                        startTime = nowTime;
                        // Check for Lost Frames (Quality Check)
                        int ISensorLostFrames = QMSensor.lostFrames(localData);

                        if (ISensorLostFrames >= 50) {
                            sensor.setState(SENSOR_STATE.CONNECTED);
                            System.out.println("In ReadStream Thread " + threadName + " : Frames Lost:" +  ISensorLostFrames);
                            break;
                        }

                        sensor.setData(localData);
                    }

                    if (QMSensor.shouldDisconnect(sensor.getLastReadTime())) {
                        System.out.println("Thread " + threadName + " is breaking coz of shouldDisconnect()");
                        break;
                    }
                    // Till here, localData contains List<DataFrame>: each DataFrame has count and frame(ax,ay,az,gx,gy,gz)
                }
                // Thread has stopped reading, callback for UI Thread
                if (communicationCB != null) {
                    //TODO: Close socket and remove it from BTManager, bluetoothSockets (CHECK IF NEEDED HERE >> IF NOT_NEEDED JUST SEND A CALLBACK)
                    UIActivity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onStopReading(sensor.getDevice());
                        }
                    });
                }
            } catch (IOException e) {
                //TODO: Close socket and remove it from BTManage::bluetoothSockets, Corresponding Sensor's ISENSOR object from BTMANAGER::SensorList
                // Found exception for connection
                if (communicationCB != null) {
                    UIActivity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            communicationCB.onConnectionLost(sensor.getDevice());
                            communicationCB.onStopReading(sensor.getDevice());
                        }
                    });
                }
                System.out.println("In ReadStream Thread " + threadName + "exception occurred");
                IBTManager.closeSocket(socket, sensor);
            }
            System.out.println("Stopping Thread: " + threadName);
        }

        public void start () {
            if (readStreamThread == null) {
                readStreamThread = new Thread (this, threadName);
                sensor.setState(SENSOR_STATE.READING);
                readStreamThread.start();
            }
        }

}
