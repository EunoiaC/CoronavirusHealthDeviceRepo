package com.healthscope.sendrecievedata;

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

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 0;
    private static final String TAG = "MainActivity";
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice hc06;
    TextInputEditText input;


    Button sendData, receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendData = findViewById(R.id.sendData);
        input = findViewById(R.id.input);
        receive = findViewById(R.id.receive);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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

        final Connection connection = new Connection(hc06, mUUID);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hc06 != null){
                    connection.connect();
                    if (connection.isConnected()){
                        connection.write(Integer.valueOf(input.getText().toString()));
                        connection.closeConnection();
                    }else{
                        Toast.makeText(MainActivity.this, "Could not connect to HC-06 module", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(MainActivity.this, "Not connected to HC-06", Toast.LENGTH_SHORT).show();
                }
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hc06 != null){
                    connection.connect();
                    if (connection.isConnected()){
                        char data = connection.read();
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Could not connect to HC-06 module", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(MainActivity.this, "Not connected to HC-06", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}