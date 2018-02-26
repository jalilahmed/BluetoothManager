package com.example.bluetoothinterface.bluetooth_module;

import com.example.bluetoothinterface.interfaces.IQMSensor;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jalil on 2/6/2018.
 */

class QMSensor implements IQMSensor {

    private ArrayList<DataFrameFactory> buffer = new ArrayList<>();

    public int lostFrames(ArrayList<DataFrameFactory> last5SecondsData) {

        ArrayList<DataFrameFactory> localData = new ArrayList<>();
        int framesLost = 0;

        int numberOfFramesToProcess = 500; // 0 to 499 index
        int bufferSize = buffer.size();
        int incomingDataSize = last5SecondsData.size();
        int availableDataSize = bufferSize + incomingDataSize;
//        System.out.println("Initial Buffer Size: " + bufferSize);

//        System.out.println("Incoming Size: " + incomingDataSize + "\tEnd Count: " + last5SecondsData.get(incomingDataSize - 1).getCount());
//        System.out.println("Available Data Size : " + availableDataSize);

        // Check if previously there was any buffer stored
        if (bufferSize != 0) {
            localData.addAll(buffer);
            buffer.clear();
        }

        // Add only numberOfFramesToProcess limit in localData
        int numberOfFramesLimitIndex = numberOfFramesToProcess - bufferSize - 1;
//        System.out.println("Last possible index for Frames limit in local Data " + numberOfFramesLimitIndex);
        try {
            for (int i = 0; i < numberOfFramesLimitIndex; ++i) {
                localData.add(last5SecondsData.get(i));
            }
            int startCount = localData.get(0).getCount();
            int endCount = localData.get(localData.size() - 1).getCount();

            framesLost = endCount - startCount - numberOfFramesToProcess ;
//            System.out.println("Available Data Size was : " + availableDataSize);
//            System.out.println("LocalData : \tStart Count: " + localData.get(0).getCount() + " \tEnd Count: " + localData.get(localData.size() - 1).getCount());
            System.out.println("Every 5 seconds lostFrames is " + framesLost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If there are more than numberOfFramesToProcess in 5 second data, then store the rest in buffer for next processing loop
        try {
            if (availableDataSize > numberOfFramesToProcess) {
                for (int i = numberOfFramesLimitIndex + 1; i < incomingDataSize; ++i) {
                    buffer.add(last5SecondsData.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("5 second data size : " + size + "\n buffer size : " + bufferSize + "\n Local Data size : " + localData.size() + "\n\n");


//        int lostFrames = 0;
//
//        for (int i = 0; i < 500; i+=20) {
//
//
//            int localStartCount = localData.get(i).getCount();
//            int localEndCount = localData.get(i + 100).getCount();
//
//            int localLostFrames = localEndCount - (localStartCount + 100 - 1);
//
//            lostFrames += localLostFrames;
//
//        }
//        int startCount;
//        int endCount;
//
//
//        startCount = localData.get(0).getCount();
//        endCount = localData.get(size - 1).getCount(); // because indexing is from 0
//        lostFrames = endCount - (startCount + size - 1);
//
//        for (int i = 0; i < size; ++i) {
//            int count = localData.get(i).getCount();
//            System.out.println(count + "\n");
//        }

//        System.out.println("Size : " + size + "   Start Count :" + startCount + "  End Count :" + endCount + "  Frame Loss " + lostFrames + "\n\n\n");

        return framesLost;
    }

    public boolean shouldDisconnect(Date lastReadTime) {

        // Not fired when sensor was out of range
        Date nowTime = new Date();
        return (nowTime.getTime() - lastReadTime.getTime())/1000 >= 10000;
    }
}
