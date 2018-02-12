package com.example.bluetoothinterface.interfaces;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by jalil on 2/12/2018.
 */

public interface IUICallback {
    void startBluetooth();
    void registerReceiver(IntentFilter filter, BroadcastReceiver receiver);
}
