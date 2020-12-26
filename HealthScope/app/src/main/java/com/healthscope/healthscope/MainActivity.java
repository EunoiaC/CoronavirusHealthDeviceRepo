package com.healthscope.healthscope;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //UUID for the hc06 module
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Request code for enabling bluetooth. Should be nonzero positive.
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String TAG = "Bluetooth Code";
    public BluetoothDevice hc06;
    public BluetoothAdapter bluetoothAdapter;
    Button pairButton;
    ListView listPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listPaired = findViewById(R.id.pair_list);
        pairButton = findViewById(R.id.button_pair);

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

        ArrayList<String> devices = new ArrayList<>();
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
                    //mUUID = UUID.fromString(deviceHardwareAddress);
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
        }

        pairButton.setOnClickListener(v -> {
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,devices);
            listPaired.setAdapter(adapter);
        });


    }


}

