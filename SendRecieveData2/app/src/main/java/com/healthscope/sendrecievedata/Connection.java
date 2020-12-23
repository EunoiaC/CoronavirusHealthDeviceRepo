package com.healthscope.sendrecievedata;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Connection {
    BluetoothSocket socket;
    BluetoothDevice device;
    UUID uuid;

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
    
    char read(){
        char input = 0;
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            inputStream.skip(inputStream.available());
            
            for (int i = 0; i < 26; i++){
                byte b = (byte) inputStream.read();
                System.out.println((char) b);
                input = (char) b;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
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
        return socket.isConnected();
    }
}
