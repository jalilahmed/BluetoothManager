package com.example.bluetoothinterface.interfaces;

import com.example.bluetoothinterface.bluetooth_module.DataFrameFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by jalil on 2/6/2018.
 */

public interface IQMSensor {
    double lostFrames(List<DataFrameFactory> last5SecondsData);
    boolean shouldDisconnect(Date lastReadTime);
}
