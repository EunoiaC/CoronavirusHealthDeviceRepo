package com.healthscope.healthscope;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //UUID for the hc06 module
    public static UUID mUUID;

    //Request code for enabling bluetooth. The value doesn't matter. It could be 1 or a million.
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String TAG = "Bluetooth Code";
    public BluetoothDevice hc06;
    public BluetoothAdapter bluetoothAdapter;
    Button synchronizeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        synchronizeData = findViewById(R.id.synchronizeData);

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
        } else{
            //synchronizeData.setEnabled(true);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList devices = new ArrayList<String>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onCreate: " + deviceName);
                devices.add(deviceName);
                if (deviceName.equals("HC-06")) {
                    //Connection specific to hc06 module
                    hc06 = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
                    //Setting mUUID to the mac address of hc06
                    mUUID = UUID.fromString(deviceHardwareAddress);
                    Toast.makeText(this, "Connected to " + hc06.getName(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!devices.contains("HC-06")) {
                Toast.makeText(this, "Not connected to HC06", Toast.LENGTH_SHORT).show();
                //synchronizeData.setEnabled(false);
            }

        } else {
            Toast.makeText(this, "Please pair a device", Toast.LENGTH_SHORT).show();
            //synchronizeData.setEnabled(false);
        }

        //Trying to establish a specific socket connection to the hc06 module
        if (mUUID != null) {
            int counter = 0;
            BluetoothSocket bluetoothSocket = null;

            do {
                Log.d(TAG, "onCreate: mUUID not null");
                try {
                    bluetoothSocket = hc06.createRfcommSocketToServiceRecord(mUUID);
                    Log.d(TAG, "onCreate: " + bluetoothSocket);
                    bluetoothSocket.connect();
                    Log.d(TAG, "onCreate: " + bluetoothSocket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error establishing direct connection to hc06", Toast.LENGTH_SHORT).show();
                }
                counter++;
            } while (!bluetoothSocket.isConnected() && counter < 3);

            try {
                bluetoothSocket.close();
                Log.d(TAG, "onCreate: " + bluetoothSocket.isConnected());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onCreate: mUUID null");
            //synchronizeData.setEnabled(false);
        }

        //Checks if bluetooth went from disabled to enabled

        //Button code
        synchronizeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSynchronizing = new Intent(MainActivity.this, SynchronizedDataActivity.class);
                startActivity(startSynchronizing);
            }
        });
    }
}