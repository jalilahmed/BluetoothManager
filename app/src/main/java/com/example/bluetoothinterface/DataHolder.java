package com.example.bluetoothinterface;

import com.example.bluetoothinterface.bluetooth_module.BTManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jalil on 1/30/2018.
 */

public class DataHolder implements Cloneable {
    private List<String> availableDevices = new ArrayList<>();

    private DataHolder () {}

    public static DataHolder getInstance () {
        return Holder.INSTANCE;
    }

    // Singleton Holder Idiom
    private static class Holder{
        static final DataHolder INSTANCE = new DataHolder();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<String> getAvailableDevices() {
        return availableDevices;
    }

    public void setAvailableDevices(String data) {
        this.availableDevices.add(data);
    }
}
