package com.example.helloworld2;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class MyApplication extends Application {
	private BluetoothSocket btSocket = null;
	private boolean is_bluetooth_connected = false;

	public BluetoothSocket getBluetoothSocket() {
		return btSocket;
	}

	public void setBluetoothSocket(BluetoothSocket bts) {
		btSocket = bts;
	}

	public boolean checkBTConnected() {
		return is_bluetooth_connected;
	}

	public void setBTConnected(boolean b) {
		is_bluetooth_connected=b;

	}

}
