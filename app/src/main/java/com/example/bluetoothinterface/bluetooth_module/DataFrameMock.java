package com.example.bluetoothinterface.bluetooth_module;

/**
 * Created by Prashant on 3/14/2018.
 * Portabiles Healthcare Technologies
 */

public class DataFrameMock {

    private int[] frame = new int[7];

    public DataFrameMock(int[] rawData) {
        frame[0] = rawData[0];

        frame[1] = rawData[1];
        frame[2] = rawData[2];
        frame[3] = rawData[3];

        frame[4] = rawData[4];
        frame[5] = rawData[5];
        frame[6] = rawData[6];
    }

    public int getCount() {
        return frame[0];
    }

    public int getAx() {
        return frame[1];
    }

    public int getAy() {
        return frame[2];
    }

    public int getAz() {
        return frame[3];
    }
}
