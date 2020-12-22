package com.example.healthscope_v03;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    //public final static String MODULE_MAC=null ;
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket;             //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    ConnectThread my_c_thread = null;
    ConnectedThread my_bs=null;
    Handler       my_main_handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This code check if the user device allows Bluetooth
        bta= BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        //This code enables Bluetooth if it isn't enabled
        if (!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } /*else{
            synchronizeData.setEnabled(true);
        }*/

        //Device is activated (if it wasn't), and paired.
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();

        ArrayList<String> devices = new ArrayList<>();
        if (pairedDevices.size() > 0)
        {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onCreate: " + deviceName);
                devices.add(deviceName);
                devices.add(deviceHardwareAddress);
                if (deviceName.equals("HC-06")) {
                    //Connection specific to hc06 module
                    mmDevice= bta.getRemoteDevice(deviceHardwareAddress);
                    //Setting mUUID to the mac address of hc06
                    Toast.makeText(this, "Connected to " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (!devices.contains("HC-06")) {
                Toast.makeText(this, "Not connected to HC-06", Toast.LENGTH_SHORT).show();
                //synchronizeData.setEnabled(false);
            }
          /*  final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,devices);
            pairedList.setAdapter(adapter);
        }*/

        else
        {
            Toast.makeText(this, "Please pair a device", Toast.LENGTH_SHORT).show();
            //synchronizeData.setEnabled(false);
        }
    }
 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            ConnectThread my_connection_thread = new ConnectThread();
            my_connection_thread.ConnectThread();
        }*/
    }

    public void start_connection(View view) {
        /*if (!bta.isEnabled()) { // This does not solve not paired case
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
       my_main_handler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == my_bs.MESSAGE_READ){
                    String read_message = (String)msg.obj;
                    TextView textView = findViewById(R.id.Read_Text);
                    textView.setText(read_message);
                }

                else if(msg.what == my_bs.MESSAGE_WRITE){
                    String write_message = (String)msg.obj;
                    TextView textView = findViewById(R.id.Sent_Text);
                    textView.setText(write_message);
                }

                else if(msg.what == my_bs.MESSAGE_TOAST){
                    String toast_message = (String)msg.obj;
                    toast_message= "Could not send the data "+ toast_message;
                    TextView textView = findViewById(R.id.Sent_Text);
                    textView.setText(toast_message);
                }
            }
        };
        my_c_thread = new ConnectThread();
        my_c_thread.start();
    }

    public void Send_data (View view){
        if(my_bs==null) {
            Toast.makeText(this, "Bluetooth connection is not made", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText editText = (EditText) findViewById(R.id.Write_Text);
        String data = editText.getText().toString();
        my_bs.write(data.getBytes());
    }


    private class ConnectThread extends Thread {
        public ConnectThread() {

            BluetoothSocket tmp = null;
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bta.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            my_bs= new ConnectedThread(mmSocket, my_main_handler);
            my_bs.start();
        }
        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}

    class ConnectedThread extends Thread {
        private static final String TAG = "MY_APP_DEBUG_TAG";
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private Handler handler;
        public ConnectedThread(BluetoothSocket socket, Handler handler_temp) {
            mmSocket = socket;
            handler = handler_temp;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MESSAGE_WRITE, -1, -1, bytes);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }