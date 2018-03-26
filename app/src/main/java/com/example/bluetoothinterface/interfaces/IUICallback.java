package com.example.bluetoothinterface.interfaces;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by jalil on 2/12/2018.
 */

public interface IUICallback {
    void registerDiscoveryReceiver(IntentFilter filter, BroadcastReceiver receiver);
}
