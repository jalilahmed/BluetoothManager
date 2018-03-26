package com.example.bluetoothinterface;

import com.example.bluetoothinterface.interfaces.IDataHolder;
import com.example.bluetoothinterface.interfaces.ISensor;

import java.util.ArrayList;

/**
 * Created by jalil on 1/30/2018.
 */

public class DataHolder implements IDataHolder, Cloneable {
    private ArrayList<String> availableDevices = new ArrayList<>();
    private ArrayList<String> selectedSensors = new ArrayList<>();
    private ArrayList<ISensor> ISensors = new ArrayList<>();

    private DataHolder () {}

    public static DataHolder getInstance () {
        return Holder.INSTANCE;
    }

    // Singleton Holder Idiom
    private static class Holder{
        static final DataHolder INSTANCE = new DataHolder();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ArrayList<String> getAvailableSensors() {
        return availableDevices;
    }

    public void setAvailableSensors(String newDevice) {
        this.availableDevices.add(newDevice);
    }

    public ArrayList<String> getSelectedSensors() {
        return selectedSensors;
    }

    public void setSelectedSensors(String newSensor) {
        this.selectedSensors.add(newSensor);

    }

    public void setISensor(ISensor sensor) {
        ISensors.add(sensor);
    }

    public ArrayList<ISensor> getISensor(){
        return ISensors;
    }

    public void removeISensor(ISensor sensor) {
        ISensors.remove(sensor);
    }
}
