package com.example.helloworld2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import org.codeandmagic.android.gauge.GaugeView;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Zaki on 06-Mar-16.
 */
public class GaugeTestActivity extends Activity {
    private static final String TAG = "BTTerminal";
    BluetoothAdapter BA;
    TextView tvBTStatus;
    ArrayList list;
    final Handler handler = new Handler();
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    StringBuilder sbnew = new StringBuilder();
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];;
    EditText textToSend;
    int i = 0;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address;
    private boolean stopThread = false;
    BTData btData;
    boolean blFirstTime = true;
    ArrayList<Parameter> parameterList = new ArrayList<>();
    ArrayList<GaugeView> gaugeList = new ArrayList<>();
    ArrayList<TextView> textViewArrayList = new ArrayList<>();
    ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    int number_of_gauges;

    private GaugeView mGaugeP1,mGaugeP2,mGaugeP3,mGaugeT,mGaugeTa,mGaugeHumidity;
    private ImageView mOpto1,mOpto2,mOpto3,mOpto4;
    private TextView tvPressure1,tvPressure2,tvPressure3,tvTemperature,tvTa,tvHumidity,tvOpto1,tvOpto2,tvOpto3,tvOpto4;
    private final Random RAND = new Random();
    private int btCheckIntreval = 5000;
    private Handler btCheckHandler = new Handler();
    Long prevBTTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gauge_test_activity);
        init_elements();
        initParameters();
        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            Intent turnon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnon, 1);
            Toast.makeText(getApplicationContext(), "Turned _On", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Already On", Toast.LENGTH_LONG).show();

    }

    private void initParameters() {
        parameterList.clear();
        parameterList.add(new Parameter("Pressure 1", Color.BLUE, "bar", false));
        parameterList.add(new Parameter("Pressure 2", Color.GREEN, "bar", false));
        parameterList.add(new Parameter("Pressure 3", Color.RED, "bar", false));
        parameterList.add(new Parameter("Temperature",Color.BLACK,"degC",false));
        parameterList.add(new Parameter("Ambient T", Color.CYAN, "degC", false));
        parameterList.add(new Parameter("Humidity", Color.GRAY, "%", false));
        parameterList.add(new Parameter("Opto1", Color.MAGENTA, " ", false));
        parameterList.add(new Parameter("Opto2", Color.YELLOW, " ", false));
        parameterList.add(new Parameter("Opto3", Color.DKGRAY, " ", false));
        parameterList.add(new Parameter("Opto4", Color.WHITE, " ", false));
    }
    private void init_elements() {
        tvBTStatus = (TextView) findViewById(R.id.tvBTStatus2);
        mGaugeP1 = (GaugeView) findViewById(R.id.gauge_P1);
        mGaugeP2 = (GaugeView) findViewById(R.id.gauge_P2);
        mGaugeP3 = (GaugeView) findViewById(R.id.gauge_P3);
        mGaugeT = (GaugeView) findViewById(R.id.gauge_T);
        mGaugeTa = (GaugeView) findViewById(R.id.gauge_Ta);
        mGaugeHumidity = (GaugeView) findViewById(R.id.gauge_Humidity);
        gaugeList.add(mGaugeP1);
        gaugeList.add(mGaugeP2);
        gaugeList.add(mGaugeP3);
        gaugeList.add(mGaugeT);
        gaugeList.add(mGaugeTa);
        gaugeList.add(mGaugeHumidity);
        number_of_gauges = gaugeList.size();
        for (int i = 0; i < gaugeList.size(); i++) {
             gaugeList.get(i).setTargetValue(0);

        }
        mOpto1 = (ImageView) findViewById(R.id.ivOpto1);
        mOpto2 = (ImageView) findViewById(R.id.ivOpto2);
        mOpto3 = (ImageView) findViewById(R.id.ivOpto3);
        mOpto4 = (ImageView) findViewById(R.id.ivOpto4);
        imageViewArrayList.add(mOpto1);
        imageViewArrayList.add(mOpto2);
        imageViewArrayList.add(mOpto3);
        imageViewArrayList.add(mOpto4);
        tvPressure1 = (TextView) findViewById(R.id.tvPressure1);
        tvPressure2 = (TextView) findViewById(R.id.tvPressure2);
        tvPressure3 = (TextView) findViewById(R.id.tvPressure3);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvTa = (TextView) findViewById(R.id.tvTa);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvOpto1 = (TextView) findViewById(R.id.tvOpto1);
        tvOpto2 = (TextView) findViewById(R.id.tvOpto2);
        tvOpto3 = (TextView) findViewById(R.id.tvOpto3);
        tvOpto4 = (TextView) findViewById(R.id.tvOpto4);
        textViewArrayList.add(tvPressure1);
        textViewArrayList.add(tvPressure2);
        textViewArrayList.add(tvPressure3);
        textViewArrayList.add(tvTemperature);
        textViewArrayList.add(tvTa);
        textViewArrayList.add(tvHumidity);

    }



    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (!stopThread) {
                if (isInterrupted())
                    return;
                try {
                    // Read from the InputStream
                    int bytesAvailable = mmInStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInStream.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == 10) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                handler.post(new Runnable() {
                                    public void run() {
                                        if (data != null) {
                                            int commas = 0;
                                            for (int i = 0; i < data.length(); i++) {
                                                  if(data.charAt(i)== ',')
                                                      commas++;
                                            }

                                            if (commas == 10) {
                                                btData = new BTData(data);
                                                if (blFirstTime) {
                                                    blFirstTime = false;
                                                    prevBTTime = System.currentTimeMillis()/1000;
                                                    StartBTChecker();
                                                }
                                                for (int i = 0; i < number_of_gauges; i++) {
                                                    gaugeList.get(i).setTargetValue((float)btData.getParameter(i+1));
                                                    textViewArrayList.get(i).setText(parameterList.get(i).getName()+": "+btData.getParameter(i+1)+" "+parameterList.get(i).getUnit());

                                                }
                                                for (int i = number_of_gauges,j=0; i < number_of_gauges+imageViewArrayList.size(); i++,j++) {
                                                    if(btData.getParameter(i+1)==1.0)
                                                        imageViewArrayList.get(j).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.statuson));
                                                    else
                                                        imageViewArrayList.get(j).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.statusoff));

                                                }
                                            }

                                            new WriteAsyncTask().execute();
                                            prevBTTime = System.currentTimeMillis()/1000;



                                        }

                                    }
                                });
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }

                        }
                    }

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord",
                        new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e("MainActivity.java", "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    Runnable btChecker = new Runnable() {
        @Override
        public void run() {
            Log.d("GaugeTestActivity", "Inside BT Checker");
            btCheckHandler.postDelayed(btChecker, btCheckIntreval);
            if(System.currentTimeMillis()/1000 - prevBTTime > 5)
            {
                blFirstTime = true;
                stopThread = true;
                if (mConnectedThread != null) {
                    mConnectedThread.interrupt();
                    Log.d(TAG, "stopped thread");
                }
                mConnectedThread = null;
                try {
                    if(btSocket!=null)
                        btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btSocket = null;
                DeleteFromMyApplication();
                checkBTandSetStatus();
//                ResetVariables();
                StopBTChecker();
            }
        }
    };

    void DeleteFromMyApplication() {
        ((MyApplication) this.getApplication()).setBTConnected(false);
        ((MyApplication) this.getApplication()).setBluetoothSocket(null);
    }

    void StartBTChecker() {
        btChecker.run();
    }

    void StopBTChecker() {
        btCheckHandler.removeCallbacks(btChecker);
    }

    class WriteAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mConnectedThread.write("$");
            return null;
        }
    }


    @Override
    protected void onDestroy() {

        stopThread = true;
        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
            Log.d(TAG, "stopped thread");
        }
        mConnectedThread = null;
        StopBTChecker();
        finish();
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MyApplication) this.getApplication()).checkBTConnected()) {
            btSocket = ((MyApplication) this.getApplication()).getBluetoothSocket();
            mConnectedThread = new ConnectedThread(btSocket);
            if (!mConnectedThread.isAlive())
                mConnectedThread.start();

            stopThread = false;
            mConnectedThread.write("$");
        }
        checkBTandSetStatus();
    }

    void checkBTandSetStatus() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (btSocket != null) {
                    if (btSocket.isConnected()) {
                        tvBTStatus.setText("connected");
                        tvBTStatus.setBackgroundColor(Color.GREEN);
                    }
                } else {
                    tvBTStatus.setText("not connected");
                    tvBTStatus.setBackgroundColor(Color.RED);
                }

            }
        });

    }

}
