package com.example.bluetoothinterface.bluetooth_module;

import java.util.List;

/**
 * Created by Prashant on 05/02/2018.
 */

public class DataFrame {
    private List<Integer> frame;
    private Integer count;

    public DataFrame(byte[] rawData) {
        count = getCount(rawData);
        ConvertA(rawData);
        ConvertG(rawData);
    }


    private Integer getCount(byte [] rawData){
        // unsigned bytes
        count = (rawData[12] & 0xff) |
                ((rawData[11] & 0xff) << 8) |
                ((rawData[10] & 0xff) << 16) |
                ((rawData[9] & 0xff) << 24);
        return count;
    }

    private void ConvertA(byte [] rawData){
        Integer ax = ((rawData[14] & 0xff) | (rawData[13] << 8));
        Integer ay = ((rawData[16] & 0xff) | (rawData[15] << 8));
        Integer az = ((rawData[18] & 0xff) | (rawData[17] << 8));
        frame.add(ax);
        frame.add(ay);
        frame.add(az);
    }
    private void ConvertG(byte [] rawData){
        Integer gx = ((rawData[22] & 0xff) | (rawData[21] << 8));
        Integer gy = ((rawData[24] & 0xff) | (rawData[23] << 8));
        Integer gz = ((rawData[26] & 0xff) | (rawData[25] << 8));
        frame.add(gx);
        frame.add(gy);
        frame.add(gz);
    }

}
