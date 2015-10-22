package com.donotdisturb;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.Model;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.constant.Constant;
import com.database.DataBase;

public class AddBlockList extends ListActivity {
	private DataBase database;
	private String Number = "";
	private String hasPhone = "";
	private ProgressDialog dialog;
	private ListView listview;
	private List<Object> contactList = new ArrayList<Object>();
	private SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	private List<Map<String, Object>> blocknolist;
	private  List<Object> phoneContact=new ArrayList<Object>();
	private  List<Object> message=new ArrayList<Object>();
	private  List<Object> calllog=new ArrayList<Object>();
	private List<Boolean> contactList_alter = new ArrayList<Boolean>();
	private List<Boolean> messageList_alter = new ArrayList<Boolean>();
	private List<Boolean> calllogList_alter = new ArrayList<Boolean>();
	//	private int counter = 0;
	private Boolean flag = false,iteration_first=true;
	private ImageView imageEmpty;
	private Button option;
	private Context mContext;
	private NumtoblockAdapter mylistAdapter ;
	private String BODY="body",READ="read",ADDRESS="address",DATE="date",UNKNOWN="Unknown";
	private String KEY_ROWID="rowID",KEY_BLOCK_NO="blockno",STATUS="status",NAME="name";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_block_list);
		imageEmpty=(ImageView)findViewById(R.id.empty);
		option=(Button)findViewById(R.id.btn_option_view);
		mContext=AddBlockList.this;
		listview = getListView();
		database = new DataBase(AddBlockList.this);
		blocknolist = TofetchAllBlockList();
		Disable_user_Intration(getResources().getString(R.string.contacts_loading));
		contactList();
	}
	//=====================================CLICK EVENT ON BUTTONS====================
	public void click(View view) {
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.keypad);
		relative.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
		EditText editNumber = (EditText) findViewById(R.id.edittext);
		editNumber.setEnabled(false);
		switch (view.getId())
		{
		case R.id.contactList:

			Disable_user_Intration(getResources().getString(R.string.contacts_loading));
			contactList();
			break;
		case R.id.messageLog:
			Disable_user_Intration(getResources().getString(R.string.message_loading));
			getMessageDetail();
			break;
		case R.id.callhistory:
			Disable_user_Intration(getResources().getString(R.string.calllog_loading));
			getCallLog("ALL");
			break;
		case R.id.btn_option_view:
			final RadioGroup radiogroup = new RadioGroup(getApplicationContext());
			final RadioButton dialedCall = new RadioButton(getApplicationContext());
			dialedCall.setText(getResources().getString(R.string.dial_call));
			dialedCall.setButtonDrawable(R.drawable.radio_button);
			final RadioButton recieveCall = new RadioButton(getApplicationContext());
			recieveCall.setText(getResources().getString(R.string.recieve_call));
			recieveCall.setButtonDrawable(R.drawable.radio_button);
			final RadioButton missedCall = new RadioButton(getApplicationContext());
			missedCall.setText(getResources().getString(R.string.missed_call));
			missedCall.setButtonDrawable(R.drawable.radio_button);
			radiogroup.addView(dialedCall);
			radiogroup.addView(recieveCall);
			radiogroup.addView(missedCall);
			dialedCall.setChecked(true);

			new AlertDialog.Builder(AddBlockList.this)
			.setTitle(getResources().getString(R.string.view_list))
			.setCancelable(false)
			.setView(radiogroup)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					String callType = "";

					if (dialedCall.isChecked() == true) {
						callType = "2";
					} else if (recieveCall.isChecked() == true) {
						callType = "1";

					} else if (missedCall.isChecked() == true) {
						callType = "3";
					}
					Disable_user_Intration(getResources().getString(R.string.calllog_loading));
					getCallLog(callType);
				}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					dialog.dismiss();
				}
			}).show();
			break;
		case R.id.enter_number:
			Number = "";
			editNumber.setText("");
			listview.setVisibility(View.GONE);
			relative.setVisibility(View.VISIBLE);
			option.setVisibility(View.GONE);
			break;
		case R.id.button_clear:
			listview.setVisibility(View.GONE);
			relative.setVisibility(View.VISIBLE);
			option.setVisibility(View.GONE);
			Number = "";
			editNumber.setText("");
			break;
		case R.id.saveNumber:
			if (Number.toString().equalsIgnoreCase("")) {
				Toast.makeText(AddBlockList.this, R.string.alert_no_blank,
						Toast.LENGTH_SHORT).show();
				break;
			} else {
				database.open();
				ContentValues intialvalues = new ContentValues();
				intialvalues.put(Constant.BLOCK_NO, editNumber.getText().toString());
				intialvalues.put(Constant.BLOCKNO_NAME, UNKNOWN);
				intialvalues.put(Constant.PHONENUMBER_STATUS,
						Constant.STATUS_BOTH_BLOCK);
				intialvalues.put(Constant.ContactId, "0-0");
				SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
				intialvalues.put(Constant.BLOCK_SINCE, sdf.format(new Date()));
				database.inserteventvalue(Constant.DATABASE_BLOCKLIST_TABLE,
						intialvalues);
				database.close();
			}
			break;
		
		default:
			option.setVisibility(View.GONE);
			listview.setVisibility(View.GONE);
			relative.setVisibility(View.VISIBLE);
			Number = Number + view.getTag().toString();
			editNumber.setText(Number);
			break;
		}
	}

	//========================================GATHERING CONTACT DETAILS FROM PHONE CONTACTS====================
	public void contactList() {
		option.setVisibility(View.GONE);

		new Thread() {
			public void run() {

				if(iteration_first){
					//					Log.e("","first run");
					phoneContact=Phone_Contact_list();
					iteration_first=false;
					for (int i = 0; i < phoneContact.size(); i++) {
						contactList.add(phoneContact.get(i));
					}
				}
				contactList.clear();
				for (int i = 0; i < phoneContact.size(); i++) {
					contactList.add(phoneContact.get(i));
					contactList_alter.add(false);
				}
//				}

				Message myMessage = new Message();
				myMessage.obj = getResources().getString(R.string.contact_success);
//				Log.e("","sending message");
				contactHandler.sendMessage(myMessage);
			}
		}.start();
	}
	//===============================SETTING ADOPTERS===================================
	private Handler contactHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//			Log.e("","in handler");
			if(msg.obj.toString().equalsIgnoreCase(getResources().getString(R.string.message_success))){
				if(message.size()==0){
					imageEmpty.setBackgroundResource(R.drawable.empty_mesglog_msg);
				}
				mylistAdapter = new NumtoblockAdapter(
						AddBlockList.this, message, 2);
				listview.setAdapter(mylistAdapter);
				dialog.dismiss();
			}
			else if(msg.obj.toString().equalsIgnoreCase(getResources().getString(R.string.contact_success))) {
				//				Log.e("","from set to zero");
				
				mylistAdapter = new NumtoblockAdapter(
						AddBlockList.this, contactList, 0);
				listview.setAdapter(mylistAdapter);
				dialog.dismiss();
			}
			else if(msg.obj.toString().equalsIgnoreCase(getResources().getString(R.string.calllog_success))){
				if(calllog.size()==0){
//					Log.e("Call log list empty","");
					imageEmpty.setBackgroundResource(R.drawable.empty_callhistory_msg);
				}
				mylistAdapter = new NumtoblockAdapter(
						AddBlockList.this, calllog, 1);
				listview.setAdapter(mylistAdapter);
				dialog.dismiss();	
			} else if (msg.obj.toString().equalsIgnoreCase(getResources().getString(R.string.saving_succes))){
				dialog.dismiss();
				finish();
				//				Toast.makeText(mContext, "SAVED", Toast.LENGTH_SHORT).show();
			}
		}
	};

	//=================================CURSOR FOR PHONE_CONTACTS==================
	private Cursor getContacts() {
		Boolean mShowInvisible=false;
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID, 
				ContactsContract.Contacts.PHOTO_ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER, };
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
		+ (mShowInvisible ? "0" : "1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
		+ " COLLATE LOCALIZED ASC";
		Cursor cr= managedQuery(uri, projection, selection, selectionArgs, sortOrder);
		if(cr.getCount()>0){
			startManagingCursor(cr);
		}
		return cr;
	}

	//==============================GATHERING CALL DETAILS==========================
	private void getCallLog(String type1) {
		option.setVisibility(View.VISIBLE);
		final String type=type1;
		new Thread(){
			@Override
			public void run(){
				List<Object> _list = new ArrayList<Object>();
				//				contactList.clear();
				String name = "";
				String id="0";
				String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
				Cursor mCallCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
						null, null, strOrder);
				if (!(mCallCursor == null) && (mCallCursor.getCount() >= 0)) {
					startManagingCursor(mCallCursor);
					mCallCursor.moveToFirst();
					for (int i = 0; i < mCallCursor.getCount(); i++) {
						Model callhistorybean = new Model();
						if(!type.equalsIgnoreCase("ALL")){
							if (mCallCursor
									.getString(
											mCallCursor.getColumnIndex(CallLog.Calls.TYPE))
											.toString().equalsIgnoreCase(type)) {
								callhistorybean.setPhoneno(mCallCursor
										.getString(mCallCursor
												.getColumnIndex(CallLog.Calls.NUMBER)));
								callhistorybean.setDate(mCallCursor.getString(mCallCursor
										.getColumnIndex(CallLog.Calls.DATE)));
								callhistorybean.setType(mCallCursor.getString(mCallCursor
										.getColumnIndex(CallLog.Calls.TYPE)));
								if (!(mCallCursor.getString(mCallCursor
										.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null)) {
									name = mCallCursor.getString(mCallCursor
											.getColumnIndex(CallLog.Calls.CACHED_NAME));
								} else {
									name = UNKNOWN;
									id="0";
								}
								callhistorybean.setName(name);
								if(!callhistorybean.getName().equalsIgnoreCase(UNKNOWN)){
									for (int j = 0; j < phoneContact.size(); j++) {
										if(callhistorybean.getName().equalsIgnoreCase(((Model)phoneContact.get(j)).getName())){
											callhistorybean.setId(((Model)phoneContact.get(j)).getId());
											callhistorybean.setPhotoId(((Model)phoneContact.get(j)).getPhotoId());
										}
									}
								}
								_list.add(callhistorybean);
							}
						}
						else {
							callhistorybean.setPhoneno(mCallCursor
									.getString(mCallCursor
											.getColumnIndex(CallLog.Calls.NUMBER)));
							callhistorybean.setDate(mCallCursor.getString(mCallCursor
									.getColumnIndex(CallLog.Calls.DATE)));
							callhistorybean.setType(mCallCursor.getString(mCallCursor
									.getColumnIndex(CallLog.Calls.TYPE)));
							if (!(mCallCursor.getString(mCallCursor
									.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null)) {
								name = mCallCursor.getString(mCallCursor
										.getColumnIndex(CallLog.Calls.CACHED_NAME));
							} else {
								name = UNKNOWN;
								id="0";
							}
							callhistorybean.setName(name);
							for (int j = 0; j < phoneContact.size(); j++) {
								if(callhistorybean.getPhoneno().equalsIgnoreCase(((Model)phoneContact.get(j)).getPhoneno())){
									name=((Model)phoneContact.get(j)).getName();
									id=((Model)phoneContact.get(j)).getId();
									callhistorybean.setPhotoId(((Model)phoneContact.get(j)).getPhotoId());
								}
							}
							callhistorybean.setName(name);
							callhistorybean.setId(id);
							//							}
							_list.add(callhistorybean);
						}
						mCallCursor.moveToNext();
					}
					calllog.clear();
					for (int i = 0; i < _list.size(); i++) {
						calllog.add(_list.get(i));
						calllogList_alter.add(false);
					}
						
				}
				Message myMessage = new Message();
				myMessage.obj = getResources().getString(R.string.calllog_success);
				contactHandler.sendMessage(myMessage);
			}
		}.start();
	}

	//===================================================GATHERING MESSAGE DETAILS==========================
	private void getMessageDetail() {
		option.setVisibility(View.GONE);
		new Thread(){
			@Override
			public void run(){
				List<Object> _list= new ArrayList<Object>();
				String strUri =Constant.MESSAGE_URI;
				Uri urisms = Uri.parse(strUri);
				Cursor c = mContext.getContentResolver().query(urisms, null, null, null,
						null);
				startManagingCursor(c);
				c.moveToFirst();
				if (c != null && c.getCount() >= 0) {
					for (int i = 0; i < c.getCount(); i++) {
						String personName = "";
						String id="0";
						String photoid="null";
						Model messagedetail = new Model();
						String Phone_no=c.getString(c.getColumnIndexOrThrow(ADDRESS));
						messagedetail.setPhoneno(Phone_no);
						messagedetail.setMessageBody(c.getString(c
								.getColumnIndexOrThrow(BODY)));
						messagedetail.setReadStatus(c.getString(c
								.getColumnIndexOrThrow(READ)));
						messagedetail.setDate(c.getString(c
								.getColumnIndexOrThrow(DATE)));
						Boolean getName=false;

						//						personName = c.getString(c.getColumnIndexOrThrow("person"));
						for (int j = 0; j < phoneContact.size(); j++) {
							if (((Model)phoneContact.get(j)).getPhoneno().equalsIgnoreCase(Phone_no)) {
//								Log.e("message person name--->",((Model) phoneContact.get(j)).getName());
								personName = ((Model) phoneContact.get(j)).getName();
								id=((Model)phoneContact.get(j)).getId();
								photoid=((Model)phoneContact.get(j)).getPhotoId();
								getName=true;
							}
						}
						if(!getName){
							personName = UNKNOWN;
						}
//						Log.e("",""+personName+"=="+Phone_no+"==="+id);
						messagedetail.setName(personName);
						messagedetail.setId(id);
						messagedetail.setPhotoId(photoid);
						_list.add(messagedetail);
						c.moveToNext();
					}
				}
				message.clear();
				for (int i = 0; i < _list.size(); i++) {
					message.add(_list.get(i));
					messageList_alter.add(false);
				}
				
				Message myMessage = new Message();
				myMessage.obj = getResources().getString(R.string.message_success);
				contactHandler.sendMessage(myMessage);
			}
		}.start();
	}

	//========================================GATHERING ALL BLOCKED PHONE NUMBERS=================
	public List<Map<String, Object>> TofetchAllBlockList() {
		List<Map<String, Object>> phonelist = new LinkedList<Map<String, Object>>();
		phonelist.clear();
		database.open();
		Cursor cursor = database.fetchAllBlockList();
		database.close();
		if (cursor != null) {
			
			for (int i = 0; i < cursor.getCount(); i++) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(KEY_ROWID, cursor.getString(cursor
						.getColumnIndexOrThrow(Constant.ROWID)));
				item.put(KEY_BLOCK_NO, cursor.getString(cursor
						.getColumnIndexOrThrow(Constant.BLOCK_NO)));
				item.put(STATUS, cursor.getString(cursor
						.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
				item.put(NAME, cursor.getString(cursor
						.getColumnIndexOrThrow(Constant.BLOCKNO_NAME)));
				phonelist.add(item);
				cursor.moveToNext();
			}

		} else {
			phonelist = null;
		}
		return phonelist;
	}

	//====================================PHONE LIST===========================
	public List<Object> Phone_Contact_list(){
		final List<Object> phoneList = new ArrayList<Object>();
		Cursor cursor = getContacts();
		startManagingCursor(cursor);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
		}

		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor
					.getString(
							cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
							.equalsIgnoreCase("1"))
				hasPhone = "true";
			else
				hasPhone = "false";

			if (Boolean.parseBoolean(hasPhone)) {
				Cursor phones = getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ " = "
						+ cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts._ID)),
								null, null);

				if (!(phones == null) && phones.getCount() >= 0) {
					startManagingCursor(phones);
					phones.moveToFirst();
					for (int j = 0; j < phones.getCount(); j++) {
						Model modclass = new Model();
						modclass.setName(cursor.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
						String Phoneno=phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Phoneno=Phoneno.replace("-", "");
						modclass.setPhoneno(Phoneno);
						modclass.setId(cursor.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
						modclass.setPhotoId(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID)));
						phoneList.add(modclass);
						phones.moveToNext();
					}
				}
			}
			cursor.moveToNext();
		}
		return phoneList;
	}


	//=========================================ADOPTER========================================
	public class NumtoblockAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<Object> Friendslist;
		List<Map<String, Object>> calllogList;
		private int isfrom = 0;
		//		private int count = 0;
		private Context mContext;
		//		private int THUMBNAIL_SIZE=64;

		public NumtoblockAdapter(Context context, List<Object> results,
				int fromWhere) {
			Friendslist = results;
			mContext = context;
			mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			isfrom = fromWhere;
			if(Friendslist.size()==0){
				imageEmpty.setVisibility(View.VISIBLE);
			}
			else{
				imageEmpty.setVisibility(View.GONE);
			}
		}

		public int getCount() {
			if (isfrom == 0) {
				return Friendslist.size();
			} else if (isfrom == 1) {
				return Friendslist.size();
			} else {
				return Friendslist.size();
			}
		}

		public Object getItem(int position) {
			if (isfrom == 0) {
				return Friendslist.get(position);
			} else if (isfrom == 1) {
				return Friendslist.get(position);
			} else {
				return Friendslist.get(position);
			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view=convertView;
			ViewHolder holder;
			//============= O MEANS DETAILS FROM PHONE CONTACTS========================================
			if (isfrom == 0) {
//				if(view==null){}
				view = mInflater.inflate(R.layout.inflatefriens_row, null);
				holder = new ViewHolder();
				holder.contactname = (TextView) view
				.findViewById(R.id.ContactNametextView1);
				holder.phoneno = (TextView) view
				.findViewById(R.id.PhoneNotextView1);
				holder.cheackmarkCheckBox = (CheckBox) view
				.findViewById(R.id.selectfriendcheckBox1);
				holder.contactImage = (ImageView) view
				.findViewById(R.id.contactImage);

				holder.contactname.setText(((Model) Friendslist.get(position))
						.getName());
				holder.phoneno.setText(((Model) Friendslist.get(position))
						.getPhoneno());
				if (((Model) Friendslist.get(position)).getPhotoId()!= null) {
					holder.contactImage
					.setImageBitmap(loadContactPhoto(
							Integer.parseInt(((Model) Friendslist.get(position)).getId()),
							Integer.parseInt(((Model) Friendslist.get(position)).getId())));
				}
				else {
					holder.contactImage.setBackgroundResource(R.drawable.defaultimage)	;		
				}

				holder.cheackmarkCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(
							CompoundButton buttonView, boolean isChecked) {
						((Model) Friendslist.get(position))
						.setSelected(isChecked);
						//						HasChanged=true;
						contactList_alter.set(position, isChecked);
					}
				});
				holder.cheackmarkCheckBox.setChecked(((Model) Friendslist
						.get(position)).isSelected());
				if (position % 2 == 0) {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg1));
				} else {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg2));
				}
			} 
			//============================DETAILS FROM CALL LOG=====================================			
			else if (isfrom == 1) {
				//				LayoutInflater inflater = LayoutInflater.from(mContext);
//				if(view==null){}
				view = mInflater.inflate(R.layout.inflate_callhistory, null);
				TextView calllog_noTV = (TextView) view
				.findViewById(R.id.textView2);
				TextView calllog_nameTV = (TextView) view
				.findViewById(R.id.textView1);
				ImageView calllog_typeTV = (ImageView) view
				.findViewById(R.id.imageView2);
				ImageView contactImage=(ImageView)view.findViewById(R.id.imageContact);
				CheckBox calllogCheckBox = (CheckBox) view
				.findViewById(R.id.checkBox1);
				if (((Model) Friendslist.get(position)).getPhotoId()!= null) {
					//					Log.e("","in");
					contactImage
					.setImageBitmap(loadContactPhoto(
							Integer.parseInt(((Model) Friendslist.get(position)).getId()),
							Integer.parseInt(((Model) Friendslist.get(position)).getId())));
				}
				else {
					contactImage.setBackgroundResource(R.drawable.defaultimage)	;			}
				calllog_noTV.setText(((Model) Friendslist.get(position))
						.getPhoneno().toString());
				calllog_nameTV.setText(((Model) Friendslist.get(position))
						.getName().toString());
				String callType = ((Model) Friendslist.get(position)).getType()
				.toString();
				int dircode = Integer.parseInt(callType);
				switch (dircode) {
				case CallLog.Calls.OUTGOING_TYPE:
					calllog_typeTV.setBackgroundResource(R.drawable.dialledcalls_icon);
					break;

				case CallLog.Calls.INCOMING_TYPE:
					calllog_typeTV.setBackgroundResource(R.drawable.recievedcalls_icon);
					break;

				case CallLog.Calls.MISSED_TYPE:
					calllog_typeTV.setBackgroundResource(R.drawable.missedcalls_icon);
					break;
				}
				calllogCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(
							CompoundButton buttonView, boolean isChecked) {
						((Model) Friendslist.get(position))
						.setSelected(isChecked);
						calllogList_alter.set(position, isChecked);
					}
				});
				calllogCheckBox.setChecked(((Model) Friendslist.get(position))
						.isSelected());
				if (position % 2 == 0) {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg1));
				} else {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg2));
				}
			}
			//================================DETAILS FROM MESSAGE==============================			
			else if (isfrom == 2) {
				//				LayoutInflater inflater = LayoutInflater.from(mContext);
//				if(view==null){}
				view = mInflater.inflate(R.layout.inflate_message_row, null);
				
				ImageView image= (ImageView)view.findViewById(R.id.contact_image);
				if (((Model) Friendslist.get(position)).getPhotoId()!= null) {
					image
					.setImageBitmap(loadContactPhoto(
							Integer.parseInt(((Model) Friendslist.get(position)).getId()),
							Integer.parseInt(((Model) Friendslist.get(position)).getId())));
				}
				else {
					image.setBackgroundResource(R.drawable.defaultimage)	;			}

				TextView messaeg_nameTV = (TextView) view
				.findViewById(R.id.messagetextView1);
				TextView messageBodyTV = (TextView) view
				.findViewById(R.id.messagetextView2);
				TextView messageDateTV = (TextView) view
				.findViewById(R.id.textView3);
				CheckBox messageCheckBox = (CheckBox) view
				.findViewById(R.id.msgcheckBox1);

				messaeg_nameTV.setText(((Model) Friendslist.get(position))
						.getName().toString());
				//				Log.e("","Now in message");
				Date date = new Date(Long.valueOf(((Model) Friendslist
						.get(position)).getDate().toString()));
				String _date= date.getDay()+"-"+getMonth(date.getMonth())+"-"+(date.getYear()+1900);
				messageDateTV.setText(_date);
				messageBodyTV.setText(((Model) Friendslist.get(position))
						.getMessageBody().toString());
				messageCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(
							CompoundButton buttonView, boolean isChecked) {
						((Model) Friendslist.get(position))
						.setSelected(isChecked);
						//						HasChanged=true;
						messageList_alter.set(position, isChecked);
					}
				});
				messageCheckBox.setChecked(((Model) Friendslist.get(position))
						.isSelected());
				if (position % 2 == 0) {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg1));
				} else {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.contacthint_list_bg2));
				}
			}
			System.gc();
			return view;
		}
		//============================GETTING CONTACT IMAGE====================
		private Bitmap loadContactPhoto(long id, long photoId) {
			ContentResolver cr = mContext.getContentResolver();
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, id);
			InputStream input = ContactsContract.Contacts
			.openContactPhotoInputStream(cr, uri);
			if (input != null) {
				return BitmapFactory.decodeStream(input);
			} else {
				Log.d("PHOTO", "first try failed to load photo");

			}

			byte[] photoBytes = null;
			Uri photoUri = ContentUris.withAppendedId(
					ContactsContract.Data.CONTENT_URI, photoId);
			Cursor c = cr.query(photoUri,
					new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
					null, null, null);
			try {
				if (c.moveToFirst())
					photoBytes = c.getBlob(0);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				c.close();
			}

			if (photoBytes != null)
				return BitmapFactory.decodeByteArray(photoBytes, 0,
						photoBytes.length);
			else
				Log.d("PHOTO", "second try also failed");
			Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.defaultimage);
			return icon;
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

	class ViewHolder {
		TextView contactname;
		TextView phoneno;
		CheckBox cheackmarkCheckBox;
		ImageView contactImage;

	}

	private void Disable_user_Intration(String Title) {
		dialog = new ProgressDialog(AddBlockList.this);
		dialog.setCancelable(false);
		dialog.setMessage(Title);
		dialog.show();
	}
	@Override
	public void onBackPressed(){
		if(calllogList_alter.contains(true) || messageList_alter.contains(true) || contactList_alter.contains(true)){
			//==========================================SAVING CHANGES =================================
			Disable_user_Intration(getResources().getString(R.string.saving));
			new Thread(){
				public  void run(){
					if(calllogList_alter.contains(true)){
						do{
							int position=calllogList_alter.indexOf(true);
//							Log.e("","call log altered"+ "and has index of "+position);
//							Log.e("",""+((Model)calllog.get(calllogList_alter.indexOf(true))).getName());
							Model item=(Model)calllog.get(position);
							calllogList_alter.set(position, false);
							flag = false;
							if (blocknolist.size() > 0) {
								for (int j = 0; j < blocknolist.size(); j++) {
									if (String.valueOf(
											blocknolist.get(j).get(KEY_BLOCK_NO))
											.equalsIgnoreCase(item.getPhoneno().toString())) {
										database.open();
										database.updateBlockNodata(Integer.parseInt(blocknolist.get(j)
												.get(KEY_ROWID).toString()),item.getPhoneno(), Constant.STATUS_BOTH_BLOCK,
												blocknolist.get(j).get(NAME).toString());
										database.close();
										flag = true;
										break;
									}
								}}
							if (flag == false || blocknolist.size() == 0) {
								ContentValues intialvalues = new ContentValues();
								intialvalues.put(Constant.BLOCK_NO,
										item.getPhoneno());
								intialvalues.put(Constant.BLOCKNO_NAME,
										item.getName());
								String contactid = "";
								if (item.getId() == null) {
									contactid = "0-0";
								} else {
									contactid = item.getId()+"-"+item.getPhotoId();
								}
								intialvalues.put(Constant.ContactId, contactid);
								intialvalues.put(Constant.PHONENUMBER_STATUS, Constant.STATUS_BOTH_BLOCK);
								intialvalues.put(Constant.BLOCK_SINCE, sdf.format(new Date()));
								database.open();
								database.inserteventvalue(
										Constant.DATABASE_BLOCKLIST_TABLE, intialvalues);
								database.close();
							}
							blocknolist = TofetchAllBlockList();
						}while(calllogList_alter.contains(true));
					}
					if(messageList_alter.contains(true)){
						do{
							int position=messageList_alter.indexOf(true);
//							Log.e("","call log altered"+ "and has index of "+position);
//							Log.e("",""+((Model)message.get(messageList_alter.indexOf(true))).getName());
							Model item=(Model)message.get(position);
							messageList_alter.set(position, false);
							flag = false;
							if (blocknolist.size() > 0) {
								for (int j = 0; j < blocknolist.size(); j++) {
									if (String.valueOf(
											blocknolist.get(j).get("blockno"))
											.equalsIgnoreCase(item.getPhoneno().toString())) {
										database.open();
										database.updateBlockNodata(Integer.parseInt(blocknolist.get(j)
												.get("rowID").toString()),item.getPhoneno(), Constant.STATUS_BOTH_BLOCK,
												blocknolist.get(j).get("name").toString());
										database.close();
										flag = true;
										break;
									}
								}}
							if (flag == false || blocknolist.size() == 0) {
								ContentValues intialvalues = new ContentValues();
								intialvalues.put(Constant.BLOCK_NO,
										item.getPhoneno());
								intialvalues.put(Constant.BLOCKNO_NAME,
										item.getName());
								String contactid = "";
								if (item.getId() == null) {
									contactid = "0-0";
								} else {
									contactid = item.getId()+"-"+item.getPhotoId();
								}
								intialvalues.put(Constant.ContactId, contactid);
								intialvalues.put(Constant.BLOCK_SINCE, sdf.format(new Date()));
								intialvalues.put(Constant.PHONENUMBER_STATUS, Constant.STATUS_BOTH_BLOCK);
								database.open();
								database.inserteventvalue(
										Constant.DATABASE_BLOCKLIST_TABLE, intialvalues);
								database.close();
							}
							blocknolist = TofetchAllBlockList();

						}while(messageList_alter.contains(true));
					}
					if(contactList_alter.contains(true)){
						do{
							int position=contactList_alter.indexOf(true);
							Model item=(Model)contactList.get(position);
							contactList_alter.set(position, false);
							flag = false;
							if (blocknolist.size() > 0) {
								for (int j = 0; j < blocknolist.size(); j++) {
									if (String.valueOf(
											blocknolist.get(j).get(KEY_BLOCK_NO))
											.equalsIgnoreCase(item.getPhoneno().toString())) {
										database.open();
										database.updateBlockNodata(Integer.parseInt(blocknolist.get(j)
												.get(KEY_ROWID).toString()),item.getPhoneno(), Constant.STATUS_BOTH_BLOCK,
												blocknolist.get(j).get(NAME).toString());
										database.close();
										flag = true;
										break;
									}
								}}
							if (flag == false || blocknolist.size() == 0) {
								ContentValues intialvalues = new ContentValues();
								intialvalues.put(Constant.BLOCK_NO,
										item.getPhoneno());
								intialvalues.put(Constant.BLOCKNO_NAME,
										item.getName());
								String contactid = "";
								if (item.getId() == null) {
									contactid = "0-0";
								} else {
									contactid = item.getId()+"-"+item.getPhotoId();
								}
								intialvalues.put(Constant.ContactId, contactid);
								intialvalues.put(Constant.BLOCK_SINCE, sdf.format(new Date()));
								intialvalues.put(Constant.PHONENUMBER_STATUS, Constant.STATUS_BOTH_BLOCK);
								database.open();
								database.inserteventvalue(
										Constant.DATABASE_BLOCKLIST_TABLE, intialvalues);
								database.close();
							}
							blocknolist = TofetchAllBlockList();

						}while(contactList_alter.contains(true));
					}
					Message msg= new Message();
					msg.obj=getResources().getString(R.string.saving_succes);
					contactHandler.sendMessage(msg);
				}
			}.start();
		}else{
			finish();
		}
	}
}

