package com.example.healthscope_v03;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;




public class MainFragment extends Fragment {

    private static final String TAG = "MY_APP_DEBUG_TAG";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final Handler my_main_handler = new Handler();
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket=null;
    double[] temperatures = new double[72];
    int temperatureCount = 0;
    public boolean connectionEstablished=false;
    public boolean paired = false;


    MutableLiveData<Boolean> risk;

    //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    ConnectedThread my_bs;

    Button takeSurvey;
    Button viewGraph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint({"SetTextI18n", "NewApi"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        takeSurvey = requireView().findViewById(R.id.surveyBtn);
        viewGraph = requireView().findViewById(R.id.viewGraph);

        Log.d(TAG, "onViewCreated: " + temperatureCount + temperatures.length);

        risk = new MutableLiveData<>();

        risk.setValue(false); //Initialize with a value

        TextView riskView = requireView().findViewById(R.id.isSafe);

        risk.observe(requireActivity(), isAtRisk -> {
            if (isAtRisk){
                riskView.setText("You are at Risk");
               // riskView.getBackground().setTint(requireActivity().getColor(R.color.red));
            } else{
                riskView.setText("You are Safe");
               //riskView.getBackground().setTint(requireActivity().getColor(R.color.green));
            }
        });


        viewGraph.setOnClickListener(v1 -> Send_data("T"));

        Log.d(TAG, "value of: " + mmSocket);

        takeSurvey.setOnClickListener(v12 -> ((MainActivity) requireActivity()).startSurvey());

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
        InitializeBluetooth();    //Bluetooth settings.
        new Handler().postDelayed(this::StartConnection, 2500);

    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                              OnCreate ENDS HERE                                 //
    /////////////////////////////////////////////////////////////////////////////////////

    public void InitializeBluetooth() {
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
                if (deviceName.equals("HC-06" )||deviceName.equals("HC-05")) {
                    //Connection specific to hc06 module
                    mmDevice = bta.getRemoteDevice(deviceHardwareAddress);
                    //Setting mUUID to the mac address of hc06
                    Toast.makeText(getActivity(), "Paired with " + mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    paired = true;
                    break;
                }
            }

            if (!devices.contains("HC-06") && !devices.contains("HC-05")) {
                Toast.makeText(getActivity(), "Not paired with Bluetooth module", Toast.LENGTH_SHORT).show();
                paired = false;
            }
        } else {
            Toast.makeText(getActivity(), "No devices were found.", Toast.LENGTH_SHORT).show();
                paired = false;
        }
    }

    public void StartConnection() {
        // Check whether BT is on. Send request to enable it If it is off.
        if (!bta.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        if (my_bs == null && paired) {
            ConnectThread my_c_thread = null;
            try {
                my_c_thread = new ConnectThread();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (my_c_thread != null){
                new Thread(my_c_thread).start();
                connectionEstablished = true;
                Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
            } else{
                connectionEstablished = false;
            }
//            /*
//            if(Connection established condition){
//                connectionEstablished = true;
//                Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
//            }
//            */
//            connectionEstablished = true;   //Remove this if above works.
        } else if(my_bs == null && !paired){
            Toast.makeText(getActivity(), "Please pair your phone with your necklace and run the app again.", Toast.LENGTH_SHORT).show();
        } else if (connectionEstablished){
            Toast.makeText(getActivity(), "Bluetooth connection is already made.", Toast.LENGTH_SHORT).show();
        }

    }

    public void Send_data(String data) {
        if (my_bs == null) {
            Toast.makeText(getActivity(), "Bluetooth connection is not made", Toast.LENGTH_SHORT).show();
            return;
        }
        my_bs.write(data.getBytes());
    }


    class ConnectThread implements Runnable {
        ConnectThread() throws IOException{

            BluetoothSocket tmp = null;
            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
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
        public void run() { // Changed part !!!!!!!!!!!!!!!!!!!!!!
            mmBuffer = new byte[73];
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    mmInStream.read(mmBuffer,0,73);
                    if(mmBuffer[0]=='T') { // TEMP DATA COMING
                        my_main_handler.post(() -> {
                            for (int i = 0; i < 72; i++) {
                                temperatures[i] = (mmBuffer[i + 1] & 0xff)*0.0588 + 25;
                            }
                            ((MainActivity) requireActivity()).startGraph();
                        });
                    }
                    else if(mmBuffer[0]=='R') { // RISK came
                        if (mmBuffer[1] == '1') { // If risk is 1 show it to the user, o.w. do nothing
                            my_main_handler.post(() -> risk.setValue(true));
                        }
                    }
                    else if(mmBuffer[0]=='S') { // survey request
                        my_main_handler.post(() -> {
                            ((MainActivity) requireActivity()).startSurvey();  // Go to the survey interface
                        });
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Read handler failed");
                }
            }
        }


        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG, "Write handler failed");
            }
        }
    }
}

