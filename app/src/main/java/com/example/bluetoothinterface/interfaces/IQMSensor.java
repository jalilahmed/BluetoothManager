package com.example.bluetoothinterface.interfaces;

import com.example.bluetoothinterface.bluetooth_module.DataFrameFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jalil on 2/6/2018.
 */

public interface IQMSensor {
    int lostFrames(ArrayList<DataFrameFactory> last5SecondsData);
    boolean shouldDisconnect(Date lastReadTime);
}
