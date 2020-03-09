package com.matthewcampisi.selfi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothDataActivity extends AppCompatActivity{

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TextView infoTextView, dataTextView;
    private ImageView outputImage;
    private FloatingActionButton disconnectBtn;
    private String deviceAddress, deviceName;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device;
    private Bitmap bitmap;
    private boolean isConnected = false;
    private boolean D = false;
    private  int[] imageArr = new int[80];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_data);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        // Extract Values
        infoTextView = findViewById(R.id.bt_data_info);
        dataTextView = findViewById(R.id.bt_data_text);
        disconnectBtn = findViewById(R.id.bt_data_disconnect);
        outputImage = findViewById(R.id.bt_data_image);

        //Extract the data from the previous activity…
        deviceAddress = bundle.getString("address");
        deviceName = bundle.getString("name");

        dataTextView.setMovementMethod(new ScrollingMovementMethod());
        Arrays.fill(imageArr, 0);
        bitmap = Bitmap.createBitmap(imageArr, 10, 8, Bitmap.Config.RGB_565);
        outputImage.setImageBitmap(bitmap);

        startConnection();

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected)
                    disconnect();
                else
                    startConnection();
            }
        });

        if(D) {
            Log.d("BTT", "Device name: " + deviceName);
            Log.d("BTT", "Device address: " + deviceAddress);
        }

    }

    public void startConnection() {
        infoTextView.setText("Connecting to " + deviceName);
        disconnectBtn.setImageResource(R.drawable.ic_bluetooth_disabled);
        dataTextView.setText("");
        // Create a new thread to run the bluetooth socket
        final Runnable bluetoothSocket = new Runnable() {
            public void run() {
                setupBT();
                while(!isConnected)
                    socketConnect();
                receiveData();
            }
        };
        Thread btThread = new Thread(bluetoothSocket);
        btThread.start();
    }

    public void setupBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = bluetoothAdapter.getRemoteDevice(deviceAddress);
    }

    public boolean socketConnect() {
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
        } catch (Exception e) {
            if(D) Log.d("BTT","Error creating socket");
            isConnected = false;
        }

        try {
            socket.connect();
            isConnected = true;
            if(D) Log.d("BTT","Connected");
        } catch (IOException e) {
            if(D) Log.d("BTT",e.getMessage());
            try {
                if(D) Log.d("BTT","trying fallback...");

                socket =(BluetoothSocket) device.getClass().getMethod(
                        "createRfcommSocket",
                        new Class[] {int.class}).invoke(device,1
                );
                socket.connect();
                isConnected=true;

                if(D) Log.d("BTT","Connected");
            }
            catch (Exception e2) {
                isConnected = false;
                if(D) Log.d("BTT", "Couldn't establish Bluetooth connection!");
            }
        } finally {
            display(" ", true);
        }
        return true;
    }


    public void receiveData() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Arrays.fill(imageArr, 0);

            //noinspection InfiniteLoopStatement
            while (isConnected) {
                String data = reader.readLine();
                if(data.substring(0,2).equals("00")) {
                    Log.d("data", Arrays.toString(imageArr));
                    bitmap = Bitmap.createBitmap(imageArr, 10, 8, Bitmap.Config.RGB_565);
                    outputImage.setImageBitmap(bitmap);
                    Arrays.fill(imageArr, 0);
                }
                imageArr[Integer.parseInt(data.substring(0,2))] = Integer.parseInt(data.substring(3));
                if(D) Log.d("BTT", data);
                //display(data, false);
            }
        } catch (Exception e) {
            if(D) Log.d("BTT", "Data Error: " + e.getMessage());
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

    public void display(final String s, final boolean isSystemMessage){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isSystemMessage) {
                    if (isConnected)
                        infoTextView.setText("Connected - " + deviceName);
                    else
                        infoTextView.setText("Couldn't connect. Retrying..");
                } else {
                    dataTextView.append(s + "\n");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        disconnect();
        super.onBackPressed();
    }

    public void disconnect() {
        // connected = false; // run loop will reset connected
        infoTextView.setText("Disconnected");
        disconnectBtn.setImageResource(R.drawable.ic_bluetooth_connected);
        isConnected = false;
        if(socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }

    }
}
