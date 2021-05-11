package com.example.healthscope_v03;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;




public class MainFragment extends Fragment {

    enum SendAndReceiveDataStates{
        RECEIVE_TEMPERATURES,
        RECEIVE_RISK,
        SURVEY_REQUEST,
        NONE
    }

    private static final String TAG = "MY_APP_DEBUG_TAG";
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler my_main_handler = new Handler();
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket;
    Button retryConnection;
    float temperatures[] = new float[432];
    int temperatureCount = 0;

    MutableLiveData<Boolean> risk;

    //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    ConnectedThread my_bs;
    public LoadingDialog dialog;
    EditText inputData;
    TextView ReadView;
    TextView WriteView;
    SendAndReceiveDataStates sendAndReceiveDataState = SendAndReceiveDataStates.NONE;
    Button takeSurvey, sendDataBtn, startConnectionBtn, viewGraph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputData = getView().findViewById(R.id.Write_Text);
        takeSurvey = getView().findViewById(R.id.surveyBtn);
        sendDataBtn = getView().findViewById(R.id.button2);
        viewGraph = getView().findViewById(R.id.viewGraph);
        startConnectionBtn = getView().findViewById(R.id.startConnection);

        Log.d(TAG, "onViewCreated: " + temperatureCount + temperatures.length);

        final LayoutInflater factory = getLayoutInflater();
        final View v = factory.inflate(R.layout.connecting_dialog, null);

        risk = new MutableLiveData<>();

        risk.setValue(false); //Initilize with a value

        TextView riskView = getView().findViewById(R.id.isSafe);

        risk.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAtRisk) {
                if (isAtRisk){
                    riskView.setText("At Risk");
                    riskView.getBackground().setTint(getActivity().getColor(R.color.red));
                } else{
                    riskView.setText("Safe");
                    riskView.getBackground().setTint(getActivity().getColor(R.color.green));
                }
            }
        });

        dialog = new LoadingDialog(getActivity(), v);
        //dialog.startLoadingAlertDialog();

        retryConnection = v.findViewById(R.id.retryConnection);

        viewGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendAndReceiveDataState = SendAndReceiveDataStates.RECEIVE_TEMPERATURES;
//                Send_data("AAAAA"); //Trigger code for arduino
                ((MainActivity) getActivity()).startGraph();

            }
        });

        retryConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshConnection();
            }
        });

        startConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_connection();
            }
        });

        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_data(inputData.getText().toString());
            }
        });

        Log.d(TAG, "value of: " + String.valueOf(mmSocket));

        takeSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).startSurvey();
            }
        });

        ReadView = getView().findViewById(R.id.Read_Text);
        WriteView = getView().findViewById(R.id.Sent_Text);
        //This code check if the user device allows Bluetooth
        bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getActivity(), "This device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        //This code enables Bluetooth if it isn't enabled
        if (!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        refreshConnection();    //Establishes connection.
    }


    //hello
    /////////////////////////////////////////////////////////////////////////////////////
    //                              OnCreate ENDS HERE                                 //
    /////////////////////////////////////////////////////////////////////////////////////


    public void refreshConnection() {
        dialog.dismissDialog();
        //Device is activated (if it wasn't), and paired.
        Set<BluetoothDevice> pairedDevices;
        try{
            pairedDevices = bta.getBondedDevices();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> devices = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onCreate: " + deviceName);
                devices.add(deviceName);
                devices.add(deviceHardwareAddress);
                if (deviceName.equals("HC-06")) {
                    //Connection specific to hc06 module
                    mmDevice = bta.getRemoteDevice(deviceHardwareAddress);
                    dialog.dismissDialog();
                    //Setting mUUID to the mac address of hc06
                    Toast.makeText(getActivity(), "Connected to " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (!devices.contains("HC-06")) {
                Toast.makeText(getActivity(), "Not connected to HC-06", Toast.LENGTH_SHORT).show();
                //synchronizeData.setEnabled(false);
            }
          /*  final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,devices);
            pairedList.setAdapter(adapter);
        }*/
        } else {
            Toast.makeText(getActivity(), "No devices were found.", Toast.LENGTH_SHORT).show();
            //synchronizeData.setEnabled(false);
        }
    }

    public void start_connection() {
        // Check whether BT is on. Send request to enable it If it is off.
        if (!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        if (my_bs == null) {
            ConnectThread my_c_thread = new ConnectThread();
            new Thread(my_c_thread).start();
            Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Bluetooth connection is already completed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void connect_and_send(String send) {
        // Check whether BT is on. Send request to enable it If it is off.
        Log.d(TAG, "connect_and_send: Starting connection after survey");
        if (!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        if (my_bs == null) {
            ConnectThread my_c_thread = new ConnectThread();
            new Thread(my_c_thread).start();
            Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Bluetooth connection is already completed!", Toast.LENGTH_SHORT).show();
        }
        Send_data(send);
    }

    public void Send_data(String data) {
        if (my_bs == null) {
            Toast.makeText(getActivity(), "Bluetooth connection is not made", Toast.LENGTH_SHORT).show();
            return;
        }
        if(data.equals("T")){
            sendAndReceiveDataState = SendAndReceiveDataStates.RECEIVE_TEMPERATURES;
        }
        my_bs.write(data.getBytes());
    }


    class ConnectThread implements Runnable {
        ConnectThread() {

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

        @Override
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
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            my_bs = new ConnectedThread();
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

    class ConnectedThread extends Thread {
        byte[] mmBuffer; // mmBuffer store for the stream
        InputStream mmInStream;
        OutputStream mmOutStream;
        String str;

        ConnectedThread() {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    my_main_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            str = new String(mmBuffer, StandardCharsets.UTF_8);
                            ReadView.setText(str);
                            switch (sendAndReceiveDataState) {
                                case RECEIVE_TEMPERATURES:
                                    temperatures[temperatureCount] = Integer.parseInt(str) / 10;
                                    temperatureCount++;
                                    if (temperatureCount == temperatures.length) {
                                        temperatureCount = 0;
                                        ReadView.setText("Temperature array: " + Arrays.toString(temperatures));
                                        sendAndReceiveDataState = SendAndReceiveDataStates.NONE;
                                    }
                                    break;
                            }

                        }
                    });
                } catch (IOException e) {
                    Log.d(TAG, "Read handler failed");
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                my_main_handler.post(new Runnable() {
                    @Override
                    public void run() {
                        str = new String(bytes, StandardCharsets.UTF_8);
                        WriteView.setText(str);
                    }
                });
            } catch (IOException e) {
                Log.d(TAG, "Write handler failed");
            }
        }
    }
}

