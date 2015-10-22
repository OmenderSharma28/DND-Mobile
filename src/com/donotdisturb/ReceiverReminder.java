//package com.donotdisturb;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.constant.Constant;
//import com.database.DataBase;
//
//public class ReceiverReminder extends BroadcastReceiver{
//	DataBase database;
//	String []phoneNumbers;
//	Context mctx;
//	Cursor c;
//	@Override  
//	public void onReceive(Context context, Intent intent) 
//	{     
//		database=new DataBase(context);
//		mctx=context;
//		try
//		{
//			database.open();
//			c=database.fetchAllNOList();
//			database.close();
//		}
//		catch (Exception e) {
//
//		}
//		try
//		{
//			for(int j=0;j<c.getCount();j++)
//			{
//				String s=c.getString(c.getColumnIndexOrThrow(Constant.PHONENO_FORMESSAGE));
//
//				String[] temp=s.split(",");
//				String[] temp2=null;
//				phoneNumbers = new String[temp.length];
//				for (int i = 0; i < temp.length; i++) 
//				{
//					if(temp[i].contains("\n")){
//						try {
//							temp2 = new String[2];
//							temp2 = temp[i].split("\n");
//							phoneNumbers[i] = temp2[1];
//						} catch (Exception e) {
//						}							
//					}else{
//						try {
//							phoneNumbers[i]=temp[i];
//						} catch (Exception e) {
//						}
//
//					}
//				}	
//				Log.e("#####",phoneNumbers[0]);
//				sms();
//			}
//		}catch (Exception e) {
//		}
//		Toast.makeText(context,"alarm is set",Toast.LENGTH_LONG).show();
//
//	}
//	public void sms()
//	{
//		try{	
//			for (int i = 0; i < phoneNumbers.length; i++) {
//				try{
//					android.telephony.SmsManager smsMng=android.telephony.SmsManager.getDefault();
//					smsMng.sendTextMessage(phoneNumbers[i].trim(), null, c.getString(c.getColumnIndexOrThrow(Constant.MESSAGE_TO_SEND)), null, null);
//					Log.e("Message sent","");;
//				}catch(Exception e){
//				}
//			}
//		}catch(Exception e1){
//			Toast.makeText(mctx, "Message could not be sent", Toast.LENGTH_SHORT).show();
//		}
//
//	}
//
//}