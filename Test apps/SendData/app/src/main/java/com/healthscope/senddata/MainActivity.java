package com.healthscope.senddata;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothAdapter btAdapter;
    public BluetoothDevice hc06;
    public BluetoothSocket btSocket;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = findViewById(R.id.send);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        int k=0;
        while(k==0) {

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    if (deviceName.equals("HC-06")) { //If the device name is HC-06, the BluetoothDevice object called hc06 will be connected by it's address
                        k++;
                        hc06 = btAdapter.getRemoteDevice(deviceHardwareAddress);
                        //hc06 = device; This can be used as well, but I am not using it just to be sure
                        Toast.makeText(this, "Established connection to HC-06", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btSocket = null;
                int trys = 0;
                do {
                    try {
                        btSocket = hc06.createRfcommSocketToServiceRecord(mUUID); //Creates a direct connection to send data
                        btSocket.connect(); //Connects the socket
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    trys++;
                } while(!btSocket.isConnected() && trys < 3);

                try {
                    OutputStream outStream = btSocket.getOutputStream(); //Gets an output stream to transmit data. "Outputs" data
                    outStream.write(48); //48 is ascii for 0. Arduino reads the 48 at 0 in code.
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error writing to arduino", Toast.LENGTH_SHORT).show();
                }

                try {
                    btSocket.close(); //Closes the socket after all the data is sent. Socket can be kept open if more data needs to be sent
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}