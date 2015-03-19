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
    	
    	for(int i=0; i<inputString.length(); i++) {
    		if(isNumber(inputString.charAt(i))) outputString += inputString.charAt(i);
    	}
    	
    	return outputString;
    }
    
    private static String extractDate(String inputString){
    	String m="", d="", y="";
    	String outputString="Couldn't detect date";
    	
    	inputString = inputString.toLowerCase(Locale.ENGLISH);
    	for(int i=0; i<inputString.length(); i++) {
    		
    		if(m!="" && d!="" && y=="") {
    			if(i+3 < inputString.length()) {  				
    				if(Integer.parseInt(inputString.substring(i, i+3)) >= 2000 && Integer.parseInt(inputString.substring(i, i+3)) <= 2115) y = inputString.substring(i, i+3);
    			}
    		} else if(m!="") {
    			if(i+1 < inputString.length()){
    				if(Integer.parseInt(inputString.substring(i, i+1)) >= 1 && Integer.parseInt(inputString.substring(i, i+1)) <= 31) d = inputString.substring(i, i+1)+"/";
    			}
			} else {
	    		if(     inputString.contains("jan") || inputString.contains("january")   || inputString.contains("1/") || inputString.contains("01/")) m="01/";
	    		else if(inputString.contains("feb") || inputString.contains("febuary")   || inputString.contains("2/") || inputString.contains("02/")) m="02/";
	    		else if(inputString.contains("mar") || inputString.contains("march")     || inputString.contains("3/") || inputString.contains("03/")) m="03/";
	    		else if(inputString.contains("apr") || inputString.contains("april")     || inputString.contains("4/") || inputString.contains("04/")) m="04/";
	    		else if(inputString.contains("may") || inputString.contains("5/")        || inputString.contains("05/")) 							   m="05/";
	    		else if(inputString.contains("jun") || inputString.contains("june")      || inputString.contains("6/") || inputString.contains("06/")) m="06/";
	    		else if(inputString.contains("jul") || inputString.contains("july")      || inputString.contains("7/") || inputString.contains("07/")) m="07/";
	    		else if(inputString.contains("aug") || inputString.contains("august")    || inputString.contains("8/") || inputString.contains("08/")) m="08/";
	    		else if(inputString.contains("sep") || inputString.contains("september") || inputString.contains("9/") || inputString.contains("09/")) m="09/";
	    		else if(inputString.contains("oct") || inputString.contains("october")   || inputString.contains("10/"))							   m="10/";
	    		else if(inputString.contains("nov") || inputString.contains("november")  || inputString.contains("11/"))						       m="11/";
	    		else if(inputString.contains("dec") || inputString.contains("december")  || inputString.contains("12/"))							   m="12/";
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
