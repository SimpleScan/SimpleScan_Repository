package com.SimpleScan.simplescan.Tools;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	
	final public static String ONE_TIME = "onetime";
	final public static String MSG = "message";

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake up!!!");
		wl.acquire(); //acquire the lock
		
		//Processing
		Bundle extras = intent.getExtras();
		StringBuilder statusMSG = new StringBuilder();
		StringBuilder notificationMSG = new StringBuilder();
		if(extras != null) {
			//Make sure this intent has been sent by the one-time timer button.
			if(extras.getBoolean(ONE_TIME, Boolean.FALSE)) statusMSG.append("One-time timer triggered");
			else statusMSG.append("Repeated timer triggered");
			
			
			notificationMSG.append(extras.getString(MSG, "alarm went off at: "));
			Format formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
			notificationMSG.append(formatter.format(new Date()));
		}
		Notification theNotification = new Notification(context, notificationMSG.toString(), "SimpleScan: " + statusMSG.toString(), false);
		if(theNotification!=null) theNotification.sendNotification(context, 001);
		
		//Release the lock
		wl.release();
	}
	
	public void setRepeatedDateAlarm(Context context, int id, String setDate, int frequency, String notificationMSG) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = alarmPIntent(context, id, false, notificationMSG);
		try {
			Date triggerDate = getFormattedDate(setDate);
			am.setRepeating(AlarmManager.RTC_WAKEUP, triggerDate.getTime(), 1000*frequency, pIntent);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void setOneTimeDateAlarm(Context context, int id, String setDate, String notificationMSG){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = alarmPIntent(context, id, true, notificationMSG);		
		try {
			Date triggerDate = getFormattedDate(setDate);
			am.set(AlarmManager.RTC_WAKEUP, triggerDate.getTime(), pIntent);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelAlarm(Context context, int id) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, id, alarmIntent, 0);		
		am.cancel(sender);
	}
	
	private PendingIntent alarmPIntent(Context context, int id, boolean oneTime, String notificationMSG) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		if(notificationMSG != ""){
			alarmIntent.putExtra(MSG, notificationMSG);
			alarmIntent.putExtra(ONE_TIME, oneTime);
		}
		return PendingIntent.getBroadcast(context, id, alarmIntent, 0);
	}
	
	private Date getFormattedDate(String theDate) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return dateFormat.parse(theDate);
	}
}
