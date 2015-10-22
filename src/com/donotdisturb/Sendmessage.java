package com.donotdisturb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.constant.Constant;
import com.database.DataBase;

public class Sendmessage extends Activity {
//	protected static final int EMPTY_FIELD_DIALOG = 103;// CONTACTS = 102;EMPTY_FIELD_DIALOG
//	protected static final int DATE_DIALOG_ID = 101, TIME_DIALOG_ID = 102;
	private TextView day_txv, month_txv, year_txv, mHour_txv, mMinute_txv;
	private EditText number, message;
	private Calendar cal;
	private int mMonth, mYear, mDay, Hour, Minute;
	private ImageButton image;
	private Boolean Am_Pm = true;
	private DataBase database;
	private String dialogMsg = "";
	private String NAME="name",NUMBER="number",UNKNOWN="Unknown";
	private List<String> list= new ArrayList<String>();
	private List<Map<String,String>> contact= new ArrayList<Map<String,String>>();
	private Button btn_contact;
	private ProgressDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_message);
		day_txv = (TextView) findViewById(R.id.date);
		month_txv = (TextView) findViewById(R.id.month);
		year_txv = (TextView) findViewById(R.id.year);
		mHour_txv = (TextView) findViewById(R.id.hour);
		mMinute_txv = (TextView) findViewById(R.id.minute);
		image = (ImageButton) findViewById(R.id.AmPm);
		btn_contact=(Button) findViewById(R.id.Contacts);
		btn_contact.setEnabled(false);
		number = (EditText) findViewById(R.id.edtMobile);
		message = (EditText) findViewById(R.id.edtMessage);

		cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);

		database = new DataBase(Sendmessage.this);

		Hour = cal.get(Calendar.HOUR_OF_DAY);
		Minute = cal.get(Calendar.MINUTE);
		Disable_user_Intration();
		contactList();
		display();
	}

	public void click(View view) {
		int limit = MonthDates(mMonth);
		switch (view.getId()) {
		case R.id.dateplus:
			mDay = mDay + 1;
			if (mDay > limit)
				mDay = 1;
			display();
			break;
		case R.id.monthplus:
			mMonth = mMonth + 1;
			if (mMonth >= 12)
				mMonth = 0;
			if(mDay>MonthDates(mMonth))mDay=MonthDates(mMonth);
			display();
			break;
		case R.id.yearplus:
			mYear = mYear + 1;
			display();
			break;
		case R.id.dateDesc:
			mDay = mDay - 1;
			if (mDay <= 0)
				mDay = limit;
			display();
			break;
		case R.id.monthDesc:
			mMonth = mMonth - 1;
			if (mMonth < 0)
				mMonth = 11;
			if(mDay>MonthDates(mMonth))mDay=MonthDates(mMonth);
			display();
			break;
		case R.id.yearDesc:
			mYear = mYear - 1;
			Log.e("", "" + (Calendar.getInstance()).get(Calendar.YEAR));
			display();
			break;

		case R.id.hourplus:
			Hour = Hour + 1;
			if (Hour > 12)
				Hour = 1;
			display();
			break;
		case R.id.minuteplus:
			Minute = Minute + 1;
			if (Minute >= 60)
				Minute = 0;
			display();
			break;
		case R.id.hourDesc:
			Hour = Hour - 1;
			if (Hour <= 0)
				Hour = 12;
			display();
			break;
		case R.id.minDesc:
			Minute = Minute - 1;
			if (Minute < 0)
				Minute = 59;
			display();
			break;
		case R.id.AmPm:
			Am_Pm = !Am_Pm;
			if (!Am_Pm) {
				image.setBackgroundResource(R.drawable.pm_btn);
			} else {
				image.setBackgroundResource(R.drawable.am_btn);
			}

			break;
		case R.id.btnsave:
			if(!Am_Pm)
			{
				Hour=Hour+12;
			if(Hour==24) 
				Hour=0;
			}
			if(Hour>24) Hour=Hour-24;
			Log.e(" TIme is ",""+Hour+"-"+Minute+"-"+mDay+"-"+mMonth+"-"+mYear);
			cal = Calendar.getInstance();
			if (number.getText().toString().trim().equalsIgnoreCase("")) {
				dialogMsg =getString(R.string.add_mobile_number);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else if (mYear < cal.get(Calendar.YEAR)) {
				dialogMsg = getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth < cal.get(Calendar.MONTH)) {
				dialogMsg = getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay < cal.get(Calendar.DATE)) {
				dialogMsg = getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay == cal.get(Calendar.DATE)
					&& Hour < cal.get(Calendar.HOUR_OF_DAY)) {
				dialogMsg = getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else if (mYear == cal.get(Calendar.YEAR)
					&& mMonth == cal.get(Calendar.MONTH)
					&& mDay == cal.get(Calendar.DATE)
					&& Hour == cal.get(Calendar.HOUR_OF_DAY)
					&& Minute < cal.get(Calendar.MINUTE)) {
				dialogMsg = getString(R.string.wrong_date_time);
				showDialog(Constant.EMPTY_FIELD_DIALOG);
			} else {
				//				String sender_name=getName(number.getText().toString());
//				Log.e("",""+getName(number.getText().toString()));
				database.open();
				ContentValues intialvalues = new ContentValues();
				intialvalues.put(Constant.PHONENO_FORMESSAGE, number.getText()
						.toString().trim());

				intialvalues.put(Constant.MESSAGE_TO_SEND, message.getText()
						.toString().trim());
				Log.e("@@@@@@@@", "" + mYear + "-" + pad(mMonth + 1) + "-"
						+ pad(mDay) + " " + pad(Hour) + ":" + pad(Minute)
						+ ":00");
				intialvalues.put(Constant.MESSAGE_SENDING_DATE, mYear + "-"
						+ pad(mMonth + 1) + "-" + pad(mDay) + " " + pad(Hour)
						+ ":" + pad(Minute) + ":00");
				intialvalues.put(Constant.MobileUser, getName(number.getText().toString()));
				intialvalues.put(Constant.MESSAGE_STATUS,
						Constant.MESSAGE_STAUS_TO_be_SEND);

				database.inserteventvalue(
						Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE,
						intialvalues);
				Toast.makeText(Sendmessage.this, R.string.successfully_saved,
						Toast.LENGTH_SHORT).show();
				Cursor crsr = database.sortDateMessage();
				startManagingCursor(crsr);
				database.close();

				if (crsr.getCount() > 0) {
					for (int i = 0; i < crsr.getCount(); i++) {
						crsr.moveToNext();
					}
					crsr.moveToFirst();
					activateProfile(Integer.parseInt(crsr.getString(crsr
							.getColumnIndexOrThrow(Constant.ROWID))));
				}
			}
			break;
		case R.id.btnviewlist:
			Intent i = new Intent(getBaseContext(), ScheduleMessageHistory.class);
			startActivity(i);
			break;
		case R.id.Contacts:
			list.clear();
			for (int j = 0; j < contact.size(); j++) {
				list.add(contact.get(j).get(NAME));
			}

			final Dialog dialog=new Dialog(Sendmessage.this);
			dialog.setTitle(getString(R.string.choose_course));
			dialog.setContentView(R.layout.choose_toblock);
			ListView listview=(ListView) dialog.findViewById(R.id.listView1);
			ArrayAdapter<String> adapter= new ArrayAdapter<String>(Sendmessage.this,R.layout.listitem_tochoose, list);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int postn, long id) {
					String Text=number.getText().toString();
					Boolean found=false;
					if(number.getText().toString().equalsIgnoreCase("")){
						number.setText(contact.get(postn).get(NUMBER));
					}else{
						String num=contact.get(postn).get(NUMBER);
						String numberlist[]=Text.split(",");
						for (int j = 0; j < numberlist.length; j++) {
							if(num.equalsIgnoreCase(numberlist[j])){
								Toast.makeText(Sendmessage.this, "All ready Added", Toast.LENGTH_SHORT).show();
								found=true;
							}
						}
						if(!found){
							number.setText(Text+","+contact.get(postn).get(NUMBER));
						}
					}
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
	
		}
	}

	private String getName(String sendMobileNumber) {
		for (int i = 0; i < contact.size(); i++) {
			if(contact.get(i).get(NUMBER).toString().equalsIgnoreCase(sendMobileNumber)){
				return contact.get(i).get(NAME);
			}
		}
		return UNKNOWN;
	}

	// set alarm to open the activity
	public void activateProfile(int id) {
		Log.e("", "Profile activate");
		database.open();
		Cursor csr = database.fetch_DataMessage2(id);
		startManagingCursor(csr);
		database.close();
		AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent2 = new Intent(Sendmessage.this, TosendMessage.class);
		intent2.putExtra("id",
				csr.getString(csr.getColumnIndexOrThrow(Constant.ROWID)));
		PendingIntent pI1 = PendingIntent.getBroadcast(Sendmessage.this,
				12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String datefull = csr.getString(csr
				.getColumnIndexOrThrow(Constant.MESSAGE_SENDING_DATE));

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

		alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pI1);
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.EMPTY_FIELD_DIALOG:
			return new AlertDialog.Builder(Sendmessage.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(getString(R.string.attention))
			.setMessage(dialogMsg)
			.setCancelable(false)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					dialog.dismiss();
					removeDialog(Constant.EMPTY_FIELD_DIALOG);
				}
			}).create();
		}
		return null;
	}

	protected void display() {
		day_txv.setText(String.valueOf(mDay));
		month_txv.setText(updateDate(mMonth));
		year_txv.setText(String.valueOf(mYear));
		if (Hour > 12) {
			Hour = Hour - 12;
			Am_Pm = false;
			image.setBackgroundResource(R.drawable.pm_btn);
		}
		else if(Hour==12){
			Am_Pm=false;
			image.setBackgroundResource(R.drawable.pm_btn);
		}
		else if (Hour == 0) {
			Hour = 12;
			image.setBackgroundResource(R.drawable.am_btn);
		}
		
		mHour_txv.setText(pad(Hour));
		mMinute_txv.setText(pad(Minute));
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private String updateDate(int month) {
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

	private int MonthDates(int month) {
		int days = 0;

		if (month == 0) {
			days = 31;
		} else if (month == 1) {
			if (isleapyear(mYear))
				days = 29;
			else
				days = 28;
		} else if (month == 2) {
			days = 31;
		} else if (month == 3) {
			days = 30;
		} else if (month == 4) {
			days = 31;
		} else if (month == 5) {
			days = 30;
		} else if (month == 6) {
			days = 31;
		} else if (month == 7) {
			days = 31;
		} else if (month == 8) {
			days = 30;
		} else if (month == 9) {
			days = 31;
		} else if (month == 10) {
			days = 30;
		} else if (month == 11) {
			days = 31;
		}
		return days;
	}

	private boolean isleapyear(int year) {
		if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
			return true;
		}
		return false;

	}
	public void contactList(){

		new Thread(){
			@Override
			public void run(){
				String hasPhone;
				Cursor cursor = getContacts();
				startManagingCursor(cursor);
				cursor.moveToFirst();
				Map<String,String> map;
				for (int i = 0; i < cursor.getCount(); i++) 
				{
//					Log.w("id, name, has number", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))+", "+cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))+", " +cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
					if ( cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1"))
						hasPhone = "true";
					else
						hasPhone = "false" ;

					if (Boolean.parseBoolean(hasPhone)) 
					{
						Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(  ContactsContract.Contacts._ID)) , null, null);
						startManagingCursor(phones);


						if(!(phones==null)&&phones.getCount()>=0){
							phones.moveToFirst();
							for (int j = 0; j < phones.getCount(); j++) {
								map= new HashMap<String, String>();
								String name=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
								String number=phones.getString(phones.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.Phone.NUMBER));
								map.put(NAME,name);
								map.put(NUMBER,number.replace("-", ""));
//								Log.e("",""+name+" "+number);
								contact.add(map);
								phones.moveToNext();
							}
						}

					}

					cursor.moveToNext();
				}
				Message msg= new Message();
				msg.obj="success";
				handler.sendMessage(msg);
			}
		}.start();
}
	Handler handler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			dialog.cancel();
			btn_contact.setEnabled(true);
		}
	};
	private void Disable_user_Intration(){
		dialog = new ProgressDialog(Sendmessage.this);
		dialog.setCancelable(false);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.show();
	}

	private Cursor getContacts()
	{ Boolean mShowInvisible=false;
	Uri uri = ContactsContract.Contacts.CONTENT_URI;
	String[] projection = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.HAS_PHONE_NUMBER,
	};
	String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (mShowInvisible ? "0" : "1") + "'";
	String[] selectionArgs = null;
	String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

	return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}
}
