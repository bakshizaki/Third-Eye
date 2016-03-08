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
		GraphView graph = (GraphView) findViewById(R.id.graph);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
		          new DataPoint(10, 50),
		          new DataPoint(11, 20),
		          new DataPoint(12, 45),
		          new DataPoint(13, 55),
		          new DataPoint(14, 10),
		          new DataPoint(15, 25),
		          new DataPoint(16, 15),
		          new DataPoint(17, 30),
		          new DataPoint(18, 5)
		});
		graph.addSeries(series);
		LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>( );
		series2.appendData(new DataPoint(10,50), true, 50);
		series2.appendData(new DataPoint(11,65), true, 50);
		series2.appendData(new DataPoint(12,55), true, 50);
		series2.appendData(new DataPoint(13,75), true, 50);
		series2.appendData(new DataPoint(14,55), true, 50);
		series2.appendData(new DataPoint(15,90), true, 50);
		series2.appendData(new DataPoint(16,75), true, 50);
		series2.appendData(new DataPoint(17,85), true, 50);
		series2.appendData(new DataPoint(18,100), true, 50);
		graph.addSeries(series2);
		series2.setColor(Color.GREEN);
		graph.getViewport().setScalable(true);
		graph.getViewport().setScrollable(true);
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMinX(10.0);
		graph.getViewport().setMaxX(14.0);
		graph.getViewport().setYAxisBoundsManual(true);
		graph.getViewport().setMinY(0.0);
		graph.getViewport().setMaxY(100);
//		graph.getViewport().setBackgroundColor(Color.WHITE);
		graph.setTitle("IP vs Zaki");
		graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
		graph.getGridLabelRenderer().setVerticalAxisTitle("Productivity");
		graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.RED);
		graph.getLegendRenderer().setVisible(true);
		graph.getLegendRenderer().setAlign(LegendAlign.TOP);
		series.setTitle("IP");
		series2.setTitle("Zaki");
		series.setOnDataPointTapListener(new OnDataPointTapListener() {

            @Override
            public void onTap(Series arg0, DataPointInterface arg1) {
                Toast.makeText(getApplicationContext(), "Time: " + arg1.getX() + " Hrs Prod: " + arg1.getY() + "%", Toast.LENGTH_SHORT).show();

            }
        });
		series2.setOnDataPointTapListener(new OnDataPointTapListener() {
			
			@Override
			public void onTap(Series arg0, DataPointInterface arg1) {
				Toast.makeText(getApplicationContext(), "Time: "+arg1.getX()+" Hrs Prod: "+arg1.getY()+"%", Toast.LENGTH_SHORT).show();
				
			}
		});
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
