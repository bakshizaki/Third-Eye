package com.example.helloworld2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BTData {
	private String btString;
	private String[] parameters=null;
    public int numberOfParameters=10;
	public BTData(String s) {
		btString = s;
	}

	double getParameter(int p) {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[p])) {
			return Double.parseDouble(parameters[p]);
		}
		else {
			return 0;
		}
	}
	double getPressure1() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[1])) {
			return Double.parseDouble(parameters[1]);
		}
		else {
			return 0;
		}
		
	}
	double getPressure2() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[2])) {
			return Double.parseDouble(parameters[2]);
		}
		else {
			return 0;
		}
		
	}
	double getPressure3() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[3])) {
			return Double.parseDouble(parameters[3]);
		}
		else {
			return 0;
		}
		
	}
	double getTime() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[0])) {
			return round(((Double.parseDouble(parameters[0]))/1000),1);
		}
		else {
			return 0;
		}
		
	}
	double getFullTime() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[0])) {
			return Double.parseDouble(parameters[0]);
		}
		else {
			return 0;
		}
		
	}
	double getTemperature() {
		if(parameters==null)
			parameters = btString.split(",");
		if(isDouble(parameters[4])) {
			return Double.parseDouble(parameters[4]);
		}
		else {
			return 0;
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
    
    public String[] getCSVData() {
        String[] s = new String[numberOfParameters];
        for(int i=0;i<numberOfParameters;i++) {
            s[i]=String.valueOf(getParameter(i+1));
        }
    	return s;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
