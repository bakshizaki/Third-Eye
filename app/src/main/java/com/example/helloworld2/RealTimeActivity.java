package com.example.helloworld2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.helloworld2.SingleListDialog.GetResultDialogListner;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer.LegendAlign;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.opencsv.CSVWriter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;

public class RealTimeActivity extends Activity implements OnClickListener, GetResultDialogListner,OnDataPointTapListener {
    private static final String TAG = "BTTerminal";
    Button bSendData, bListDevices;
    ListView lv;
    BluetoothAdapter BA;
    private DialogFragment singlechoice;
    ProgressDialog prog;
    TextView received_data, tvBTStatus;
    int selected_item;
    ArrayList list;
    boolean is_device_selected = false;

    ArrayList mList = new ArrayList();
    ArrayList paired_addresses = new ArrayList();
    ArrayList discovered_addresses = new ArrayList();
    Handler handler = new Handler();
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
    LineGraphSeries<DataPoint> seriesP1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> seriesP2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> seriesP3 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> seriesT = new LineGraphSeries<DataPoint>();
    long xCount = 0;
    GraphView graph;
    Button bPause, bScale, bStartRecording, bStopRecording, bSelectPara;
    boolean isPaused = false, isScaled = true, isRecording = false, blFirstTime = true;
    String currFile;
    File csvfile;
    List<BTData> pausedBTData = new ArrayList<BTData>();
    CSVWriter writer = null;
    double firstTime,currentTime;
    ArrayList<Parameter> parameterList = new ArrayList<>();
    private MultiSpinner spinner;
    private ArrayAdapter<String> adapter;
    boolean showPressure1 = true, showPressure2 = true, showPressure3 = false, showTemperature = false;
    CharSequence[] parameters;
    ArrayList<CharSequence> selectedParameter = new ArrayList<CharSequence>();
    BTData btData;
    Long prevBTTime;
    private int btCheckIntreval = 5000;
    private Handler btCheckHandler = new Handler();
    RequestQueue queue;
    String url ="http://128.199.166.20/ge.php?";
    String[] parametersTitle;
    TextView tvRecordingStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.realtimeactivity);
        initParameters();
        init_elements();
        queue = Volley.newRequestQueue(this);
        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            Intent turnon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnon, 1);
            Toast.makeText(getApplicationContext(), "Turned _On", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Already On", Toast.LENGTH_LONG).show();

        graph = (GraphView) findViewById(R.id.rtgraph);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        parameters = new CharSequence[parameterList.size()];
        for(int i=0;i<parameterList.size();i++) {
            parameters[i]=parameterList.get(i).getName();
        }

        for(int i=0;i<parameterList.size();i++) {
            parameterList.get(i).series.setOnDataPointTapListener(this);
        }

        for(int i=0;i<parameterList.size();i++) {
            if(parameterList.get(i).isVisible()) {
                selectedParameter.add(parameterList.get(i).getName());
                graph.addSeries(parameterList.get(i).series);
            }
        }
		setLegendsAndTap();
    }

    void ResetVariables() {
        paired_addresses = new ArrayList();
        handler = new Handler();
        readBufferPosition = 0;
        readBuffer = new byte[1024];;

    }

    private void initParameters() {
        parameterList.clear();
        parameterList.add(new Parameter("Pressure 1", Color.BLUE, "bar", true));
        parameterList.add(new Parameter("Pressure 2", Color.GREEN, "bar", true));
        parameterList.add(new Parameter("Pressure 3", Color.RED, "bar", false));
        parameterList.add(new Parameter("Temperature", Color.BLACK, "degC", false));
        parameterList.add(new Parameter("Ambient T", Color.CYAN, "degC", false));
        parameterList.add(new Parameter("Humidity", Color.GRAY, "%", false));
        parameterList.add(new Parameter("Opto1", Color.MAGENTA, " ", false));
        parameterList.add(new Parameter("Opto2", Color.YELLOW, " ", false));
        parameterList.add(new Parameter("Opto3", Color.DKGRAY, " ", false));
        parameterList.add(new Parameter("Opto4", Color.WHITE, " ", false));
    }


    private void init_elements() {
        // TODO Auto-generated method stub
        bListDevices = (Button) findViewById(R.id.bListDevices);
        bPause = (Button) findViewById(R.id.bPause);
        bScale = (Button) findViewById(R.id.bScale);
        // bSendData = (Button) findViewById(R.id.bSend);
        // received_data=(TextView) findViewById(R.id.tvReceived);
        tvBTStatus = (TextView) findViewById(R.id.tvBTStatus);
        // textToSend=(EditText) findViewById(R.id.etTextToSend);
        // received_data.setMovementMethod(new ScrollingMovementMethod());
        bListDevices.setOnClickListener(this);
        bPause.setOnClickListener(this);
        bScale.setOnClickListener(this);
        // bSendData.setOnClickListener(this);
        bStartRecording = (Button) findViewById(R.id.bStartRecordinng);
        bStartRecording.setOnClickListener(this);
        bStopRecording = (Button) findViewById(R.id.bStopRecordinng);
        bStopRecording.setOnClickListener(this);
        bSelectPara = (Button) findViewById(R.id.bSelectPara);
        bSelectPara.setOnClickListener(this);
        tvRecordingStatus = (TextView) findViewById(R.id.tvRecordingStatus);
        tvRecordingStatus.setBackgroundColor(Color.RED);
    }

    void setLegendsAndTap() {
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendAlign.TOP);
        for(int i=0;i<parameterList.size();i++) {
            parameterList.get(i).series.setOnDataPointTapListener(this);
            parameterList.get(i).series.setTitle(parameterList.get(i).getName());
        }
    }

    protected void onChangeSelectedParameters() {
        graph.removeAllSeries();

        for(int i=0;i<parameterList.size();i++) {
            if(selectedParameter.contains(parameterList.get(i).getName())) {
                if(parameterList.get(i).series == null || btData == null) {
                    selectedParameter.remove(parameterList.get(i).getName());
                    Toast.makeText(getApplicationContext(),"No data to display in "+parameterList.get(i).getName(),Toast.LENGTH_SHORT).show();
                }
                else {
                    parameterList.get(i).setVisible(true);
                    parameterList.get(i).series.appendData(new DataPoint(btData.getTime() - firstTime, btData.getParameter(i + 1)), true, 120);
                    graph.addSeries(parameterList.get(i).series);
                }
            } else {
                parameterList.get(i).setVisible(false);
                graph.removeSeries(parameterList.get(i).series);
                parameterList.get(i).series = new LineGraphSeries<DataPoint>();
                parameterList.get(i).series.setColor(parameterList.get(i).getColour());
            }
        }

        setLegendsAndTap();
    }

    protected void showSelectParaDialog() {
        boolean[] checkedParemeters = new boolean[parameters.length];
        int count = parameters.length;

        for (int i = 0; i < count; i++)
            checkedParemeters[i] = selectedParameter.contains(parameters[i]);

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    selectedParameter.add(parameters[which]);
                else
                    selectedParameter.remove(parameters[which]);

                onChangeSelectedParameters();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Parameters");
        builder.setMultiChoiceItems(parameters, checkedParemeters, coloursDialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void ConnectToDevice() {
        i++;
        if (i == 2)
            i = 0;
        BluetoothDevice device = BA.getRemoteDevice(address);
        Log.d(TAG, "Inside function");
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "In onResume, socket creation failed", Toast.LENGTH_LONG).show();
        }
        BA.cancelDiscovery();
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                if(btSocket!=null) {
                    btSocket.close();
                    Log.d(TAG, "Closing btSocket");
                }
                return;
            } catch (IOException e2) {
                Toast.makeText(getApplicationContext(), "Unable to close socket during socket failure",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");
        if (btSocket == null)
            Log.d(TAG, "btSocket is null");
        else
            Log.d(TAG, "btSocket is not null");
        if (btSocket.isConnected()) {
            ((MyApplication) this.getApplication()).setBluetoothSocket(btSocket);
            ((MyApplication) this.getApplication()).setBTConnected(true);
            checkBTandSetStatus();

        }
        stopThread = false;


        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write("$");
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
                                            Log.d(TAG,"Data Received");
                                            int commas = 0;
                                            for (int i = 0; i < data.length(); i++) {
                                                if(data.charAt(i)== ',')
                                                    commas++;
                                            }

                                            if (commas == 10) {
                                                if (isPaused == false) {
                                                    xCount++;
                                                    btData = new BTData(data);
                                                    if (blFirstTime) {
                                                        firstTime = btData.getTime();
                                                        blFirstTime = false;
                                                        prevBTTime = System.currentTimeMillis()/1000;
                                                        StartBTChecker();
                                                    }
                                                    String urlParameters = url;
                                                    urlParameters += "p1=";
                                                    urlParameters += String.valueOf((btData.getParameter(1)));
                                                    urlParameters += "&";
                                                    urlParameters += "p2=";
                                                    urlParameters += String.valueOf((btData.getParameter(2)));
                                                    urlParameters += "&";
                                                    urlParameters += "p3=";
                                                    urlParameters += String.valueOf((btData.getParameter(3)));
                                                    urlParameters += "&";
                                                    urlParameters += "t=";
                                                    urlParameters += String.valueOf((btData.getParameter(4)));
                                                    urlParameters += "&";
                                                    urlParameters += "ta=";
                                                    urlParameters += String.valueOf((btData.getParameter(5)));
                                                    urlParameters += "&";
                                                    urlParameters += "h=";
                                                    urlParameters += String.valueOf((btData.getParameter(6)));
                                                    urlParameters += "&";
                                                    urlParameters += "o1=";
                                                    urlParameters += String.valueOf((btData.getParameter(7)));
                                                    urlParameters += "&";
                                                    urlParameters += "o2=";
                                                    urlParameters += String.valueOf((btData.getParameter(8)));
                                                    urlParameters += "&";
                                                    urlParameters += "o3=";
                                                    urlParameters += String.valueOf((btData.getParameter(9)));
                                                    urlParameters += "&";
                                                    urlParameters += "o4=";
                                                    urlParameters += String.valueOf((btData.getParameter(10)));
                                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlParameters, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.d("RealTimeActivity","Response is: "+ response);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d("RealTimeActivity","Request Failed");
                                                        }
                                                    });
                                                    queue.add(stringRequest);

                                                        currentTime = btData.getTime();
                                                    for(int i=0;i<parameterList.size();i++) {

                                                        if(parameterList.get(i).isVisible())
                                                            try {
                                                            parameterList.get(i).series.appendData(new DataPoint(btData.getTime()-firstTime,btData.getParameter(i+1)),true,120); }
                                                            catch (Exception e) {

                                                            }
                                                    }

                                                    if (isRecording) {
                                                        openCSVFile();
                                                        writeCSVFile(btData);
                                                    }

                                                    if (isScaled) {
                                                        graph.getViewport().setXAxisBoundsManual(true);
                                                        if (xCount < 50) {
                                                            graph.getViewport().setMinX(1);
                                                            graph.getViewport().setMaxX(50);
                                                        } else {
                                                            graph.getViewport().setMinX(xCount - 48);
                                                            graph.getViewport().setMaxX(xCount + 2);

                                                        }
                                                        isScaled = false;
                                                    }

                                                } else {
                                                    pausedBTData.add(new BTData(data));
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

    Runnable btChecker = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Inside BT Checker");
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
                ResetVariables();
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bListDevices:
                Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
                list = new ArrayList();
                for (BluetoothDevice bt : pairedDevices) {
                    list.add(bt.getName());
                    paired_addresses.add(bt.getAddress());
                }
                Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_LONG).show();

                singlechoice = new SingleListDialog("Paired Devices", (String[]) list.toArray(new String[list.size()]));
                singlechoice.show(getFragmentManager(), "Iamhopeless");

                break;
            case R.id.bPause:
                isPaused = !isPaused;
                if (isPaused)
                    bPause.setText("Resume");
                else {
                    bPause.setText("Pause");
                    while (!pausedBTData.isEmpty()) {
                        xCount++;
                        for(int i=0;i<parameterList.size();i++) {
                            if(parameterList.get(i).isVisible())
                                parameterList.get(i).series.appendData(new DataPoint(pausedBTData.get(0).getTime()-firstTime,pausedBTData.get(0).getParameter(i+1)),true,120);
                        }

                        pausedBTData.remove(0);
                    }
                }
                break;
            case R.id.bScale:
                isScaled = !isScaled;
                break;
            case R.id.bStartRecordinng:
                tvRecordingStatus.setText("Recording");
                tvRecordingStatus.setBackgroundColor(Color.GREEN);
                Log.d("Recording","Recording Started");
                Toast.makeText(getApplicationContext(),"Recording Started",Toast.LENGTH_SHORT);
                isRecording = true;
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                String format = s.format(new Date());
                currFile = format;
                openCSVFile();
                writeCSVTitle();
                break;
            case R.id.bStopRecordinng:
                tvRecordingStatus.setText("Not Recording");
                tvRecordingStatus.setBackgroundColor(Color.RED);
                isRecording = false;
                break;
            case R.id.bSelectPara:
                showSelectParaDialog();
                break;
        }
    }

    public void openCSVFile() {
        File pathfile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Gilbarco");
        if (!pathfile.isDirectory()) {
            pathfile.mkdir();
        }

        csvfile = new File(pathfile, File.separator + currFile + ".csv");
        if (!csvfile.exists()) {
            try {
                csvfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void writeCSVFile(BTData btd) {

        try {
            writer = new CSVWriter(new FileWriter(csvfile, true));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("Zaki Baki", "It did not work");
            e.printStackTrace();
        }
        String[] s1 = { String.valueOf(BTData.round((btd.getTime() - firstTime),1)) };
        String[] s2 =  ArrayUtils.addAll(s1, btd.getCSVData());
        // String[]
        // s={String.valueOf(btd.getTime()-firstTime),btd.getCSVData()};
        writer.writeNext(s2);
        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Inside write function", Toast.LENGTH_SHORT);

    }

    public void writeCSVTitle() {
        try {
            writer = new CSVWriter(new FileWriter(csvfile, true));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("Zaki Baki", "It did not work");
            e.printStackTrace();
        }
        parametersTitle = new String[parameterList.size()+1];
        parametersTitle[0] = "Time";
        for (int i = 0; i < parameterList.size(); i++) {
            parametersTitle[i+1] = parameterList.get(i).getName();

        }
        writer.writeNext(parametersTitle);
        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogFinish(int selected) {
        selected_item = selected;
        if(paired_addresses.size() == 0)
            return;
        if (btSocket != null) {
            if (btSocket.isConnected()) {
                Toast.makeText(getApplicationContext(),"Already Connected",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        address = paired_addresses.get(selected_item).toString();

        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
            mConnectedThread = null;
        }
        new ConnectAsyncTask().execute();
        is_device_selected = true;
        graph.removeAllSeries();

        for(int i=0;i<parameterList.size();i++) {
            if(selectedParameter.contains(parameterList.get(i).getName())) {
                parameterList.get(i).setVisible(true);
                parameterList.get(i).series = new LineGraphSeries<DataPoint>();
                parameterList.get(i).series.setColor(parameterList.get(i).getColour());
                try {
                    graph.addSeries(parameterList.get(i).series);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        setLegendsAndTap();
    }

    class ConnectAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            prog = ProgressDialog.show(RealTimeActivity.this, "Connecting...", "Please Wait");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            prog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ConnectToDevice();
            return null;
        }

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
    public void onTap(Series s, DataPointInterface d) {
        for(int i=0;i<parameterList.size();i++) {
            if(s==parameterList.get(i).series) {
                Toast.makeText(getApplicationContext(), "Time: "+BTData.round(d.getX(),1)+" "+parameterList.get(i).getName()+": "+d.getY()+parameterList.get(i).getUnit(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
