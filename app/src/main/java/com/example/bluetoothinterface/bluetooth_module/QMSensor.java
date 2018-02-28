package com.example.bluetoothinterface.bluetooth_module;

import android.bluetooth.BluetoothDevice;

import com.example.bluetoothinterface.interfaces.IQMSensor;
import com.example.bluetoothinterface.interfaces.IQualityCheckCallback;

import java.util.ArrayList;

/**
 * Created by jalil and prashant on 2/6/2018.
 */

class QMSensor implements IQMSensor {

    private ArrayList<DataFrameFactory> buffer = new ArrayList<>();

    public void qualityCheck(ArrayList<DataFrameFactory> latestData,
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
            float percentageLoss = lostFramesPercentage(lostFrames);
            //System.out.println("Percentage loss: " + percentageLoss);

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost(lostFrames, sensor);
                qualityCheckCallback.framesLostPercentage(percentageLoss, sensor);
            }
        }
    }

    private int lostFrames(ArrayList<DataFrameFactory> bufferData) {
        int startCount = bufferData.get(0).getCount();
        int endCount = bufferData.get(bufferData.size() - 1).getCount();
        int lostFrames = (endCount - startCount) - bufferData.size() + 1; // Because counting starts from 0

        //System.out.println("bufferData Size: " + bufferData.size() + "\t Start Count: " + startCount + "\tEnd Count: " + endCount);
        //System.out.println("Lost Frames : " + lostFrames);

        return lostFrames;
    }

    private float lostFramesPercentage(int lostFrames) {
        return (lostFrames / 500.f) * 100.f;
    }

    public void clearAllBuffer() {
        buffer.clear();
    }
}
