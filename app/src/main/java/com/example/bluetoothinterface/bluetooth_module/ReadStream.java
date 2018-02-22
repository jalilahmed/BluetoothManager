package com.example.bluetoothinterface.bluetooth_module;

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

class ReadStream implements Runnable{

        //  Private attributes
        private Thread readStreamThread;
        private String threadName;
        private ISensor sensor;
        private BluetoothSocket socket;
        private InputStream mInputStream;
        private IQMSensor QMSensor;
        private PackageToolbox packageToolbox = PackageToolbox.getInstance();
        private ICommunicationCallback communicationCB;

        //  Public Attributes
        public List<Integer> data = new ArrayList<>();

        // BTManger Instance
        private IBluetooth IBTManager = BTManager.getInstance();

        ReadStream(Sensor mySensor,BluetoothSocket mySocket,  ICommunicationCallback BTManagerCommunicationCB){
            InputStream stream = null;
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
            byte [] buffer = new byte[16384];
            int lastReadIndex = 0;
            int notProcessedLength = 0;
            int loopCount = 0;
            ArrayList<DataFrameFactory> localData = new ArrayList<>();
            Date startTime = new Date();

            while (sensor.getState() == SENSOR_STATE.READING) {

                try {
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
                                //Todo: going in this exception :: Reaction??
                                System.out.println("Exception in copying of range " + e.toString());
                            }
                            System.arraycopy(notProcessed, 0, buffer, 0, notProcessed.length);
                        }
                    }

                    Date nowTime = new Date();
                    if((nowTime.getTime() - startTime.getTime())/1000 >= 5){
                        startTime = nowTime;
                        // Check for Lost Frames (Quality Check)
                        //TODO:: Check Logic Here. if the percentage is working good.
                        int ISensorLostFrames = QMSensor.lostFrames(localData);

                        if (communicationCB != null) {
                            communicationCB.onFramesLost(ISensorLostFrames, sensor.getDevice());
                        }

                        if (ISensorLostFrames >= 50000) {
                            sensor.setState(SENSOR_STATE.CONNECTED);
                            System.out.println("In ReadStream Thread " + threadName + " : Frames Lost:" +  ISensorLostFrames);
                            break;
                        }

                        sensor.setData(localData);
                        localData.clear();
                    }

                    if (QMSensor.shouldDisconnect(sensor.getLastReadTime())) {
                        System.out.println("Thread " + threadName + " is breaking coz of shouldDisconnect()");
                        break;
                    }
                    // Till here, localData contains List<DataFrame>: each DataFrame has count and frame(ax,ay,az,gx,gy,gz)
                } catch (IOException e) {
                    if (communicationCB != null) {
                        communicationCB.onConnectionLost(sensor.getDevice());
                        communicationCB.onStopReading(sensor.getDevice());
                    }
                    System.out.println("In ReadStream Thread " + threadName + "exception occurred");
                    IBTManager.closeSocket(socket, sensor);
                }
            }
            // Thread has stopped reading, callback for UI Thread
            if (communicationCB != null) {
                //TODO: Close socket and remove it from BTManager, bluetoothSockets (CHECK IF NEEDED HERE >> IF NOT_NEEDED JUST SEND A CALLBACK)
                communicationCB.onStopReading(sensor.getDevice());
            }
            if(sensor.getState() != SENSOR_STATE.CONNECTED) {
                sensor.setState(SENSOR_STATE.CONNECTED);
            }
            System.out.println("Stopping Thread: " + threadName);
        }

        public void start () {
            if (readStreamThread == null) {
                readStreamThread = new Thread (this, threadName);
                sensor.setState(SENSOR_STATE.READING);
                readStreamThread.start();
                System.out.println("ReadStream :: read thread start for sensor " + sensor.getName());

                if (communicationCB != null) {
                    communicationCB.onStartReading(sensor.getDevice());
                }
            }
        }

        public Thread.State getState() {
            return readStreamThread.getState();
        }

}
