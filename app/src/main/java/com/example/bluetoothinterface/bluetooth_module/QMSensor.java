package com.example.bluetoothinterface.bluetooth_module;

import com.example.bluetoothinterface.interfaces.IQMSensor;

import java.util.Date;
import java.util.List;

/**
 * Created by jalil on 2/6/2018.
 */

public class QMSensor implements IQMSensor {
    private int numberOfFramesLost;

    public int lostFrames(List<DataFrame> last5SecondsData) {
        int currentCount;
        int nextCount;
        int differenceInCounts;
//        System.out.println(last5SecondsData.size());

        for (int i = 0; i < (last5SecondsData.size() - 1); ++i) {
            currentCount = last5SecondsData.get(i).getCount();
            nextCount = last5SecondsData.get(i+1).getCount();
            differenceInCounts = nextCount - currentCount;

            if (differenceInCounts != 0) {
                numberOfFramesLost = differenceInCounts;
            }
        }

        return numberOfFramesLost;
    }


    public boolean shouldDisconnect(Date lastReadTime) {

        // TODO: Need better logic ?
        // Not fired when sensor was out of range
        Date nowTime = new Date();
        return (nowTime.getTime() - lastReadTime.getTime())/1000 >= 10;
    }
}
