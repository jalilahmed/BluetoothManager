package com.example.bluetoothinterface.bluetooth_module;

/**
 * Created by Prashant on 3/14/2018.
 * Portabiles Healthcare Technologies
 */

public class DataFrameFactoryMock {

    private DataFrameMock frame;

    public DataFrameFactoryMock(int[] rawData) {
        frame = new DataFrameMock(rawData);
    }

    public int getCount() {
        return frame.getCount();
    }

    public int getAx() {
        return frame.getAx();
    }

    public int getAy() {
        return frame.getAy();
    }

    public int getAz() {
        return frame.getAz();
    }
}
