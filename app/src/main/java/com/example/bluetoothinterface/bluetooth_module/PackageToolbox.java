package com.example.bluetoothinterface.bluetooth_module;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jalil on 2/6/2018.
 */

class PackageToolbox {
    private PackageToolbox () {}

    public static PackageToolbox getInstance () {
        return Holder.INSTANCE;
    }

    // Singleton Holder Idiom
    private static class Holder{
        static final PackageToolbox INSTANCE = new PackageToolbox();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int findDataPackages(byte[] buffer, int endIndex, ArrayList<DataFrameFactory> localData) {
        int pos;
        if (endIndex < 0 || endIndex > buffer.length - 1) {
            System.out.println("findNextDataStartPosition: Index out of Range.");
        }

        int lastFrameStart = 0;

        for (int currentIndex = 0; currentIndex < endIndex - 27; currentIndex++) {
            pos=0;

            if (buffer[currentIndex] != 2) {
                continue;
            }

            if (buffer[currentIndex + 1] != 82) {
                continue;
            }

            if (buffer[currentIndex + 2] != 15) {
                continue;
            }

            int d = (97 + buffer[currentIndex + 3] + buffer[currentIndex + 4]) % 256 ;
            if (buffer[currentIndex + 5] != d) {
                continue;
            }

            if (buffer[currentIndex+27] != 3) {
                continue;
            }

            byte[] DATA = Arrays.copyOfRange(buffer, currentIndex, currentIndex + 28);

            try {
                DataFrameFactory frame = new DataFrameFactory(DATA);
                localData.add(frame);
            } catch(Exception e) {
                System.out.println("findDataPackages::Error sendData: " + e.getMessage());
            }
            lastFrameStart = currentIndex;

            currentIndex += 27;
        }

        return lastFrameStart + 27;
    }
}
