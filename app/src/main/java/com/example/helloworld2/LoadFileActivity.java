package com.example.helloworld2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer.LegendAlign;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.opencsv.CSVReader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoadFileActivity extends Activity implements OnClickListener,OnDataPointTapListener {
	Button bSelectFile,bSelectPara;

	final Activity activityForButton = this;
	private final int REQUEST_CODE_PICK_DIR = 1;
	private final int REQUEST_CODE_PICK_FILE = 2;
	List<BTData> loadedBTData = new ArrayList<>();
	GraphView graph;
	ArrayList<Parameter> parameterList = new ArrayList<>();
	double numberOfPoints;
	CharSequence[] parameters;
	ArrayList<CharSequence> selectedParameter = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadfileactivity);
		initParameters();
		init_elements();
		init_graph();
		LineGraphSeries<DataPoint> temp = new LineGraphSeries<>();
		temp.appendData(new DataPoint(0, 0), false, 50);
		graph.addSeries(temp);
		parameters = new CharSequence[parameterList.size()];
		for(int i=0;i<parameterList.size();i++) {
			parameters[i]=parameterList.get(i).getName();
		}

		for(int i=0;i<parameterList.size();i++) {
			parameterList.get(i).series.setOnDataPointTapListener(this);
		}

		for(int i=0;i<parameterList.size();i++) {
			if(parameterList.get(i).isVisible())
				selectedParameter.add(parameterList.get(i).getName());
		}

	}

	private void initParameters() {
		parameterList.clear();
		parameterList.add(new Parameter("Pressure 1", Color.BLUE, "bar", true));
		parameterList.add(new Parameter("Pressure 2", Color.GREEN, "bar", true));
		parameterList.add(new Parameter("Pressure 3",Color.RED,"bar",false));
		parameterList.add(new Parameter("Temperature",Color.BLACK,"degC",false));
	}

	private void clearSeries() {
		for(int i=0;i<parameterList.size();i++) {
			parameterList.get(i).series= new LineGraphSeries<>();
			parameterList.get(i).series.setColor(parameterList.get(i).getColour());
		}
	}

	private void init_elements() {
		bSelectFile = (Button) findViewById(R.id.bSelectFile);
		bSelectFile.setOnClickListener(this);
		graph = (GraphView) findViewById(R.id.lfgraph);
		bSelectPara = (Button) findViewById(R.id.bSelectParaLoad);
		bSelectPara.setOnClickListener(this);
		
		// tvCSVData = (TextView) findViewById(R.id.tvCSVData);
		// tvCSVData.setMovementMethod(new ScrollingMovementMethod());
		
	}

	private void init_graph() {
		graph.getViewport().setScalable(true);
		graph.getViewport().setScrollable(true);
		
	}
	
	protected void onChangeSelectedParameters() {
		graph.removeAllSeries();


		for(int i=0;i<parameterList.size();i++) {
			if(selectedParameter.contains(parameterList.get(i).getName())) {
				parameterList.get(i).setVisible(true);
				graph.addSeries(parameterList.get(i).series);
			} else {
				parameterList.get(i).setVisible(false);
				graph.removeSeries(parameterList.get(i).series);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSelectFile:

			clearSeries();

			Intent fileExploreIntent = new Intent(FileBrowserActivity.INTENT_ACTION_SELECT_FILE, null,
					activityForButton, FileBrowserActivity.class);
					fileExploreIntent.putExtra(FileBrowserActivity.startDirectoryParameter, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Gilbarco");
			startActivityForResult(fileExploreIntent, REQUEST_CODE_PICK_FILE);
			break;
		case R.id.bSelectParaLoad:
			showSelectParaDialog();
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_FILE) {
			if (resultCode == RESULT_OK) {
                graph.removeAllSeries();
				init_graph();
				String newFile = data.getStringExtra(FileBrowserActivity.returnFileParameter);
				Toast.makeText(getApplicationContext(), "Received FILE path from file browser:\n" + newFile,
						Toast.LENGTH_SHORT).show();
				CSVReader csvReader = null;
				try {
					csvReader = new CSVReader(new FileReader(newFile));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] row;
				try {
					while ((row = csvReader.readNext()) != null) {
						StringBuilder sb = new StringBuilder("");
						int i ;
						for (i = 0; i < row.length - 1; i++) {
							sb.append(row[i]);
							sb.append(",");
							// tvCSVData.append(row[i]);
							// tvCSVData.append(",");
						}
						sb.append(row[i]);
						loadedBTData.add(new BTData(sb.toString()));
						// tvCSVData.append("\n");
					}
					numberOfPoints = loadedBTData.size();
					while (!loadedBTData.isEmpty()) {
						// tvCSVData.append(String.valueOf(loadedBTData.get(0).getFullTime())+","+String.valueOf(loadedBTData.get(0).getPressure1())+","+String.valueOf(loadedBTData.get(0).getPressure2()));
						// tvCSVData.append("\n");
							for(int i=0;i<parameterList.size();i++) {
								parameterList.get(i).series.appendData(new DataPoint(loadedBTData.get(0).getFullTime(),loadedBTData.get(0).getParameter(i + 1)),false, (int) numberOfPoints);
							}
						loadedBTData.remove(0);
					}

					graph.getViewport().setXAxisBoundsManual(true);
					graph.getViewport().setMinX(parameterList.get(0).series.getLowestValueX());
					graph.getViewport().setMaxX(parameterList.get(0).series.getLowestValueX()+50);
					
					graph.getViewport().setScalable(true);
					graph.getViewport().setScrollable(true);

					for(int i=0;i<parameterList.size();i++) {
						if(parameterList.get(i).isVisible()) {
							graph.addSeries(parameterList.get(i).series);
						}
					}

					setLegendsAndTap();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(getApplicationContext(), "Received no result from file browser", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	void setLegendsAndTap() {
//		initParameters();
		graph.getLegendRenderer().setVisible(true);
		graph.getLegendRenderer().setAlign(LegendAlign.TOP);
		for(int i=0;i<parameterList.size();i++) {
			parameterList.get(i).series.setOnDataPointTapListener(this);
			parameterList.get(i).series.setTitle(parameterList.get(i).getName());
		}

	}
	@Override
	public void onTap(Series s, DataPointInterface d) {
//		Toast.makeText(getApplicationContext(), "Inside OnTap", Toast.LENGTH_SHORT).show();
		for(int i=0;i<parameterList.size();i++) {
			if(s==parameterList.get(i).series) {
				Toast.makeText(getApplicationContext(), "Time: "+d.getX()+" "+parameterList.get(i).getName()+": "+d.getY()+parameterList.get(i).getUnit(), Toast.LENGTH_SHORT).show();
			}
		}
	}

}

