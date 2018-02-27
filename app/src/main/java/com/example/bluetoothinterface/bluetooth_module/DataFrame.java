package com.example.bluetoothinterface.bluetooth_module;

import java.util.Arrays;

/**
 * Created by Prashant on 05/02/2018.
 */

class DataFrame {
    private int[] frame = new int[7];

    public DataFrame(byte[] rawData) {
        frame[0] = getCount(rawData);
        ConvertA(rawData);
        ConvertG(rawData);
    }

    private int getCount(byte[] rawData) {
        // unsigned bytes
        int count = (rawData[12] & 0xff) |
                ((rawData[11] & 0xff) << 8) |
                ((rawData[10] & 0xff) << 16) |
                ((rawData[9] & 0xff) << 24);
        return count;
    }

    private void ConvertA(byte[] rawData) {
        int ax = ((rawData[14] & 0xff) | (rawData[13] << 8));
        int ay = ((rawData[16] & 0xff) | (rawData[15] << 8));
        int az = ((rawData[18] & 0xff) | (rawData[17] << 8));
        frame[1] = ax;
        frame[2] = ay;
        frame[3] = az;
    }

    private void ConvertG(byte[] rawData) {
        int gx = ((rawData[22] & 0xff) | (rawData[21] << 8));
        int gy = ((rawData[24] & 0xff) | (rawData[23] << 8));
        int gz = ((rawData[26] & 0xff) | (rawData[25] << 8));
        frame[4] = gx;
        frame[5] = gy;
        frame[6] = gz;
    }


    public int getCount() {
        return frame[0];
    }

    public  int[] getA() {
        return Arrays.copyOfRange(frame, 1, 4);
    }

    public int[] getG() {
        return Arrays.copyOfRange(frame, 4, frame.length);
    }

}
