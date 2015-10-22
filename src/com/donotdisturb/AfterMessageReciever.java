package com.donotdisturb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.constant.Constant;
import com.database.DataBase;

public class AfterMessageReciever  extends Activity{
	private DataBase  mwDB;   
	private Cursor myCursor;
	private int id=0;
	private String []phoneNumbers;
	private String rowId,s;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mwDB = new DataBase(this);
		Bundle b = getIntent().getExtras();   
		rowId=b.getString(Constant.KEY_ID);            

		long row=0;
		try {
			row = Long.parseLong(rowId);
		} catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException: " + nfe.getMessage());
		}

		mwDB.open();    
		myCursor = mwDB.fetch_DataMessage2(row);
		startManagingCursor(myCursor);
		mwDB.close();
		try
		{
			for(int j=0;j<myCursor.getCount();j++)
			{
				s=myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.PHONENO_FORMESSAGE));

				String[] temp=s.split(",");
				String[] temp2=null;
				phoneNumbers = new String[temp.length];
				for (int i = 0; i < temp.length; i++) 
				{
					if(temp[i].contains("\n")){
						try {
							temp2 = new String[2];
							temp2 = temp[i].split("\n");
							phoneNumbers[i] = temp2[1];
						} catch (Exception e) {
						}							
					}else{
						try {
							phoneNumbers[i]=temp[i];
						} catch (Exception e) {
						}

					}
				}	
				sms();
			}
		}catch (Exception e) {
		}

	}
	public void sms()
	{
		try{	
			for (int i = 0; i < phoneNumbers.length; i++) {
				try{
					android.telephony.SmsManager smsMng=android.telephony.SmsManager.getDefault();
					smsMng.sendTextMessage(phoneNumbers[i].trim(), null, myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.MESSAGE_TO_SEND)), null, null);

					Log.e("#####",phoneNumbers[i]);
					Toast.makeText(AfterMessageReciever.this, "Message sent", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
				}
			}
			mwDB.open();
			mwDB.updateMessageTosendStatus(Integer.parseInt(rowId), s, myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.MESSAGE_TO_SEND)),myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.MESSAGE_SENDING_DATE)), Constant.MESSAGE_STAUS_SEND);
			mwDB.close();
			setMessageTosend();
		}catch(Exception e1){
			//		Toast.makeText(mctx, "Message could not be sent", Toast.LENGTH_SHORT).show();
		}
}

	public void setMessageTosend()
	{
		mwDB.open();              
		myCursor=mwDB.sortDateMessage();
		startManagingCursor(myCursor);
		mwDB.close();
		if(myCursor.getCount()>0)
		{
			myCursor.moveToFirst();
			for (int i = 0; i < myCursor.getCount(); i++) 
			{
		//				Log.w("ROWID, FULL",myCursor.getString(myCursor.getColumnIndexOrThrow(MobileWatchDBAdapter.ROWID))+"," +myCursor.getString(myCursor.getColumnIndexOrThrow(MobileWatchDBAdapter.SET_PROFILE_FULL_DATE_TIME)));
				myCursor.moveToNext();
			}  
			myCursor.moveToFirst();
			id = myCursor.getInt(myCursor.getColumnIndexOrThrow(Constant.ROWID));
			//			Log.w("id", id+"");    

			mwDB.open();		
			myCursor=mwDB.fetch_DataMessage2(id);           
			startManagingCursor(myCursor);
			myCursor.moveToFirst();
			mwDB.close();                      
			AlarmManager alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
			Intent intent2 = new Intent(AfterMessageReciever.this, TosendMessage.class);			
			intent2.putExtra(Constant.KEY_ID,myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.ROWID)));
			PendingIntent pI1 = PendingIntent.getBroadcast(AfterMessageReciever.this, 12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
			Calendar calendar = Calendar.getInstance();         
			calendar.setTimeInMillis(System.currentTimeMillis()); 
			String datefull=myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.MESSAGE_SENDING_DATE));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				Date convertedDate= dateFormat.parse(datefull);
				calendar.set(Calendar.DATE, convertedDate.getDate());
				calendar.set(Calendar.MONTH, convertedDate.getMonth());
				calendar.set(Calendar.YEAR, convertedDate.getYear()+1900);
				calendar.set(Calendar.HOUR_OF_DAY, convertedDate.getHours());
				calendar.set(Calendar.MINUTE, convertedDate.getMinutes());
				calendar.set(Calendar.SECOND, convertedDate.getSeconds());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
			alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pI1);
		}
		finish();
	}
}
