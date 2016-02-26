package com.example.helloworld2;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Zaki on 20-Feb-16.
 */
public class Parameter {
    private String sName;
    private int iColour;
    private String sUnit;
    public boolean visible;
    public LineGraphSeries<DataPoint> series;

    public Parameter(String sName, int iColour, String sUnit, boolean visible) {
        this.sName = sName;
        this.iColour = iColour;
        this.sUnit = sUnit;
        this.visible = visible;
        this.series = new LineGraphSeries<>();
        this.series.setColor(this.getColour());
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return sName;
    }

    public void setName(String sName) {
        this.sName = sName;
    }

    public int getColour() {
        return iColour;
    }

    public void setColour(int iColour) {
        this.iColour = iColour;
    }

    public String getUnit() {
        return sUnit;
    }

    public void setUnit(String sUnit) {
        this.sUnit = sUnit;
    }


}
