package com.healthscope.healthscope;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //Request code for enabling bluetooth. The value doesn't matter. It could be 1 or a million.
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "Bluetooth Code";
    public BluetoothDevice hc06;
    public BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This code check if the user device allows Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        //This code enables Bluetooth if it isn't enabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            ArrayList devices = new ArrayList<String>();
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onCreate: " + deviceName);
                devices.add(deviceName);
                if (deviceName.equals("HC06")){
                    //Connection specific to hc06 module
                    hc06 = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
                    Toast.makeText(this, "Connected to " + hc06.getName(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!devices.contains("HC06")){
                Toast.makeText(this, "Not connected to HC06", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(this, "Please pair a device", Toast.LENGTH_SHORT).show();
        }
    }
}