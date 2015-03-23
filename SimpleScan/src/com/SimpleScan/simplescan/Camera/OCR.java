package com.SimpleScan.simplescan.Camera;

import java.util.Locale;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

public class OCR {
	
	public static final String DETECT_ALL = "detect_all";
	public static final String DETECT_NUMBERS = "detect_numbers";
	public static final String DETECT_DATE = "detect_date";
	
	private final static String [] MONTHS = {"january", "febuary", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
	
	//For EditExpense, convert amount from String to double, for budget subtracting 
	public static double amtStr2double (String amtStr) {
    	double amt=0;
    	String numStr="";
    	
    	for(int i=0; i<amtStr.length(); i++) {
    		if(amtStr.charAt(i)!='$') numStr+=amtStr.charAt(i);  			
    	}

	    amt=Double.parseDouble(numStr);
    	
    	return amt;
    }
	
	public static String detect_text(Bitmap targetBitmap, String detectOption){
    	TessBaseAPI baseApi = new TessBaseAPI();
    	Log.i("tessrect", "new tess object created");   	
    	baseApi.init(Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/", "eng");
    	Log.i("tessrect", "initialized");
    	
    	baseApi.setImage(targetBitmap);
    	Log.i("tessrect", "bitmap/image set");
    	
    	String recognizedText = baseApi.getUTF8Text();
    	
    	if (detectOption == DETECT_NUMBERS) recognizedText = extractNumbers(recognizedText);
    	else if(detectOption == DETECT_DATE) recognizedText = extractDate(recognizedText);
    	
    	baseApi.end();
    	
    	return recognizedText;
    }
    
    private static String extractNumbers(String inputString) {
    	String outputString="";
    	boolean numberFound = false;
    	
    	for(int i=0; i<inputString.length(); i++) {
    		if(isNumber(inputString.charAt(i))) outputString += inputString.charAt(i);
    		numberFound = true;
    	}

    	if(numberFound) {
    		//outputString = "$" + outputString;
    		outputString = outputString;
    		numberFound = false;
    	}
    	else outputString = "Couldn't detect amount";
    	
    	return outputString;
    }
    
    private static String extractDate(String inputString){
    	String m="", d="", y="";
    	String outputString="";

    	inputString = inputString.toLowerCase(Locale.ENGLISH);
    	Log.i("inputString", inputString);
    	
    	double [] accuracy = {0,0,0,0,0,0,0,0,0,0,0,0};
    	double curMaxVal=0;
    	int curMaxIdx=0;
    	for(int cur=0; cur<12; cur++) {
    		String curMonth = MONTHS[cur];
    		for(int j=0; j<curMonth.length()-1; j++) {
    			String month_substr = curMonth.substring(j, j+2);
    			if(inputString.contains(month_substr)) accuracy[cur] ++;
    		}
    		if((accuracy[cur]/curMonth.length())>curMaxVal) {
    			curMaxIdx = cur;
    			curMaxVal = accuracy[cur]/curMonth.length();
    		}
    	}
    	Log.i("curMaxVal", String.valueOf(curMaxVal));
    	if(curMaxVal > 0) {
	    	if(String.valueOf(curMaxIdx+1).length() == 1) m = "0"+String.valueOf(curMaxIdx+1)+"/";
			else m = String.valueOf(curMaxIdx+1)+"/";
			Log.i("month", m);
    	}
    	
    	for(int i=0; i<inputString.length(); i++) {
    		
    		if(m!="" && d!="") {
    			if(i+3 < inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1)) && isNumber(inputString.charAt(i+2)) && isNumber(inputString.charAt(i+3))) {
    					if(Integer.parseInt(inputString.substring(i, i+4)) >= 2000 && Integer.parseInt(inputString.substring(i, i+4)) <= 2115) y = inputString.substring(i, i+4);
    					Log.i("year", y);
    				}
    			}
    		} else if(m!="") {
    			if(i+1 < inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1))) {
    					if(Integer.parseInt(inputString.substring(i, i+2)) >= 1 && Integer.parseInt(inputString.substring(i, i+2)) <= 31) 
    						d = inputString.substring(i, i+2)+"/";
    				} 
    			} else if(isNumber(inputString.charAt(i))) {
    				if(Integer.parseInt(inputString.substring(i, i+1)) >= 1 && Integer.parseInt(inputString.substring(i, i+1)) <= 9) 
    					d = "0"+inputString.substring(i, i+1)+"/";
    			}
    			Log.i("date", d);   					
			} 
		}	
    	
    	if(m!="" && d!="" && y!="") outputString = m + d + y;
    	
    	return outputString;
    }
    
    private static boolean isNumber(char input) {
    	if(input == '0' || input == '1' || input == '2' || input == '3' || input == '4'
    	|| input == '5' || input == '6' || input == '7' || input == '8' || input == '9'
    	|| input == '.') return true;
    	else return false;
    }
   
}
