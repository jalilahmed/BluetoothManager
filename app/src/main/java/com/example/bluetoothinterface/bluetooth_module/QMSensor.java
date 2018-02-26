package com.example.bluetoothinterface.bluetooth_module;

import com.example.bluetoothinterface.interfaces.IQMSensor;

import java.util.ArrayList;

/**
 * Created by jalil on 2/6/2018.
 */

class QMSensor implements IQMSensor {

    private ArrayList<DataFrameFactory> unCheckedLocalBuffer = new ArrayList<>();
    private boolean inARowLoss = false;

    public int lostFrames(ArrayList<DataFrameFactory> last5SecondsData) throws Exception {

        ArrayList<DataFrameFactory> localDataBuffer = new ArrayList<>();
        int numberOfFramesToProcess = 500; // 0 to 499 index
        int framesLost = 0;

        // Defining the necessary array sizes
        int unCheckedLocalBufferSize = unCheckedLocalBuffer.size();
        int newLocalDataSize = last5SecondsData.size();
        int availableDataSize = unCheckedLocalBufferSize + newLocalDataSize;

        if (availableDataSize < numberOfFramesToProcess) {
            throw new Exception("Not enough data for quality check");
        }

        int numberOfFramesLimitIndex = numberOfFramesToProcess - unCheckedLocalBufferSize;
        //System.out.println("Initial notChecked Frames: " + unCheckedLocalBufferSize);

        System.out.println("Incoming Size: " + newLocalDataSize + "\tStart Count: " + last5SecondsData.get(0).getCount()  +
                "\tEnd Count: " + last5SecondsData.get(newLocalDataSize - 1).getCount());
        //System.out.println("Available Data Size : " + availableDataSize);

        // Check if previously there was any buffer stored
        if (unCheckedLocalBufferSize != 0) {
            localDataBuffer.addAll(unCheckedLocalBuffer);
            unCheckedLocalBuffer.clear();
        }

        // Add only numberOfFramesToProcess limit in localData
        try {
            for (int i = 0; i < numberOfFramesLimitIndex; ++i) {
                localDataBuffer.add(last5SecondsData.get(i));
            }
            //TODO: Do the processing and callbacks from here ?
            int startCount = localDataBuffer.get(0).getCount();
            int endCount = localDataBuffer.get(localDataBuffer.size() - 1).getCount();

            framesLost = (endCount - startCount) - numberOfFramesToProcess + 1;

            // TODO: Check for lostframes in a row
            // IF more than 20 set ifMoreThan20PacketsLoss = true
            for (int i = 0; i < localDataBuffer.size() - 20; ++i) {
                int initialCount = localDataBuffer.get(i).getCount();
                int windowEndCount = localDataBuffer.get(i+19).getCount();

                System.out.println("Inside local window of 20 " + "\t Start Count: " + initialCount + "\t Window end Count: " +windowEndCount);

                if (windowEndCount - initialCount > 40) {
                    System.out.println("Found 20 packets lost in local data");
                    inARowLoss = true;
                }
            }

            //System.out.println("Quality Check : " + "Size: " + localDataBuffer.size() + "\tStart Count: " + localDataBuffer.get(0).getCount() + " \tEnd Count: " + localDataBuffer.get(localDataBuffer.size() - 1).getCount());
            //System.out.println("Algorithm frameLost is  " + framesLost + "\n\n\n");
        } catch (Exception e) {
            System.out.println("Exception in framesLost Logic " + e.toString());
        }

        // If there are more frames incoming than numberOfFramesToProcess, then store the rest in buffer for next processing loop
        try {
//            if (availableDataSize > numberOfFramesToProcess) {
                for (int i = numberOfFramesLimitIndex; i < newLocalDataSize; ++i) {
                    unCheckedLocalBuffer.add(last5SecondsData.get(i));
                }
//            }
        } catch (Exception e) {
            System.out.println("Exception in adding remaining data to buffer " + e.toString());
        }

        return framesLost;
    }

    public void clearAllBuffer() {
        unCheckedLocalBuffer.clear();
    }

    public boolean shouldDisconnect() {

        // TODO: Need better logic ?
        // Not fired when sensor was out of range
//        Date nowTime = new Date();
//        return (nowTime.getTime() - lastReadTime.getTime())/1000 >= 10000;

        return inARowLoss;
    }

    //TODO: in a row lost packages.
}
