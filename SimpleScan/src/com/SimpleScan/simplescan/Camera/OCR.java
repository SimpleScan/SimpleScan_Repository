package com.SimpleScan.simplescan.Camera;

import java.util.Locale;

import android.graphics.Bitmap;
import android.os.Environment;
import com.googlecode.tesseract.android.TessBaseAPI;

public class OCR {
	
	public static final boolean wordLimit = true;
	
	public static final String DETECT_ALL = "detect_all";
	public static final String DETECT_NUMBERS = "detect_numbers";
	public static final String DETECT_DATE = "detect_date";
	
	private static final String [] MONTHS = {"january", "febuary", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
	
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
    	baseApi.init(Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/", "eng");
    	baseApi.setImage(targetBitmap);
    	String recognizedText = baseApi.getUTF8Text();
    	
    	if (detectOption == DETECT_NUMBERS) recognizedText = extractNumbers(recognizedText);
    	else if(detectOption == DETECT_DATE) recognizedText = extractDate(recognizedText);
    	else if(wordLimit && recognizedText.length() >= 15) recognizedText = recognizedText.substring(0, 15);
    	
    	baseApi.end();
    	
    	return recognizedText;
    }
    
    private static String extractNumbers(String inputString) {
    	String outputString="";
    	boolean numberFound = false;
    	
    	int numberTextLength = inputString.length();
    	for(int i=0; i<numberTextLength; i++) {
    		if(isNumber(inputString.charAt(i))){
    			numberFound = true;
    			
    			outputString += inputString.charAt(i);
    			
    			if(inputString.charAt(i) == '.' //rounding to 2 decimal places
    			&& i+2 < inputString.length()) numberTextLength = i+2+1;
    		}
    	}
    	
    	if(numberFound) numberFound = false;
    	else outputString = "Couldn't detect amount";
    	
    	return outputString;
    }
    
    private static String extractDate(String inputString){
    	String outputString="";
    	inputString = inputString.toLowerCase(Locale.ENGLISH);

    	if(inputString.contains("/")) outputString = extractDateFromNumbers(inputString); //checking for numbers; 			
    	if(outputString=="") outputString = extractDateFromLetters(inputString); //checking for alphabetically-spelling months
    	
    	return outputString;
    }
    
    private static String extractDateFromLetters(String inputString) {
    	String m="", d="", y="";
    	String extractedDate="";
    	
    	//Extracting month
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
    	if(curMaxVal > 0) {
	    	if(String.valueOf(curMaxIdx+1).length() == 1) m = "0"+String.valueOf(curMaxIdx+1)+"/";
			else m = String.valueOf(curMaxIdx+1)+"/";
    	}

		for(int i=0; i<inputString.length(); i++) {	
    		if(m!="" && d!="") { //extracting year   			
    			if(i+3 < inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1)) && isNumber(inputString.charAt(i+2)) && isNumber(inputString.charAt(i+3))) {
    					if(Integer.parseInt(inputString.substring(i, i+4)) >= 2000 && Integer.parseInt(inputString.substring(i, i+4)) <= 2115) {
    						y = inputString.substring(i, i+4);	
    					}
    				}
    			}		
    		} else if(m!="") { //extracting date
    			if(i+1 < inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1))) {
    					if(Integer.parseInt(inputString.substring(i, i+2)) >= 1 && Integer.parseInt(inputString.substring(i, i+2)) <= 31) 
    						d = inputString.substring(i, i+2)+"/";
    				} 
    			} else if(isNumber(inputString.charAt(i))) {
    				if(Integer.parseInt(inputString.substring(i, i+1)) >= 1 && Integer.parseInt(inputString.substring(i, i+1)) <= 9) 
    					d = "0"+inputString.substring(i, i+1)+"/";
    			} 	
			} 
		}
    	
		if(m!="" && d!="" && y!="") extractedDate = m + d + y;
    	return extractedDate;
    }
    
    private static String extractDateFromNumbers(String inputString) {
    	String m="", d="", y="";
    	String extractedDate="";    	
    	
		for(int i=0; i<inputString.length(); i++) {
			if(m!="" && d!="" && y=="") { //extracting year
				if(i+3 < inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1)) && isNumber(inputString.charAt(i+2)) && isNumber(inputString.charAt(i+3))) {
    					if(Integer.parseInt(inputString.substring(i, i+4)) >= 2000 && Integer.parseInt(inputString.substring(i, i+4)) <= 2115) {
    						y = inputString.substring(i, i+4);	
    					}
    				}
    			} else if(i+1 < inputString.length()) {
					if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1))) {
						if(Integer.parseInt(inputString.substring(i, i+2)) >= 0 && Integer.parseInt(inputString.substring(i, i+2)) <= 99) {
							y = "20" + inputString.substring(i, i+2);
						}
					}	
				}
			}
			else if(m!="" && d=="" && y=="") { //extracting date
				if(i+2 < inputString.length()) {
					if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1)) && inputString.charAt(i+2) == '/') {
						if(Integer.parseInt(inputString.substring(i, i+2)) >= 1 && Integer.parseInt(inputString.substring(i, i+2)) <= 31) { 
							d = inputString.substring(i, i+2)+"/";
						}
					}
				}
			}
			else if(m=="" && d=="" && y=="") { //extracting month
    			if(i+3 <= inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && isNumber(inputString.charAt(i+1)) && inputString.charAt(i+2) == '/') {	
		    			if (inputString.charAt(i) == '0') { 
		    				switch(inputString.charAt(i+1)) {
			    				case '1' :
				    				m="01/";
				    				break;
			    				case '2' :
				    				m="02/";
				    				break;
			    				case '3' :
				    				m="03/";
				    				break;
			    				case '4' :
				    				m="04/";
				    				break;
			    				case '5' :
				    				m="05/";
				    				break;
			    				case '6' :
				    				m="06/";
				    				break;
			    				case '7' :
				    				m="07/";
				    				break;
			    				case '8' :
				    				m="08/";
				    				break;
			    				case '9' :
				    				m="09/";
		    				} 
		    			} else if(inputString.charAt(i) == '1') {
		    				switch (inputString.charAt(i+1)) {
		    					case '0' :
		    						m="10/";
		    						break;
		    					case '1' :
		    						m="11/";
		    						break;
		    					case '2' :
		    						m="12/";	    						
		    				}
	    				}
    				}
    			}
    			if (m=="" && i+2 <= inputString.length()) {
    				if(isNumber(inputString.charAt(i)) && inputString.charAt(i+1) == '/') {
	    				switch(inputString.charAt(i)) {
		    				case '1' :
			    				m="01/";
			    				break;
		    				case '2' :
			    				m="02/";
			    				break;
		    				case '3' :
			    				m="03/";
			    				break;
		    				case '4' :
			    				m="04/";
			    				break;
		    				case '5' :
			    				m="05/";
			    				break;
		    				case '6' :
			    				m="06/";
			    				break;
		    				case '7' :
			    				m="07/";
			    				break;
		    				case '8' :
			    				m="08/";
			    				break;
		    				case '9' :
			    				m="09/";
	    				} 
    				}
    			}
			}
		}   	
    	if(m!="" && d!="" && y!="") extractedDate = m + d + y;
    	return extractedDate;
    }
    
    private static boolean isNumber(char input) {
    	if(input == '0' || input == '1' || input == '2' || input == '3' || input == '4'
    	|| input == '5' || input == '6' || input == '7' || input == '8' || input == '9'
    	|| input == '.') return true;
    	else return false;
    } 
}
