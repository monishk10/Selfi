package com.matthewcampisi.selfi;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

class SerialSocket implements Runnable {

    private static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final BroadcastReceiver disconnectBroadcastReceiver;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private boolean connected;

    SerialSocket() {
        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }

    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */
    void connect(BluetoothDevice device) throws IOException {
        if(connected || socket != null)
            throw new IOException("already connected");
        this.device = device;
        Executors.newSingleThreadExecutor().submit(this);
    }

    void disconnect() {
        // connected = false; // run loop will reset connected
        if(socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }

    }


    @Override
    public void run() { // connect & read
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
            return;
        }
        connected = true;
        try {
            byte[] buffer = new byte[1024];
            int len;
            //noinspection InfiniteLoopStatement
            while (true) {
                len = socket.getInputStream().read(buffer);
                byte[] data = Arrays.copyOf(buffer, len);
                Log.d("BTT", new String(data));
            }
        } catch (Exception e) {
            connected = false;
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

}