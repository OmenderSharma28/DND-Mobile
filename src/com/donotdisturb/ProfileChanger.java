//package com.donotdisturb;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.AlertDialog;
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.app.PendingIntent;
//import android.app.TimePickerDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.ImageButton;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import com.constant.Constant;
//import com.database.DataBase;
//
//public class ProfileChanger extends Activity {
//	protected static final int EMPTY_FIELD_DIALOG = 200;
//	protected static final int DATE_DIALOG_ID = 201, TIME_DIALOG_ID = 202;
//	public int BACK = 222;
//	DataBase mwDb;
//	String dialogMsg = "";
//	//	int mHour, mMinute;
//	//	EditText datetext, timetext;
//	String datetext,timetext;
//	RadioButton vibrate, ring, silent;
//	String vibrate_allow = "no", ring_allow = "no", mode = "Ring", lati = "",
//	longi = "",endTime="",endDate="",startDate="",startTime="";
//	static int profileid;
//	Calendar cal;
//	int mMonth, mYear, mDay, Hour, Minute;
//	TextView day_txv, month_txv, year_txv, mHour_txv, mMinute_txv;
//	Boolean Am_Pm = true;
//	ImageButton image;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.profile_changer);
//
//		mwDb = new DataBase(this);
//
//		day_txv = (TextView) findViewById(R.id.date);
//		month_txv = (TextView) findViewById(R.id.month);
//		year_txv = (TextView) findViewById(R.id.year);
//		mHour_txv = (TextView) findViewById(R.id.hour);
//		mMinute_txv = (TextView) findViewById(R.id.minute);
//		image = (ImageButton) findViewById(R.id.AmPm);
//		ring = (RadioButton) findViewById(R.id.radio0);
//		silent = (RadioButton) findViewById(R.id.radio1);
//		vibrate = (RadioButton) findViewById(R.id.radio2);
//
//
//		cal = Calendar.getInstance();
//		mYear = cal.get(Calendar.YEAR);
//		mMonth = cal.get(Calendar.MONTH);
//		mDay = cal.get(Calendar.DAY_OF_MONTH);
//
//		Hour = cal.get(Calendar.HOUR_OF_DAY);
//		Minute = cal.get(Calendar.MINUTE);
//
//		display();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == BACK && ProfileList.mainbtn == false) {
//			//			finish();
//		} else {
//			// do nothing
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//	}
//
//	// set alarm to open the activity
//	public void activateProfile(int id) {
//
//		profileid = id;
//		mwDb.open();
//		Cursor csr = mwDb.fetch_Data2(id);
//		startManagingCursor(csr);
//		mwDb.close();
//
//		// int date=csr.getInt(csr.getColumnIndexOrThrow(Constant.SET_DAY));
//		// int month=csr.getInt(csr.getColumnIndexOrThrow(Constant.SET_MONTH));
//		// int year=csr.getInt(csr.getColumnIndexOrThrow(Constant.SET_YEAR));
//		// int hour=csr.getInt(csr.getColumnIndexOrThrow(Constant.SET_HOUR));
//		// int minutes=csr.getInt(csr.getColumnIndexOrThrow(Constant.SET_MIN));
//
//		AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
//		Intent intent2 = new Intent(ProfileChanger.this,
//				BackgroundProfileChanger.class);
//		intent2.putExtra("id",
//				csr.getString(csr.getColumnIndexOrThrow(Constant.ROWID)));
//		intent2.putExtra("profile_start", true);
//		intent2.putExtra("gpsRun", true);
//		intent2.putExtra("true", "yes");
//		PendingIntent pI1 = PendingIntent.getBroadcast(ProfileChanger.this,
//				12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//
//		String datefull = csr.getString(csr
//				.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//		"yyyy-MM-dd hh:mm:ss");
//
//		try {
//			Date convertedDate = dateFormat.parse(datefull);
//			calendar.set(Calendar.DATE, convertedDate.getDate());
//			calendar.set(Calendar.MONTH, convertedDate.getMonth());
//			calendar.set(Calendar.YEAR, convertedDate.getYear() + 1900);
//			calendar.set(Calendar.HOUR_OF_DAY, convertedDate.getHours());
//			calendar.set(Calendar.MINUTE, convertedDate.getMinutes());
//			calendar.set(Calendar.SECOND, convertedDate.getSeconds());
//			// Log.e("for profile changer  date  to cheack: month : year  ::  hour : minutes",
//			// convertedDate.getDate()+" : "+ convertedDate.getMonth()+" : "+
//			// convertedDate.getYear()+ "  :  "+
//			// convertedDate.getHours()+" : "+convertedDate.getMinutes()+"");
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
//		alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//				pI1);
//		finish();
//	}
//
//	private static String pad(int c) {
//		if (c >= 10)
//			return String.valueOf(c);
//		else
//			return "0" + String.valueOf(c);
//	}
//
//	// ========================onClick Event========================
//	public void click(View view) {
//		int limit = MonthDates(mMonth);
//		switch (view.getId()) {
//		case R.id.dateplus:
//			mDay = mDay + 1;
//			if (mDay > limit)
//				mDay = 1;
//			display();
//			break;
//		case R.id.monthplus:
//			mMonth = mMonth + 1;
//			if (mMonth >= 12)
//				mMonth = 0;
//			if(mDay>MonthDates(mMonth))mDay=MonthDates(mMonth);
//			display();
//			break;
//		case R.id.yearplus:
//			mYear = mYear + 1;
//			display();
//			break;
//		case R.id.dateDesc:
//			mDay = mDay - 1;
//			if (mDay <= 0)
//				mDay = limit;
//			display();
//			break;
//		case R.id.monthDesc:
//			mMonth = mMonth - 1;
//			if (mMonth < 0)
//				mMonth = 11;
//			if(mDay>MonthDates(mMonth))mDay=MonthDates(mMonth);
//			display();
//			break;
//		case R.id.yearDesc:
//			mYear = mYear - 1;
//			//			Log.e("", "" + (Calendar.getInstance()).get(Calendar.YEAR));
//			display();
//			break;
//		case R.id.hourplus:
//			Hour = Hour + 1;
//			if (Hour > 12)
//				Hour = 1;
//			//			System.out.println("HOur is "+Hour);
//			display();
//			break;
//		case R.id.minuteplus:
//			Minute = Minute + 1;
//			if (Minute >= 60)
//				Minute = 0;
//			//			System.out.println("Minute is "+Minute);
//			display();
//			break;
//		case R.id.hourDesc:
//			Hour = Hour - 1;
//			if (Hour <= 0)
//				Hour = 12;
//			display();
//			break;
//		case R.id.minDesc:
//			Minute = Minute - 1;
//			if (Minute < 0)
//				Minute = 59;
//			display();
//			break;
//		case R.id.btnsave:
//			//			Log.e("","Now hour is"+Hour);
//			if(!Am_Pm){ if(Hour<12){
//				Hour=Hour+12; }}
//			else{
//				if(Hour==12){
//					Hour=0;
//				} else if(Hour>24) Hour=Hour-12;
//			}
//			//			Log.e("","after setting hour is"+Hour);
//			datetext=mDay+"-"+updateDate(mMonth)+"-"+mYear;
//			timetext=pad(Hour)+":"+pad(Minute);
//			startTime=pad(Hour)+":"+pad(Minute)+":15";
//			startDate=mYear + "-" + pad(mMonth + 1) + "-" + pad(mDay);
//			if(endDate.equalsIgnoreCase("")){
//				endDate=startDate;
//				//				Toast.makeText(getApplicationContext(), "Date not set yet", Toast.LENGTH_SHORT).show();
//			}
//			if(endTime.equalsIgnoreCase("")){
//				Toast.makeText(getApplicationContext(), "Profile End Time Not Set Yet", Toast.LENGTH_SHORT).show();
//				break;
//			}
//
//			cal = Calendar.getInstance();
//			String hr = String.valueOf(Hour);// timetext.getText().toString().substring(0,
//			// 2);
//			String min = String.valueOf(Minute);// timetext.getText().toString().substring(3);
//			if (mYear < cal.get(Calendar.YEAR)) {
//				dialogMsg = "Sorry, you entered wrong date";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} else if (mYear == cal.get(Calendar.YEAR)
//					&& mMonth < cal.get(Calendar.MONTH)) {
//				dialogMsg = "Sorry, you entered wrong date";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} else if (mYear == cal.get(Calendar.YEAR)
//					&& mMonth == cal.get(Calendar.MONTH)
//					&& mDay < cal.get(Calendar.DATE)) {
//				dialogMsg = "Sorry, you entered wrong date";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} else if (mYear == cal.get(Calendar.YEAR)
//					&& mMonth == cal.get(Calendar.MONTH)
//					&& mDay == cal.get(Calendar.DATE)
//					&& Integer.parseInt(hr) < cal.get(Calendar.HOUR_OF_DAY)) {
//				dialogMsg = "Sorry,you entered wrong time";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} else if (mYear == cal.get(Calendar.YEAR)
//					&& mMonth == cal.get(Calendar.MONTH)
//					&& mDay == cal.get(Calendar.DATE)
//					&& Integer.parseInt(hr) == cal
//					.get(Calendar.HOUR_OF_DAY)
//					&& Integer.parseInt(min) < cal.get(Calendar.MINUTE)) {
//				dialogMsg = "Sorry, you entered wrong time";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} else if(chekTime(startDate+" "+startTime,endDate+" "+endTime)){
//				dialogMsg="Sorry, you entered worng end time";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			}else if (vibrate.isChecked() == false
//
//					&& ring.isChecked() == false && silent.isChecked()==false ) {
//				dialogMsg = "Please, click Vibrate or Ring";
//				showDialog(EMPTY_FIELD_DIALOG);
//				break;
//			} 
//
//			if (vibrate.isChecked()) {
//				//				Log.e("","vibration cheked");
//				mode = "Vibrate";
//			} else if(ring.isChecked()) {
//				//				Log.e("","ring cheked");
//				mode = "Ring";
//			}else{
//				mode ="Silent";
//				//				Log.e("","silent cheked");
//			}
//			//			System.out.println("date is :"+datetext);
//			//			System.out.println("date is :"+endDate+"now day is"+mDay);
//			//			System.out.println("time is :"+timetext);
//			//			System.out.println("mode is :"+mode);
//
//			mwDb.open();
//			Boolean insert=true;
//			Cursor cursor=mwDb.fetchAllProfile();
//			startManagingCursor(cursor);
//			Log.e("Profile change Activity","@@@@@@@@@@"+cursor.getCount());
//			if(cursor.getCount()>0){
//				cursor.moveToFirst();
//				do {
//					String datefull = cursor.getString(cursor
//							.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));
//					String endfulldate=cursor.getString(cursor.getColumnIndexOrThrow(Constant.SET_PROFILE_END_TIME));
//					Boolean flag= checktime(datefull, endfulldate,
//							mYear + "-" + pad(mMonth + 1) + "-" + pad(mDay)
//							+ " " + pad(Hour) + ":" + pad(Minute)
//							+ ":15",endDate+" "+endTime);
//					if(!flag){
//						Toast.makeText(getApplicationContext(), "Have Been Scheduled", Toast.LENGTH_SHORT).show();
//						insert=false;
//						break;
//					}
//
//				}while (cursor.moveToNext());
//			}
//
//			if(!insert){
//				mwDb.close();
//				cursor.close();
//				break;
//			}
//			else
//			{
////				mwDb.insertProfileChangerData(Constant.DEFAULT_USERID,
////						Integer.toString(mDay), Integer
////						.toString(mMonth + 1), Integer
////						.toString(mYear), datetext, timetext,
////						mYear + "-" + pad(mMonth + 1) + "-" + pad(mDay)
////						+ " " + pad(Hour) + ":" + pad(Minute)
////						+ ":15", hr, min, mode,endDate+" "+endTime);
//			}
//			mwDb.close();
//
//			mwDb.open();
//			Cursor crsr = mwDb.sortDate2();
//			startManagingCursor(crsr);
//			mwDb.close();
//			Toast.makeText(getApplicationContext(), "Profile saved",
//					Toast.LENGTH_SHORT).show();
//			if (crsr.getCount() > 0) {
//				for (int i = 0; i < crsr.getCount(); i++) {
//					// Log.w("ROWID, FULL",crsr.getString(crsr.getColumnIndexOrThrow(MobileWatchDBAdapter.ROWID))+","
//					// +crsr.getString(crsr.getColumnIndexOrThrow(MobileWatchDBAdapter.SET_PROFILE_FULL_DATE_TIME)));
//					crsr.moveToNext();
//				}
//				crsr.moveToFirst();
//				activateProfile(Integer.parseInt(crsr.getString(crsr
//						.getColumnIndexOrThrow(Constant.ROWID))));
//			}
//			endDate="";
//			break;
//
//		case R.id.AmPm:
//
//			if (!Am_Pm) {
//				image.setBackgroundResource(R.drawable.am_btn);
//				Am_Pm = !Am_Pm;
//			} else {
//				image.setBackgroundResource(R.drawable.pm_btn);
//
//				Am_Pm = !Am_Pm;
//			}
//			//			Log.e("", "Am is" + Am_Pm);
//			break;
//		case R.id.endDate:
//			showDialog(DATE_DIALOG_ID);
//			break;
//		case R.id.endTime:
//			showDialog(TIME_DIALOG_ID);
//			break;
//		case R.id.btndelete:
//			Toast.makeText(getApplicationContext(), "Deleted",
//					Toast.LENGTH_SHORT).show();
//			finish();
//			break;
//		case R.id.btnviewlist:
//			Intent i= new Intent(getBaseContext(),ProfileList.class);
//			startActivityForResult(i,BACK);
//			break;
//		}
//	}
//	private boolean chekTime(String startdate, String enddate) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//		try{ Date start=dateFormat.parse(startdate);
//		Date endDate=dateFormat.parse(enddate);
//		System.out.println("@@@@"+startdate+"==="+start+"");
//		System.out.println("@@@@"+enddate+"==="+endDate+"");
//		return endDate.before(start);
//		}
//		catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	private Boolean checktime(String start, String end,String object,String object2){
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//
//		try {
//			Date DateStart = dateFormat.parse(start);
//			Date DateEnd=dateFormat.parse(end);
//			Date current=dateFormat.parse(object);
//			Date current2=dateFormat.parse(object2);
//
//			Boolean flag1=current.after(DateStart);
//			Boolean flag2=current.before(DateEnd);
//
//			Boolean flag3=current2.after(DateStart);
//			Boolean flag4=current2.before(DateEnd);
//			if(flag1 && flag2){
//				return false;
//			}else{
//				if(flag3 && flag4){
//					return false;
//				}
//
//				else {
//					return true;
//				}
//			}
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//			//			return true;
//		}	
//		return true;
//	}
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case DATE_DIALOG_ID:
//			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);
//		case TIME_DIALOG_ID:
//			return new TimePickerDialog(this,mTimeSetListener, Hour, Minute, true);
//		case EMPTY_FIELD_DIALOG:
//			return new AlertDialog.Builder(ProfileChanger.this)
//			.setIcon(android.R.drawable.ic_dialog_alert)
//			.setTitle("Attention!")
//			.setMessage(dialogMsg)
//			.setCancelable(false)
//			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) 
//				{
//					dialog.dismiss();
//					removeDialog(EMPTY_FIELD_DIALOG);
//				}
//			})		
//			.create(); 
//		}
//		return null;
//	}
//	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			endTime=pad(hourOfDay)+":"+pad(minute)+ ":00";
//			System.out.println("LIstener"+endTime);
//		}
//	};
//	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//		public void onDateSet(DatePicker view, int year, int monthOfYear,
//				int dayOfMonth) {
//			endDate=year+"-"+pad(monthOfYear+1)+"-"+pad(dayOfMonth);
//		}
//	};
//
//	protected void display() {
//
//		day_txv.setText(String.valueOf(mDay));
//		month_txv.setText(updateDate(mMonth));
//		year_txv.setText(String.valueOf(mYear));
//		if(Hour>12){
//			Hour=Hour-12;
//			Am_Pm=false;
//			image.setBackgroundResource(R.drawable.pm_btn);
//		}
//		if(Hour==0){
//			Hour=12;
//			image.setBackgroundResource(R.drawable.am_btn); }
//		mHour_txv.setText(pad(Hour));
//		mMinute_txv.setText(pad(Minute));
//		System.out.print("Date is now"+Hour+":"+Minute);
//		System.out.println(Am_Pm?"AM":"PM"+"And date is"+mYear+"-"+mMonth+"-"+mDay);
//	}
//
//	private String updateDate(int month) {
//		String monthName = "Jan";
//		if (month == 0) {
//			monthName = "Jan";
//		} else if (month == 1) {
//			monthName = "Feb";
//		} else if (month == 2) {
//			monthName = "Mar";
//		} else if (month == 3) {
//			monthName = "Apr";
//		} else if (month == 4) {
//			monthName = "May";
//		} else if (month == 5) {
//			monthName = "Jun";
//		} else if (month == 6) {
//			monthName = "Jul";
//		} else if (month == 7) {
//			monthName = "Aug";
//		} else if (month == 8) {
//			monthName = "Sep";
//		} else if (month == 9) {
//			monthName = "Oct";
//		} else if (month == 10) {
//			monthName = "Nov";
//		} else if (month == 11) {
//			monthName = "Dec";
//		}
//		return monthName;
//	}
//
//	private int MonthDates(int month) {
//		int days = 0;
//
//		if (month == 0) {
//			days = 31;
//		} else if (month == 1) {
//			if (isleapyear(mYear))
//				days = 29;
//			else
//				days = 28;
//		} else if (month == 2) {
//			days = 31;
//		} else if (month == 3) {
//			days = 30;
//		} else if (month == 4) {
//			days = 31;
//		} else if (month == 5) {
//			days = 30;
//		} else if (month == 6) {
//			days = 31;
//		} else if (month == 7) {
//			days = 31;
//		} else if (month == 8) {
//			days = 30;
//		} else if (month == 9) {
//			days = 31;
//		} else if (month == 10) {
//			days = 30;
//		} else if (month == 11) {
//			days = 31;
//		}
//		return days;
//	}
//
//	private boolean isleapyear(int year) {
//		if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
//			return true;
//		}
//		return false;
//
//	}
//}
