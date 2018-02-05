package com.example.bluetoothinterface.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalil on 2/5/2018.
 */

public interface IDataHolder {
    List<String> getAvailableDevices();
    void setAvailableDevices(String newDevice);
}
