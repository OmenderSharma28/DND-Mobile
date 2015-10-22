package com.donotdisturb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.constant.Constant;
import com.database.DataBase;

public class ScheduleMessageHistory extends ListActivity {
	private String RowId="rowId",Message="message",Status="status",Date="date",PhoneNo="phoneNumber",User="user";
	private DataBase database;
	private List<Map<String,String>> tosendMessageList=new LinkedList<Map<String, String>>();
	private int dtlrowID;
	private MyListAdapter mylistAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database=new DataBase(this);
		setContentView(R.layout.schedule_list);
		TofetchAllMsgList();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		dtlrowID=position;
		showDialog(Constant.DELETE_MESSAGE);
	}
	private void  TofetchAllMsgList(){   
		tosendMessageList.clear();
		database.open();
		Cursor cursor=database.fetchAllMessageReminderList();
		database.close();
		Log.e("","@@@@@@@"+cursor.getCount());
		if(cursor!=null)
		{
			for(int i=0;i<cursor.getCount();i++)
			{
				if(cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_STATUS)).equalsIgnoreCase("To be send")){
				Map<String, String> item=new HashMap<String, String>();
				item.put(RowId,cursor.getString(cursor.getColumnIndexOrThrow(Constant.ROWID)));
				item.put(Message,cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_TO_SEND)));
				item.put(PhoneNo,cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENO_FORMESSAGE)));
				item.put(Status,cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_STATUS)));
				Log.e("","Status is "+cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_STATUS)));
				item.put(Date,cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_SENDING_DATE)));
				item.put(User,cursor.getString(cursor.getColumnIndexOrThrow(Constant.MobileUser)));
				//					if(cursor.getString(cursor.getColumnIndexOrThrow(Constant.MobileUser)).equalsIgnoreCase("Un))
				tosendMessageList.add(item);
				}
				cursor.moveToNext();
			}
			mylistAdapter= new MyListAdapter(ScheduleMessageHistory.this, tosendMessageList, R.layout.send_message_item,new String[]{"message","status","date"}, new int[]{R.id.msgtextView,R.id.booleantextView,R.id.dt_tmtextView});
			setListAdapter(mylistAdapter);
		}
		cursor.close();

	}
	public void activateProfile(int id){
		//		profileid1=id;
		database.open();
		Cursor csr=database.fetch_DataMessage2(id);
		startManagingCursor(csr);
		database.close();   
		AlarmManager alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent intent2 = new Intent(ScheduleMessageHistory.this, TosendMessage.class);			
		intent2.putExtra(Constant.KEY_ID,csr.getString(csr.getColumnIndexOrThrow(Constant.ROWID)));
		intent2.putExtra("gpsRun",true);
		PendingIntent pI1 = PendingIntent.getBroadcast(ScheduleMessageHistory.this, 12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		Calendar calendar = Calendar.getInstance();         
		calendar.setTimeInMillis(System.currentTimeMillis());   				
		String datefull=csr.getString(csr.getColumnIndexOrThrow(Constant.MESSAGE_SENDING_DATE));
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
		finish();
	}
	public class MyListAdapter extends SimpleAdapter {
		List<Map<String, String>> mData;
		String[] mFrom;
		int[] mTo;
		LayoutInflater mInflater;
		View view;

		TextView name,month,date;
		TextView message;

		public MyListAdapter(Context context, List<Map<String, String>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			mData = data;
			mFrom = from;
			mTo = to;
			mInflater = (LayoutInflater) ScheduleMessageHistory.this
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			view = mInflater.inflate(R.layout.send_message_item, null, false);
			name = (TextView) view.findViewById(R.id.txvname);
			message = (TextView) view.findViewById(R.id.txvMessage);
			month=(TextView)view.findViewById(R.id.txvmonth);
			date=(TextView)view.findViewById(R.id.txvdate);
			String dates=mData.get(position).get(Date);
			String[] dateparse=dates.split(" ");
			String[] date1=dateparse[0].split("-");
			month.setText(updateDate(Integer.parseInt(date1[1])));
			date.setText(date1[2]);
			if(mData.get(position).get(User).equalsIgnoreCase("Unknown")){
				name.setText(mData.get(position).get(PhoneNo));
			}
			else{
				name.setText(mData.get(position).get(User));
			}
			message.setText(mData.get(position).get(Message));
			return view;
		}
	}
	private String updateDate(int month) {
		month-=1;
		String monthName = "Jan";
		if (month == 0) {
			monthName = "Jan";
		} else if (month == 1) {
			monthName = "Feb";
		} else if (month == 2) {
			monthName = "Mar";
		} else if (month == 3) {
			monthName = "Apr";
		} else if (month == 4) {
			monthName = "May";
		} else if (month == 5) {
			monthName = "Jun";
		} else if (month == 6) {
			monthName = "Jul";
		} else if (month == 7) {
			monthName = "Aug";
		} else if (month == 8) {
			monthName = "Sep";
		} else if (month == 9) {
			monthName = "Oct";
		} else if (month == 10) {
			monthName = "Nov";
		} else if (month == 11) {
			monthName = "Dec";
		}
		return monthName;
	}
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DELETE_MESSAGE:
			return new AlertDialog.Builder(ScheduleMessageHistory.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(getString(R.string.attention))
			.setMessage(getString(R.string.sure_to_delete))
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					database.open();
					database.deleteSendingMessage(Integer.parseInt(tosendMessageList.get(dtlrowID).get(RowId).toString()));
					database.close();
					tosendMessageList.remove(dtlrowID);
					mylistAdapter.notifyDataSetChanged();					
					dialog.dismiss();
					removeDialog(Constant.DELETE_MESSAGE);

					database.open();              
					Cursor crsr=database.sortDateMessage();
					startManagingCursor(crsr);
					database.close();	

					for (int i = 0; i < crsr.getCount(); i++) 
					{
						crsr.moveToNext();
					}
					crsr.moveToFirst();	
					if(crsr.getCount()>0)
					{
						activateProfile(Integer.parseInt(crsr.getString(crsr.getColumnIndexOrThrow(Constant.ROWID))));	
					}
				}
			})	
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					dialog.dismiss();
					removeDialog(Constant.DELETE_MESSAGE);
				}
			})	
			.create(); 

		}return null;
	}


}
