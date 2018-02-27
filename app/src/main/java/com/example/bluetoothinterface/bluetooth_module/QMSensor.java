package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothDevice;

import com.example.bluetoothinterface.interfaces.IQMSensor;
import com.example.bluetoothinterface.interfaces.IQualityCheckCallback;

import java.util.ArrayList;

/**
 * Created by jalil and prashant on 2/6/2018.
 */

class QMSensor implements IQMSensor {

    private ArrayList<DataFrameFactory> unCheckedLocalBuffer = new ArrayList<>();
    private ArrayList<DataFrameFactory> buffer = new ArrayList<>(500);
    private boolean inARowLoss = false;

    public void qualityCheck(ArrayList<DataFrameFactory> latestData,
                             IQualityCheckCallback qualityCheckCallback,
                             BluetoothDevice sensor) throws Exception {

        ArrayList<DataFrameFactory> localDataBuffer = new ArrayList<>();
        int numberOfFramesToProcess = 500; // 0 to 499 index
        int framesLost = 0;

        // Defining the necessary array sizes
        int unCheckedLocalBufferSize = unCheckedLocalBuffer.size();
        int newLocalDataSize = latestData.size();
        System.out.println("Incoming Data size " + newLocalDataSize);
        int availableDataSize = unCheckedLocalBufferSize + newLocalDataSize;

        if (availableDataSize < numberOfFramesToProcess) {
            throw new Exception("Not enough data for quality check");
        }

        int numberOfFramesLimitIndex = numberOfFramesToProcess - unCheckedLocalBufferSize;
        //System.out.println("Initial notChecked Frames: " + unCheckedLocalBufferSize);

//        System.out.println("Incoming Size: " + newLocalDataSize + "\tStart Count: " + last5SecondsData.get(0).getCount()  +
//                "\tEnd Count: " + last5SecondsData.get(newLocalDataSize - 1).getCount());
        //System.out.println("Available Data Size : " + availableDataSize);

        // Check if previously there was any buffer stored
        if (unCheckedLocalBufferSize != 0) {
            localDataBuffer.addAll(unCheckedLocalBuffer);
            unCheckedLocalBuffer.clear();
        }

        // Add only numberOfFramesToProcess limit in localData
        try {
            for (int i = 0; i < numberOfFramesLimitIndex; ++i) {
                localDataBuffer.add(latestData.get(i));
            }
            //TODO: Do the processing and callbacks from here ?
            int startCount = localDataBuffer.get(0).getCount();
            int endCount = localDataBuffer.get(localDataBuffer.size() - 1).getCount();

            framesLost = (endCount - startCount) - numberOfFramesToProcess + 1;

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost(framesLost, sensor);
            }

//            System.out.println("Quality Check : " + "Size: " + localDataBuffer.size() + "\tStart Count: " + localDataBuffer.get(0).getCount() + " \tEnd Count: " + localDataBuffer.get(localDataBuffer.size() - 1).getCount());
            System.out.println("Algorithm frameLost is  " + framesLost + "\n\n\n");
        } catch (Exception e) {
            System.out.println("Exception in framesLost Logic " + e.toString());
        }

        // If there are more frames incoming than numberOfFramesToProcess, then store the rest in buffer for next processing loop
        try {
//            if (availableDataSize > numberOfFramesToProcess) {
                for (int i = numberOfFramesLimitIndex; i < newLocalDataSize; ++i) {
                    unCheckedLocalBuffer.add(latestData.get(i));
                }
//            }
        } catch (Exception e) {
            System.out.println("Exception in adding remaining data to buffer " + e.toString());
        }
    }

    public void clearAllBuffer() {
        unCheckedLocalBuffer.clear();
    }

    public void qualityCheckTest(ArrayList<DataFrameFactory> latestData,
                            IQualityCheckCallback qualityCheckCallback,
                            BluetoothDevice sensor) throws Exception {

        buffer.addAll(latestData);
        //System.out.println("Incoming Data Size" + latestData.size());

        if (buffer.size() <= 500) {

            int lostFrames = lostFrames(buffer);

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost(lostFrames, sensor);
            }
        } else {

            int extraFrames = (buffer.size() - 500);

            for (int i = 0; i < extraFrames; ++i) {
                buffer.remove(0);
            }

            int lostFrames = lostFrames(buffer);

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost(lostFrames, sensor);
            }
        }
    }

    public boolean shouldDisconnect() {
        return inARowLoss;
    }

    private int lostFrames(ArrayList<DataFrameFactory> bufferData) {
        int startCount = bufferData.get(0).getCount();
        int bufferEndCount = bufferData.get(bufferData.size() - 1).getCount();
        int lostFrames = (bufferEndCount - startCount) - bufferData.size();

        System.out.println("bufferData Size: " + bufferData.size() + "\t Start Count: " + startCount + "\tEnd Count: " + bufferEndCount);
        System.out.println("Lost Frames : " + lostFrames);

        return lostFrames;
    }
}
