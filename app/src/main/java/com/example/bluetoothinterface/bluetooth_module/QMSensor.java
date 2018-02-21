package com.example.bluetoothinterface.bluetooth_module;

import com.example.bluetoothinterface.interfaces.IQMSensor;

import java.util.Date;
import java.util.List;

/**
 * Created by jalil on 2/6/2018.
 */

class QMSensor implements IQMSensor {

    public int lostFrames(List<DataFrameFactory> last5SecondsData) {
        int startCount;
        int endCount;
        int lostFrames;
        int size = last5SecondsData.size();

        startCount = last5SecondsData.get(0).getCount();
        endCount = last5SecondsData.get(size - 1).getCount();
        lostFrames = endCount - (startCount + size - 1);
        float percentage = (float) (lostFrames / (size + lostFrames ) ) * 100.f;
        System.out.println("Frame Loss in percentage " + percentage);
        System.out.println("Start Count :" + startCount + "  End Count :" + endCount + "  Frame Loss " + lostFrames + "\n\n\n");

        return lostFrames;
    }


    public boolean shouldDisconnect(Date lastReadTime) {

        // TODO: Need better logic ?
        // Not fired when sensor was out of range
        Date nowTime = new Date();
        return (nowTime.getTime() - lastReadTime.getTime())/1000 >= 10;
    }
}
