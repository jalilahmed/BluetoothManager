package com.example.bluetoothinterface.bluetooth_module;

/**
 * Created by jalil on 2/12/2018.
 */

public class DataFrameFactory {
    private DataFrame frame;

    public DataFrameFactory(byte[] rawData) {
        frame = new DataFrame(rawData);
    }


    public int getCount() {
        return frame.getCount();
    }

    public int[] getA(){
        return frame.getA();
    }

    public int[] getG(){
        return frame.getG();
    }
}
