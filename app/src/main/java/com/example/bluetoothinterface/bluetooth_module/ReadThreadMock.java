package com.example.bluetoothinterface.bluetooth_module;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Prashant on 3/14/2018.
 * Portabiles Healthcare Technologies
 */

public class ReadThreadMock implements Runnable {

    private int start = 0;
    private int end = 29;

    @Override
    public void run() {

        while (true) {
            try {
                generateFrames();
                SystemClock.sleep(250);
                start += 30;
                end += 30;
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
    }

    private void generateFrames() {
        Random ran = new Random();
        ArrayList<DataFrameFactoryMock> localData = new ArrayList<DataFrameFactoryMock>();
        ArrayList<Integer> tempList = new ArrayList<>();

        for (int i = start; i < end+1; ++i) {
            tempList.add(i);
        }
        Collections.shuffle(tempList);

        for (int i = 0; i < 30; ++i) {
            int [] frame = new int[7];
            for (int j = 0; j < frame.length; ++j) {
                if (j == 0) {
                    frame[j] = tempList.get(i);
                } else {
                    frame[j] = j;
                }
            }

            DataFrameFactoryMock frameMockUp = new DataFrameFactoryMock(frame);
            localData.add(frameMockUp);
//            System.out.println("Local Data : " + localData.get(i).getCount() + ", " + localData.get(i).getAx() + ", " +
//                                localData.get(i).getAy() + ", "  + localData.get(i).getAz());
        }


        Collections.sort( localData, new Comparator<DataFrameFactoryMock>() {
            @Override
            public int compare(DataFrameFactoryMock dataFrameFactoryMock, DataFrameFactoryMock t1) {
                return Integer.compare(dataFrameFactoryMock.getCount(), t1.getCount());
            }
        } );

        for (int i = 0; i < localData.size(); ++i) {
            System.out.println("Local Data : " + localData.get(i).getCount());
        }

    }
}
