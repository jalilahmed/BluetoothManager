//package com.example.bluetoothinterface.interfaces;
//
//import android.bluetooth.BluetoothDevice;
//import android.support.test.rule.ActivityTestRule;
//
//import com.example.bluetoothinterface.ISensorActivity;
//import com.example.bluetoothinterface.bluetooth_module.BTFactory;
//import com.example.bluetoothinterface.bluetooth_module.DataFrameFactoryMock;
//
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Random;
//
///**
// * Created by Prashant on 3/26/2018.
// * Portabiles Healthcare Technologies
// */
//public class IQMSensorTest implements IQualityCheckCallback {
//    private ArrayList<DataFrameFactoryMock> localData = new ArrayList<>();
//    private IBluetooth BTInterface = BTFactory.getInstance();
//    private IQualityCheckCallback IQuality;
//    private IQMSensor QMSensor;
//
//    @Rule
//    public ActivityTestRule<ISensorActivity> ISensorActivityRule = new ActivityTestRule<>(ISensorActivity.class);
//
//    @Test
//    public void qualityCheck() throws Exception {
//        generateFrames();
//        BTInterface.setQualityCheckCB(ISensorActivityRule.getActivity());
//        IQuality = BTInterface.getQualityCheckCB();
////        System.out.println("IQMSensorTest :: IQualityCallback is " + IQuality);
////        System.out.println("IQMSensorTest :: ISensorActivityRule is " + ISensorActivityRule);
//    }
//
//    @Test
//    public void clearAllBuffer() throws Exception {
//    }
//
//    private void generateFrames() {
//        int start = 0;
//        int end = 30;
//        Random ran = new Random();
//
//        ArrayList<Integer> tempList = new ArrayList<>();
//
//        for (int i = start; i < end + 1; ++i) {
//            tempList.add(i);
//        }
//
//        for (int i = 0; i < 30; ++i) {
//            int[] frame = new int[7];
//            for (int j = 0; j < frame.length; ++j) {
//                if (j == 0) {
//                    frame[j] = tempList.get( i );
//                } else {
//                    frame[j] = j;
//                }
//            }
//
//            DataFrameFactoryMock frameMockUp = new DataFrameFactoryMock( frame );
//            localData.add(frameMockUp);
////            System.out.println("Local Data : " + localData.get(i).getCount() + ", " + localData.get(i).getAx() + ", " +
////                                localData.get(i).getAy() + ", "  + localData.get(i).getAz());
//        }
//    }
//
//    @Override
//    public void framesLost(int framesLost, BluetoothDevice device) {
//        System.out.println("Callback frames lost are : " + framesLost);
//    }
//
//    @Override
//    public void framesLostPercentage(float percentage, BluetoothDevice device) {
//        System.out.println("Callback percentage loss is : " + percentage);
//    }
//}