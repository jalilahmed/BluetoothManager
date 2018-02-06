package com.example.bluetoothinterface.interfaces;

import com.example.bluetoothinterface.bluetooth_module.DataFrame;

import java.util.Date;
import java.util.List;

/**
 * Created by jalil on 2/6/2018.
 */

public interface IQMSensor {
    int lostFrames(List<DataFrame> last5SecondsData);
    boolean shouldDisconnect(Date lastReadTime);
}
