package com.donotdisturb;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.constant.Constant;
import com.database.DataBase;

public class IncomingcallrecieverActivity extends BroadcastReceiver {
	private String TAG = "BRODCASTRECIEVER";
	private String INCOMMING_CALL = "incomming";
	//	private String OUTGOING_CALL = "outgoing";
	private TelephonyManager telephony;
	private ITelephony telephonyService;
	private DataBase database;
	int OUTGOING_CALL_ACTION;
	int flag = 0;
	private String STATUS="status",BLOCK_NUMBER="blockno";
	//	private String outgoingno;

	public IncomingcallrecieverActivity() {
		Constant.IncommingCall=!Constant.IncommingCall;
		//		Log.e(TAG + "list element!!!!!!!!!!!", "vconstruter");
	}

	public void onReceive(Context context, Intent intent) {
		Log.e("",""+Constant.IncommingCall);
		database = new DataBase(context);
		List<Map<String, Object>> blocknolist = TofetchAllBlockList();
		/*		if (intent.getAction().equalsIgnoreCase(
		"android.intent.action.NEW_OUTGOING_CALL")) {
			//			Log.e("", "Out going call");
			outgoingno = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			for (int i = 0; i < blocknolist.size(); i++) {
				if (blocknolist.get(i).get("blockno").toString()
						.equalsIgnoreCase(outgoingno)) {

					setResultData(null);
					Toast.makeText(context,
							"Call:  " + outgoingno + " - ABORTING CALL.",
							Toast.LENGTH_LONG).show();
					database.open();
					Calendar cal = Calendar.getInstance();
					String Time = cal.get(Calendar.HOUR_OF_DAY) + ":"
					+ cal.get(Calendar.MINUTE) + " "
					+ cal.get(Calendar.DAY_OF_MONTH) + "-"
					+ cal.get(Calendar.MONTH) + "-"
					+ cal.get(Calendar.YEAR);
					ContentValues initialvalues = new ContentValues();
					initialvalues.put(Constant.MESSAGE_TIMESTAMP,
							Time);
					initialvalues.put(Constant.BLOCK_NO, outgoingno);
					initialvalues.put(Constant.CallType, OUTGOING_CALL);
					database.inserteventvalue(
							Constant.DATABASE_BLOCKLIST_CALL_TABLE,
							initialvalues);
					database.close();
				}

			}
			flag=1;
			// abortBroadcast();
		} else */
		if (Constant.IncommingCall) {
			flag=1;
			//		if (intent.getAction().equalsIgnoreCase(
			//		"android.intent.action.NEW_OUTGOING_CALL")) {
			//
			telephony = (TelephonyManager) context
			.getSystemService(Context.TELEPHONY_SERVICE);
			phonelistner customPhoneListener = new phonelistner();
			telephony.listen(customPhoneListener,
					customPhoneListener.LISTEN_CALL_STATE);

			Bundle bundle = intent.getExtras();
			String phoneNr = bundle.getString("incoming_number");
			Log.e(TAG, "phoneNr: " + phoneNr);
			try {
				Class c = Class.forName(telephony.getClass().getName());
				Method m = c.getDeclaredMethod("getITelephony");
				m.setAccessible(true);
				telephonyService = (ITelephony) m.invoke(telephony);

				for (int i = 0; i < blocknolist.size(); i++) {
					// String
					// PHnumber=blocknolist.get(i).get("blockno").toString().replace("-","");
					//					Log.e("sss", blocknolist.get(i).get("blockno").toString()
					//							+ "====" + phoneNr + "=");// +PHnumber);
					if (blocknolist.get(i).get(BLOCK_NUMBER).toString()
							.equalsIgnoreCase(phoneNr)) {
						Log.e("status  ", blocknolist.get(i).get(STATUS)
								.toString());
						if (blocknolist.get(i).get(STATUS).toString()
								.equalsIgnoreCase(Constant.STATUS_BOTH_BLOCK)
								|| blocknolist.get(i).get(STATUS).toString()
								.equalsIgnoreCase(Constant.STATUS_ONLY_CALL)) {
							telephonyService.endCall();
							//							Log.e("match ho gaya ", "phone no");
							database.open();
							Calendar cal = Calendar.getInstance();
							String Time = cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE) + " "
							+ cal.get(Calendar.DAY_OF_MONTH) + "-"
							+ cal.get(Calendar.MONTH) + "-"
							+ cal.get(Calendar.YEAR);
							ContentValues initialvalues = new ContentValues();
							initialvalues.put(Constant.MESSAGE_TIMESTAMP,
									Time);
							initialvalues.put(Constant.BLOCK_NO, phoneNr);
							initialvalues
							.put(Constant.CallType, INCOMMING_CALL);
							database.inserteventvalue(
									Constant.DATABASE_BLOCKLIST_CALL_TABLE,
									initialvalues);
							database.close();
						}
					}
				}

			} catch (Exception e) {

			}

		}
	}

	public class phonelistner extends PhoneStateListener {

		private static final String TAG = "PhoneStateListener";

		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// Log.e(TAG, "RINGING");
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// Intent intent=new
				// Intent(IncomingcallrecieverActivity.this,blocklist.class);
				//Log.e(TAG, "IDLE");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//				Log.e(TAG, "offhook");
				break;
			}
		}
	}

	public List<Map<String, Object>> TofetchAllBlockList() {
		List<Map<String, Object>> phonelist = new LinkedList<Map<String, Object>>();

		database.open();
		Cursor cursor = database.fetchAllBlockList();
		database.close();
		if (cursor != null) {
			for (int i = 0; i < cursor.getCount(); i++) {
				Map<String, Object> item = new HashMap<String, Object>();
				String blockno = cursor.getString(
						cursor.getColumnIndexOrThrow(Constant.BLOCK_NO))
						.replace("-", "");
				Log.e("no from db", blockno);
				item.put(BLOCK_NUMBER, blockno);
				item.put(STATUS, cursor.getString(cursor
						.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
				phonelist.add(item);
				cursor.moveToNext();
			}
		}
		return phonelist;
	}
}
