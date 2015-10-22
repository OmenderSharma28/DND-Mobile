//package com.donotdisturb;
//
//import java.sql.Date;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//public class setReminder {
//	private Context mContext;
//	AlarmManager aManager;
//	java.util.Date convertedDate ;
//	public setReminder(Context context) {
//		mContext =context;
//	}
//
//	public void setAlarm(String date) throws ParseException{
//		
//		Log.e("date and time ",date);
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm"); 
//		convertedDate = dateFormat.parse(date);
//		Log.e("date in miliseconed ", convertedDate.getTime()+""); 
////		Calendar cal = Calendar.getInstance();
//		Intent it=new Intent(mContext,ReceiverReminder.class);
//		PendingIntent	pintent=PendingIntent.getBroadcast(mContext, 1, it,Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		aManager=(AlarmManager)(mContext.getSystemService(mContext.ALARM_SERVICE));
//		//		aManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pintent);
//		Log.e("date in miliseconed", convertedDate.getTime()+"");
//		aManager.set(AlarmManager.RTC_WAKEUP,convertedDate.getTime(), pintent);
//		Log.e("miliseconed to date",new SimpleDateFormat("dd-mm-yyyy hh:mm").format(new Date(convertedDate.getTime()))+"");
//
//	}
//
//}