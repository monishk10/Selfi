package com.matthewcampisi.selfi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothDataActivity extends AppCompatActivity{

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    TextView infoTextView, dataTextView;
    Button stopBtn, disconnectBtn;
    String deviceAddress, deviceName;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device;
    boolean isRead = true;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_data);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        // Extract Values
        infoTextView = findViewById(R.id.bt_info);
        dataTextView = findViewById(R.id.dataTextView);
        stopBtn = findViewById(R.id.stop);
        disconnectBtn = findViewById(R.id.disconnect);

        dataTextView.setMovementMethod(new ScrollingMovementMethod());
        //Extract the data…
        deviceAddress = bundle.getString("address");
        deviceName = bundle.getString("name");

        infoTextView.setText("Connecting...");

        Log.d("BTT", deviceName);
        Log.d("BTT", deviceAddress);


        final Runnable bluetoothSocket = new Runnable() {
            public void run() {
                setupBT();
                socketConnect();
                receiveData();
            }
        };

        Thread bt = new Thread(bluetoothSocket);
        bt.start();

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRead ^= true;
                display("Read stopped", true);
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });

    }

    public void setupBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = bluetoothAdapter.getRemoteDevice(deviceAddress);

    }

    public boolean socketConnect() {
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
        } catch (Exception e) {
            Log.d("BTT","Error creating socket");
            isConnected = false;
        }

        try {
            socket.connect();
            isConnected = true;
            Log.d("BTT","Connected");
        } catch (IOException e) {
            Log.d("BTT",e.getMessage());
            try {
                Log.d("BTT","trying fallback...");

                socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                socket.connect();
                isConnected=true;

                Log.d("BTT","Connected");
            }
            catch (Exception e2) {
                isConnected = false;
                Log.d("BTT", "Couldn't establish Bluetooth connection!");
            }
        } finally {
            display(" ", true);
        }
        return true;
    }

    public void display(final String s, final boolean isSystemMessage){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isSystemMessage) {
                    if (isConnected)
                        infoTextView.setText("Connected");
                    else
                        infoTextView.setText("Couldn't connect");
                } else {
                    dataTextView.append(s + "\n");
                }
            }
        });
    }

    public void receiveData() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //noinspection InfiniteLoopStatement
            while (true) {
                while (isRead) {
                    String data = reader.readLine();
                    Log.d("BTT", data);
                    display(data, false);
                }
            }
        } catch (Exception e) {
            Log.d("BTT", "Data Error: " + e.getMessage());
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void disconnect() {
        // connected = false; // run loop will reset connected
        infoTextView.setText("Disconnected");
        if(socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }

    }



}
