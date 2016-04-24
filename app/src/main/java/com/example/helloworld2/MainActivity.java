package com.example.helloworld2;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer.LegendAlign;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
//import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.opencsv.CSVWriter;




public class MainActivity extends Activity {
	
	Button bRealTime,bMakeCSV,bLoadCSV,bGaugeTest;
	TextView tvTestText;
	double count=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bRealTime = (Button) findViewById(R.id.bRealTime);
		bRealTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent;
				myIntent = new Intent(getApplicationContext(), RealTimeActivity.class);
				startActivity(myIntent);

				
			}
		});
		bLoadCSV = (Button) findViewById(R.id.bLoadCSV);
		bLoadCSV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent;
				myIntent = new Intent(getApplicationContext(), LoadFileActivity.class);
				startActivity(myIntent);

				
			}
		});
        bGaugeTest = (Button) findViewById(R.id.bGaugeTest);
        bGaugeTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent;
                myIntent = new Intent(getApplicationContext(), GaugeTestActivity.class);
                startActivity(myIntent);

            }
        });
//		bMakeCSV = (Button) findViewById(R.id.bMakeCSV);
//		bMakeCSV.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
//				String format = s.format(new Date());
//				tvTestText.setText(format);
//			    File pathfile = new File(Environment.getExternalStorageDirectory()
//			            .getAbsolutePath()
//			            + File.separator
//			            + "Gilbarco");
//			    if (!pathfile.isDirectory()) {
//			        pathfile.mkdir();
//			    }
//
//			    File file = new File(pathfile,
//			                File.separator + format+".csv");
//			    if (!file.exists()) {
//			        try {
//			            file.createNewFile();
//			        } catch (IOException e) {
//			            e.printStackTrace();
//			        }
//			    }
////				String csv = Environment.getExternalStorageDirectory().toString() +"data.csv";
////				String csv =Environment.getExternalStorageDirectory().getAbsolutePath();
//				CSVWriter writer = null;
//				try {
//					writer = new CSVWriter(new FileWriter(file));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					Log.d("Zaki Baki", "It did not work");
//					e.printStackTrace();
//				}
//
//				List<String[]> data = new ArrayList<String[]>();
//				data.add(new String[] {"India", "New Delhi"});
//				data.add(new String[] {"United States", "Washington D.C"});
//				data.add(new String[] {"Germany", "Berlin"});
//				
//				writer.writeAll(data);
//
//				try {
//					writer.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		});
//		tvTestText = (TextView) findViewById(R.id.tvTestText);
		
	}
	

}
