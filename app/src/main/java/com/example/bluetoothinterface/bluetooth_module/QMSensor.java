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
                            BluetoothDevice sensor) {

        buffer.addAll(latestData);

        if (buffer.size() <= 500) { // Just quality check, no removing and padding

            int lostFrames = lostFrames( buffer );

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost( lostFrames, sensor );
            }
        } else { // Once buffer reaches size >= 500, remove old to make size 500 again

            int extraFrames = (buffer.size() - 500);

            // Removing extra frames already processed previously
            for (int i = 0; i < extraFrames; ++i) {
                buffer.remove(0);
            }

            int lostFrames = lostFrames(buffer);
            float percentageLoss = lostFramesPercentage(lostFrames);

            if (qualityCheckCallback != null) {
                qualityCheckCallback.framesLost(lostFrames, sensor);
                qualityCheckCallback.framesLostPercentage(percentageLoss, sensor);
            }
        }
        /*
        * Cross-check log with ReadStream
        * ReadStream first count should be 1 plus the last read count of QMSensor buffer
        * This means all frames are checked in real-time (no lag)
        * */
        //System.out.println("QMSensor :: Buffer last read count: " + buffer.get(buffer.size()-1).getCount());
    }

    private int lostFrames(ArrayList<DataFrameFactory> bufferData) {
        int startCount = bufferData.get(0).getCount();
        int endCount = bufferData.get(bufferData.size() - 1).getCount();
        int lostFrames = (endCount - startCount) - bufferData.size() + 1; // Because counting starts from 0

        lostFramesInARowCheck(bufferData);

        //System.out.println("bufferData Size: " + bufferData.size() + "\t Start Count: " + startCount + "\tEnd Count: " + endCount);
        //System.out.println("Lost Frames : " + lostFrames);

        return lostFrames;
    }

    private float lostFramesPercentage(int lostFrames) {
        return (lostFrames / 500.f) * 100.f;
    }

    private void lostFramesInARowCheck(ArrayList<DataFrameFactory> bufferData) {

        for (int i = 0; i < bufferData.size() - 1; ++i) {
            int nextCount = bufferData.get(i+1).getCount();
            int currentCount = bufferData.get(i).getCount();

            if (nextCount - currentCount != 1) {
                int missingFramesInRow = nextCount - currentCount - 1;

                if (missingFramesInRow > 20) {
                    //TODO: callback for inARowLoss?
                    System.out.println("Found more than 20 frames missing in a row");
                }
            }
        }
    }

    public void clearAllBuffer() {
        buffer.clear();
    }
}
