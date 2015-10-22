package com.donotdisturb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.constant.Constant;
import com.database.DataBase;


public class Profile extends Activity {
	
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHours;
	private int mMinutes;
	private TextView mDateStartDisplay,mDateEndDisplay;
	private TextView mTimeStartDispaly,mTimeEndDisplay;
	private Boolean enddate=false,endTimeSet=false;
	private RadioButton vibrate, ring, silent,vibrate_ring;
	private String _DateFormate="yyyy-MM-dd hh:mm:ss";
	private DataBase database;
//	private static int profileid;
	private String dialogMsg = "";
	private String mode = "Ring";
	private String startTime;//=mTimeStartDispaly.getText().toString()+":15";//pad(Hour)+":"+pad(Minute)+":15";
	private String startDate;//=mDateStartDisplay.getText().toString();
	private String endTime;//=mTimeEndDisplay.getText().toString()+":00";
	private String endDate;//=mDateEndDisplay.getText().toString();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_select);
		Calendar c=Calendar.getInstance();
		database=new DataBase(this);
		mDateStartDisplay=(TextView)findViewById(R.id.txv_start_date);
		mDateEndDisplay=(TextView)findViewById(R.id.txv_end_date);
		mTimeStartDispaly=(TextView)findViewById(R.id.txv_time_start);
		mTimeEndDisplay=(TextView)findViewById(R.id.txv_end_time);

		mDateStartDisplay.setInputType(InputType.TYPE_NULL);
		mDateEndDisplay.setInputType(InputType.TYPE_NULL);
		mTimeStartDispaly.setInputType(InputType.TYPE_NULL);
		mTimeEndDisplay.setInputType(InputType.TYPE_NULL);

		//		mTimeStartDispaly.setEnabled(false);
		//		mDateEndDisplay.setEnabled(false);
		//		mDateStartDisplay.setEnabled(false);
		//		mTimeEndDisplay.setEnabled(false);

		ring = (RadioButton) findViewById(R.id.radio0);
		silent = (RadioButton) findViewById(R.id.radio1);
		vibrate = (RadioButton) findViewById(R.id.radio2);
		vibrate_ring=(RadioButton)findViewById(R.id.radio3);

		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHours= c.get(Calendar.HOUR_OF_DAY);
		mMinutes= c.get(Calendar.MINUTE);

		mDateStartDisplay.setText(pad(mDay)+"-"+getMonth(mMonth)+"-"+mYear);
		mDateEndDisplay.setText(pad(mDay)+"-"+getMonth(mMonth)+"-"+mYear);
		mTimeStartDispaly.setText(pad(mHours)+":"+pad(mMinutes));
		mTimeEndDisplay.setText(pad(mHours)+":"+pad(mMinutes));

		startDate=mYear+"-"+pad(mMonth+1)+"-"+pad(mDay);
		startTime=pad(mHours)+":"+pad(mMinutes)+":15";
		endTime=pad(mHours)+":"+pad(mMinutes)+":00";
	}
	public void click(View view){
		switch (view.getId()) {
		case R.id.btn_showlist:
			Intent intent= new Intent(getBaseContext(),ProfileList.class);
			startActivity(intent);
			//			startActivityForResult(i,BACK);
			break;
		
		case R.id.btn_saveProfile:
			if(startDate==null){
				Toast.makeText(getApplicationContext(), "Profile Start Time Not Set Yet", Toast.LENGTH_SHORT).show();
				Log.e(""," startDate is null");
				break;
			}
			if(endDate==null || endDate.equalsIgnoreCase("")){
				endDate=startDate;
			}
			//			else  if(startTime==null){
			//				Toast.makeText(getApplicationContext(), "Profile Start Time Not Set Yet", Toast.LENGTH_SHORT).show();
			//				Log.e(""," startTime is null");
			//				break;
			//			}else
			if(endTime==null){
				Toast.makeText(getApplicationContext(), "Profile End Time Not Set Yet", Toast.LENGTH_SHORT).show();
				Log.e(""," endTime is null");
				break;
			}
			Calendar cal = Calendar.getInstance();
			if (mYear < cal.get(Calendar.YEAR)) {
				dialogMsg = getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth < cal.get(Calendar.MONTH)) {
				dialogMsg = getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay < cal.get(Calendar.DATE)) {
				dialogMsg = getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay == cal.get(Calendar.DATE)
					&& mHours < cal.get(Calendar.HOUR_OF_DAY)) {
				dialogMsg = getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay == cal.get(Calendar.DATE)
					&& mHours == cal
					.get(Calendar.HOUR_OF_DAY)
					&& mMinutes < cal.get(Calendar.MINUTE)) {
				dialogMsg = getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} else if(chekTime(startDate+" "+startTime,endDate+" "+endTime)){
				dialogMsg=getResources().getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
				break;
			} 
			if (vibrate.isChecked()) {
//				Log.e("","vibration cheked");
				mode = Constant.VIBRATE;
			} else if(ring.isChecked()) {
//				Log.e("","ring cheked");
				mode = Constant.RING;
			}else if(silent.isChecked()){
				mode =Constant.SILENT;
//				Log.e("","silent cheked");
			}else if(vibrate_ring.isChecked()){
				mode=Constant.VIBRATE_RING;
			}

			database.open();
			Boolean insert=true;
			Cursor cursor=database.fetchAllProfile();
			startManagingCursor(cursor);
			Log.e("Profile change Activity","@@@@@@@@@@"+cursor.getCount());
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				do {
					String datefull = cursor.getString(cursor
							.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
					String endfulldate=cursor.getString(cursor.getColumnIndexOrThrow(Constant.SET_PROFILE_END_TIME));
					Boolean flag= checktime(datefull, endfulldate,
							startDate+ " " +startTime,endDate+" "+endTime);
					if(!flag){
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.has_scheduled), Toast.LENGTH_SHORT).show();
						insert=false;
						break;
					}

				}while (cursor.moveToNext());
			}

			if(!insert){
				database.close();
				cursor.close();
				break;
			}
			else
			{
				database.insertProfileChangerData(startDate+ " " +startTime, mode,endDate+" "+endTime);
			}
			database.close();

			database.open();

			Log.e("",""+database.fetchAllList2().getCount());
			Cursor crsr = database.sortDate2();
			startManagingCursor(crsr);
			database.close();
			Toast.makeText(getApplicationContext(), "Profile saved",
					Toast.LENGTH_SHORT).show();
			if (crsr.getCount() > 0) {
				for (int i = 0; i < crsr.getCount(); i++) {
					Log.e("",""+crsr.getString(crsr.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME)));
					crsr.moveToNext();
				}
				crsr.moveToFirst();
				Log.e("",""+crsr.getString(crsr
						.getColumnIndexOrThrow(Constant.ROWID)));
				activateProfile(Integer.parseInt(crsr.getString(crsr
						.getColumnIndexOrThrow(Constant.ROWID))));
			}
			endDate="";
			break;
			//			break;

		case R.id.txv_start_date:
			enddate=false;
			showDialog(Constant.DATE_DIALOG_ID);

			break;
		case R.id.txv_end_date:
			enddate=true;
			showDialog(Constant.DATE_DIALOG_ID);
			break;
		case R.id.txv_time_start:
			endTimeSet=false;
			showDialog(Constant.TIME_DIALOG_ID);
			break;
		case R.id.txv_end_time:
			endTimeSet=true;
			showDialog(Constant.TIME_DIALOG_ID);
			break;
		
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case Constant.TIME_DIALOG_ID:
			return new TimePickerDialog(this,mTimeSetListener, mHours, mMinutes, true);
		case Constant.EMPTY_FIELD_DIALOG:
			return new AlertDialog.Builder(Profile.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Attention!")
			.setMessage(dialogMsg)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					dialog.dismiss();
					removeDialog(Constant.EMPTY_FIELD_DIALOG);
				}
			})		
			.create();
		}
		return null;
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			String date=mDay+"-"+getMonth(mMonth)+"-"+mYear;
			if(!enddate){
				mDateStartDisplay.setText(date);
				startDate=mYear+"-"+pad(mMonth+1)+"-"+pad(mDay);

			}else{
				mDateEndDisplay.setText(date);
				endDate=mYear+"-"+pad(mMonth+1)+"-"+pad(mDay);//mDay+"-"+(mMonth+1)+"-"+mYear;
			}
		}
	};
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHours = hourOfDay;
			mMinutes = minute;
			String Time=pad(mHours)+":"+pad(mMinutes);
			if(!endTimeSet){
				mTimeStartDispaly.setText(Time);
				startTime=Time+":15";
			}else{
				mTimeEndDisplay.setText(Time);
				endTime=Time+":00";
			}
		}
	};
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	// set alarm to open the activity
	public void activateProfile(int id) {

//		profileid = id;
		database.open();
		Cursor csr = database.fetch_Data2(id);
		startManagingCursor(csr);
		database.close();

		AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent2 = new Intent(Profile.this,
				BackgroundProfileChanger.class);
		intent2.putExtra(Constant.KEY_ID,
				csr.getString(csr.getColumnIndexOrThrow(Constant.ROWID)));
		intent2.putExtra(Constant.KEY_PROFILE_START, true);
		PendingIntent pI1 = PendingIntent.getBroadcast(Profile.this,
				12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		String datefull = csr.getString(csr
				.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
		SimpleDateFormat dateFormat = new SimpleDateFormat(_DateFormate);

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

		alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pI1);
		finish();
	}
	private boolean chekTime(String startdate, String enddate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(_DateFormate);
		try{ Date start=dateFormat.parse(startdate);
		Date endDate=dateFormat.parse(enddate);
		System.out.println("@@@@"+startdate+"==="+start+"");
		System.out.println("@@@@"+enddate+"==="+endDate+"");
		return endDate.before(start);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	private Boolean checktime(String start, String end,String object,String object2){
		SimpleDateFormat dateFormat = new SimpleDateFormat(_DateFormate);

		try {
			Date DateStart = dateFormat.parse(start);
			Date DateEnd=dateFormat.parse(end);
			Date current=dateFormat.parse(object);
			Date current2=dateFormat.parse(object2);

			Boolean flag1=current.after(DateStart);
			Boolean flag2=current.before(DateEnd);

			Boolean flag3=current2.after(DateStart);
			Boolean flag4=current2.before(DateEnd);
			if(flag1 && flag2){
				return false;
			}else{
				if(flag3 && flag4){
					return false;
				}

				else {
					return true;
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
			//			return true;
		}	
		return true;
	}
	private String getMonth(int month) {
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

}
