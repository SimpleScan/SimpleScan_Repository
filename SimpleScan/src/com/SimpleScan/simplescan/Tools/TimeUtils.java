package com.SimpleScan.simplescan.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
	
	public static final String NONE = "None";
	public static final String ONE_DAY = "1 day before due date";
	public static final String TWO_DAYS = "2 days before due date";
	public static final String THREE_DAYS = "3 days before due date";
	public static final String ONE_WEEK = "1 week before due date";
	
	/**
	 * Get the date representing the specified reminding days before the due date
	 * @param dueDate
	 * @param remindDays
	 * @return reminding date
	 */
	public static String getRemindDate(String dueDate, String remindDays) {
		String remindDate="";
		
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
			Date beforeDate = dateFormat.parse(dueDate);
			
			int y = beforeDate.getYear();
			int m = beforeDate.getMonth();
			int d = beforeDate.getDate();
			
			if(d<remindDateStr2Int(remindDays)) {
				
				if(m==1) {
					y --;
					m = 12;
				} else m--;
				
				switch(m) {
					case 1:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 2:
						if(y%4==0) d = 29-(remindDateStr2Int(remindDays)-d);
						else d = 28-(remindDateStr2Int(remindDays)-d);
						break;
					case 3:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 4:
						d = 30-(remindDateStr2Int(remindDays)-d);
						break;
					case 5:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 6:
						d = 30-(remindDateStr2Int(remindDays)-d);
						break;
					case 7:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 8:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 9:
						d = 30-(remindDateStr2Int(remindDays)-d);
						break;
					case 10:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
					case 11:
						d = 30-(remindDateStr2Int(remindDays)-d);
						break;
					case 12:
						d = 31-(remindDateStr2Int(remindDays)-d);
						break;
						
				}
			}
			
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(System.currentTimeMillis());
	        cal.set(Calendar.YEAR, y);
	        cal.set(Calendar.MONTH, m);
	        cal.set(Calendar.DATE, d);
	        
	        remindDate = cal.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}	
    	
		return remindDate;
	}
	
	/**
	 * Get the current date difference between the input date and current date
	 * @param billDueDate
	 * @return the date difference
	 */
	public static String getDateDiffStr(String billDueDate)
    {
        try
        {
            Calendar cal = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date dueDate = dateFormat.parse(billDueDate); 
            
            long curTime = cal.getTimeInMillis();
            long dueTime = dueDate.getTime();
            long oneDay = 1000 * 60 * 60 * 24;
            long delta = (dueTime - curTime) / oneDay;

            if (delta > 0) {
                return "" + delta + "";
            }
            else {
                delta *= -1;
                return "" + delta + "";
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return "";
    }
	
	/**
	 * Convert the date format from String to Integer
	 * @param remindDateStr: String representation of the reminding date
	 * @return remindDateInt: Integer representation of the reminding date
	 */
	public static int remindDateStr2Int(String remindDateStr) {
		int remindDateInt = 0;
		if(remindDateStr==ONE_DAY) remindDateInt = 1;
		if(remindDateStr==TWO_DAYS) remindDateInt = 2;
		if(remindDateStr==THREE_DAYS) remindDateInt = 3;
		if(remindDateStr==ONE_WEEK) remindDateInt = 14;
		return remindDateInt;
	}
	
	/**
	 * Convert days 2 seconds
	 * @param days
	 * @return seconds
	 */
	public static int days2sec(int days) {
		return days*24*60*60;
	}
	
	/**
	 * Get current day in the current month
	 * @return the day in month
	 */
	public static int getDaysInMonth() {
		int repeatDays = 0;
		Calendar cal = Calendar.getInstance();
		int curMonth = cal.get(Calendar.MONTH);
		switch(curMonth) {
		case 1:
			repeatDays = 31;
			break;
		case 2:
			if(cal.get(Calendar.YEAR)%4==0) repeatDays = 29;
			else repeatDays = 28;
			break;
		case 3:
			repeatDays = 31;
			break;
		case 4:
			repeatDays = 30;
			break;
		case 5:
			repeatDays = 31;
			break;
		case 6:
			repeatDays = 30;
			break;
		case 7:
			repeatDays = 31;
			break;
		case 8:
			repeatDays = 31;
			break;
		case 9:
			repeatDays = 30;
			break;
		case 10:
			repeatDays = 31;
			break;
		case 11:
			repeatDays = 30;
			break;
		case 12:
			repeatDays = 31;
			break;
		}
		return repeatDays;
	}
}
