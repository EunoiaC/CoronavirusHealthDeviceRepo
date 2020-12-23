package com.healthscope.sendrecievedata;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Connection {
    private BluetoothSocket socket;
    private BluetoothDevice device;
    private UUID uuid;
    private Boolean connected;

    public Connection(BluetoothDevice device, UUID uuid){
        this.device = device;
        this.uuid = uuid;
    }

    void connect(){
        socket = null;
        int counter = 0;
        do {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                System.out.println(socket);
                socket.connect();
                System.out.println(socket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }while (!socket.isConnected() && counter < 3);
        if (socket.isConnected()){
            connected = true;
        } else{
            connected = false;
        }
    }
    
    void write (int num){
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            outputStream.write(num);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    String read(int charsReceived){
        char input = 0;
        String receivedString = "";
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            inputStream.skip(inputStream.available());

            for (int i = 0; i < charsReceived; i++){
                byte b = (byte) inputStream.read();
                System.out.println((char) b);
                input = (char) b;
                receivedString = receivedString + input;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivedString;
    }

    void closeConnection(){
        try {
            socket.close();
            System.out.println(socket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Boolean isConnected(){
        return connected;
    }
}
