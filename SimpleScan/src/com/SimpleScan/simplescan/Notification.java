package com.SimpleScan.simplescan;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class Notification {
	
	private NotificationCompat.Builder mBuilder;
	
	public Notification (Context context, String text, String statusText, boolean curIntent) {
		Intent resultIntent;
		if(curIntent) resultIntent = ((Activity) context).getIntent();
		else resultIntent = new Intent(context, Main.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//Bitmap large_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_small_icon);
		
		setBuilder(context, text, statusText);
		mBuilder.setContentTitle("SimpleScan") //set the title (first row) of the notification, in a standard notification.		
				.setContentIntent(resultPendingIntent);
	}
	
	public Notification (Context context, String title, String text, String statusText, boolean curIntent) {
		Intent resultIntent;
		if(curIntent) resultIntent = ((Activity) context).getIntent();
		else resultIntent = new Intent(context, Main.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		setBuilder(context, text, statusText);
		mBuilder.setContentTitle(title) //set the title (first row) of the notification, in a standard notification.		
				.setContentIntent(resultPendingIntent); //automically jump into the app when the user clicks it in the panel.
	}
	
	public NotificationCompat.Builder getNotification() {
		return mBuilder;
	}
	
	public void sendNotification(Context context, int id) {
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(id, mBuilder.build());
	}
	
	private void setBuilder(Context context, String text, String statusText) {
		mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.logo_small_icon) //set the small icon to use in the notification layouts
		//.setLargeIcon(large_icon) //set the large icon that is shown in the ticker and notification.	
		.setContentText(text) //set the text (second row) of the notification, in a standard notification.				
		.setAutoCancel(true) //the notification is automatically canceled when the user clicks it in the panel.
		.setVibrate(new long[] {0, 1500}) //after 0sec delay, vibrate for 1.5sec
		.setTicker(statusText); //set the text that is displayed in the status bar when the notification first arrives.				
	}
}
