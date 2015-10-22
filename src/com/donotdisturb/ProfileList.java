package com.donotdisturb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.constant.Constant;
import com.database.DataBase;

public class ProfileList extends ListActivity {

	protected static final int DELETE_TASK=101;

//	static boolean mainbtn;
	String Mode="mode";
	String Time="time";
	String Date="date";
	String Text="text";
	String RowId="rowid";

	List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
	int pos;
	static int profileListid;
	DataBase mwDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_list_all);
		mwDB = new DataBase(this);
		showAllList();            
	}


	public void showAllList(){
		list_map.clear();
		mwDB.open();
		Cursor cur=mwDB.fetchAllList2();
		mwDB.close();
		startManagingCursor(cur); 
		cur.moveToFirst();
		Map<String, String> map;
		for(int i=0;i<cur.getCount();i++){
			map= new HashMap<String, String>();
			map.put(RowId,String.valueOf(cur.getInt(cur.getColumnIndexOrThrow(Constant.ROWID))));
			String mode=cur.getString(cur.getColumnIndexOrThrow(Constant.SET_PROFILE_VIBRATE_RING));
			map.put(Mode, mode);

			String date=cur.getString(cur.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
			String[] values=date.split(" ");
			String date2=cur.getString(cur.getColumnIndexOrThrow(Constant.SET_PROFILE_END_TIME));
			String[] endvalue=date2.split(" ");
			String[] Time1=values[1].split(":");
			String[] Time2=endvalue[1].split(":");
			try {
				if(values[0].equalsIgnoreCase(endvalue[0])){
				map.put(Text, "Profile set to <b><font color=\'#ffffff\'>\""+mode+"\"</font></b> for "+values[0]+" from "+Time1[0]+":"+Time1[1]+" to "+Time2[0]+":"+Time2[1]);	
			}else{
				map.put(Text, "Profile set to <b><font color=\'#ffffff\'>\""+mode+"\"</font></b> for "+values[0]+" to "+endvalue[0]+" from "+Time1[0]+":"+Time1[1]+" to "+Time2[0]+":"+Time2[1]);
			}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			
			list_map.add(map);
			cur.moveToNext();
		}
		MyListAdapter mylistAdapter = new MyListAdapter(this, list_map, R.layout.profile_list1,new String[] {Mode,Text}, new int[] { R.id.image,R.id.profile_List}); 
		setListAdapter(mylistAdapter);

	}
	public class MyListAdapter extends SimpleAdapter
	{
		List<Map<String,String>> mData ;
		String[] mFrom;
		int[] mTo;
		LayoutInflater mInflater;
		View view;
		ImageView image;
		TextView data;

		public MyListAdapter(Context context, List<Map<String, String>> data,int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			mData = data;
			mFrom = from;
			mTo = to;
			mInflater = (LayoutInflater) ProfileList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {  
			//			final List<String> dltID= new ArrayList<String>();

			view = mInflater.inflate(R.layout.profile_list1,null, false);

			image=(ImageView) view.findViewById(R.id.image);
			data= (TextView) view.findViewById(R.id.profile_List);

			if(mData.get(position).get(Mode).toString().equalsIgnoreCase("Ring")){
				image.setImageResource(R.drawable.sound_icon);
			}
			if(mData.get(position).get(Mode).toString().equalsIgnoreCase("Vibrate")){
				image.setImageResource(R.drawable.vibration_icon);
			}
			if(mData.get(position).get(Mode).toString().equalsIgnoreCase("Silent")){
				image.setImageResource(R.drawable.silent_icon);
			}
			Log.e("",""+mData.get(position).get(Text)+"=="+getString(R.color.white));
			data.setText(Html.fromHtml(mData.get(position).get(Text).toString()));

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					try{
						pos=Integer.parseInt(mData.get(position).get(RowId));
					}catch (Exception e) {
						e.printStackTrace();
					}
					showDialog(DELETE_TASK);

				}
			});
			if (position % 2 == 0) {
				view.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.contacthint_list_bg1));
			} else {
				view.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.contacthint_list_bg2));
			}
			return view;
		}
	}


	//	dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DELETE_TASK:
			return new AlertDialog.Builder(ProfileList.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Attention!")
			.setMessage("Are you sure, you want to delete the Task")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					mwDB.open();
					mwDB.deleteTaskData2(pos);
					mwDB.close();

					showAllList();
					dialog.dismiss();
					removeDialog(DELETE_TASK);

					mwDB.open();              
					Cursor crsr=mwDB.sortDate2();
					startManagingCursor(crsr);
					mwDB.close();	

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
					removeDialog(DELETE_TASK);
				}
			})	
			.create(); 

		}return null;
	}

	//	set the alarm to open activity
	public void activateProfile(int id){

		profileListid=id;
		mwDB.open();
		Cursor csr=mwDB.fetch_Data2(id);
		startManagingCursor(csr);
		mwDB.close();   

		AlarmManager alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent intent2 = new Intent(ProfileList.this, BackgroundProfileChanger.class);			
		intent2.putExtra("mode",csr.getString(csr.getColumnIndexOrThrow(Constant.SET_PROFILE_VIBRATE_RING)));
		PendingIntent pI1 = PendingIntent.getBroadcast(ProfileList.this, 12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);

		Calendar calendar = Calendar.getInstance();         
		calendar.setTimeInMillis(System.currentTimeMillis());   
		String datefull = csr.getString(csr
				.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");

		try {
			Date convertedDate = dateFormat.parse(datefull);
			calendar.set(Calendar.DATE, convertedDate.getDate());
			calendar.set(Calendar.MONTH, convertedDate.getMonth());
			calendar.set(Calendar.YEAR, convertedDate.getYear() + 1900);
			calendar.set(Calendar.HOUR_OF_DAY, convertedDate.getHours());
			calendar.set(Calendar.MINUTE, convertedDate.getMinutes());
			calendar.set(Calendar.SECOND, convertedDate.getSeconds());

		} catch (ParseException e) {
			e.printStackTrace();
		}
		alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pI1);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

		}
		return super.onKeyDown(keyCode, event);
	}


}
