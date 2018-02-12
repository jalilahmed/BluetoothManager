package com.example.bluetoothinterface.bluetooth_module;

import com.example.bluetoothinterface.bluetooth_module.BTManager;
import com.example.bluetoothinterface.interfaces.IBluetooth;

/**
 * Created by jalil on 2/12/2018.
 */

public class BTFactory {

     public static IBluetooth getInstance() {
         return BTManager.getInstance();
     }




}
