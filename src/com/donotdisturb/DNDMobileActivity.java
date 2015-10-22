//package com.donotdisturb;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.LinkedList;
//import java.util.List;
//import model.PhoneContactsBackupBean;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.ContentValues;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.Contacts;
//import android.provider.ContactsContract;
//import android.provider.ContactsContract.CommonDataKinds.Phone;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.Toast;
//import com.constant.Constant;
//
//public class DNDMobileActivity  extends Activity{
//	private Thread contactsThread;
//	private	String hasPhone="";
//	private	boolean mShowInvisible;
//	private String time;
//	private static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH.mm";
//	private	ProgressDialog dialog;
//	private	List<PhoneContactsBackupBean> contactList = new LinkedList<PhoneContactsBackupBean>();
//	private Boolean import_contact=false,export_contact=false;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main2);
//
//		Button add_blacklistButton=(Button) findViewById(R.id.blocklistbutton);
//		Button view_blocklist=(Button) findViewById(R.id.addtoblockbutton);
//		Button setringing_scheduleButton=(Button) findViewById(R.id.manageringerbutton1);
//		Button sendMessageButton=(Button) findViewById(R.id.sendmessagebutton);
//		Button phoneBackupButton=(Button) findViewById(R.id.phonebackupbutton);
//
//		add_blacklistButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent =new Intent(DNDMobileActivity.this,AddBlockList.class);
//				startActivity(intent);
//
//			}
//		});
//		view_blocklist.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent =new Intent(DNDMobileActivity.this,ViewBlockListActivity.class);
//				startActivity(intent);
//
//			}
//		});
//		setringing_scheduleButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO
//				Intent intent =new Intent(DNDMobileActivity.this,Profile.class);
//				startActivity(intent);	
//			}
//		});
//
//		sendMessageButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent =new Intent(DNDMobileActivity.this,Sendmessage.class);
//				startActivity(intent);
//
//			}
//		});
//		phoneBackupButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final Dialog dialog= new Dialog(DNDMobileActivity.this);
//				dialog.setContentView(R.layout.backup);
//				dialog.setTitle("BackUP");
//				dialog.show();
//				final Button import_Button= (Button) dialog.findViewById(R.id.button1);
//				final Button export_Button= (Button) dialog.findViewById(R.id.button2);
//				import_Button.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(DNDMobileActivity.this, "Import contacts", Toast.LENGTH_SHORT).show();
//						Disable_user_Intration();
//						contactList();
//						import_contact=true;
//						export_contact=false;
//						dialog.cancel();
//					}
//				});
//				export_Button.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Disable_user_Intration();
//						contactList();
//						import_contact=false;
//						export_contact=true;
//						WriteTOSDcard();
//						showDialog(Constant.DIALOG_EMAIL_ID); 
//						dialog.cancel();
//
//					}
//				});
//			}
//		});
//
//	}
//	public void contactList(){
//
//		contactsThread = new Thread(){
//			public void run(){
//				Message message=new Message();
//				Cursor cursor = getContacts();
//				startManagingCursor(cursor);
//				cursor.moveToFirst();
//				for (int i = 0; i < cursor.getCount(); i++) 
//				{
//					PhoneContactsBackupBean modclass=new PhoneContactsBackupBean();
//					Long contactId=(long) 0.00;
//					if ( cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1"))
//						hasPhone = "true";
//					else
//						hasPhone = "false" ;
//					if (Boolean.parseBoolean(hasPhone)) 
//					{
//						Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(  ContactsContract.Contacts._ID)) , null, null);
//						startManagingCursor(phones);
//						phones.moveToFirst();
//						if(!(phones==null)&&phones.getCount()>=0){
//							for (int j = 0; j < phones.getCount(); j++) {
//								modclass.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
//								modclass.setPhoneNo(phones.getString(phones.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.Phone.NUMBER)));
//								int type=phones.getInt(phones.getColumnIndex(Phone.TYPE));
//								Log.e("contact id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
//								modclass.setType(getContactType(type));
//								contactId=cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//								phones.moveToNext();
//							}
//						}
//					}
//
//					Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//					Log.e("Email count= ",""+emails.getCount());
//					while (emails.moveToNext()) { 
//						String emailAddress = emails.getString( 
//								emails.getColumnIndex(ContactsContract.Data.DATA1));
//						modclass.setEmail(emailAddress); 
//						//						Log.e("",""+emailAddress);//CommonDataKinds.CommonDataColumns.DATA)); 
//					}
//					emails.close(); 
//					Cursor Address = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//					String PostalAddress ="";
//					while (Address.moveToNext()) { 
//						PostalAddress=PostalAddress+ Address.getString( 
//								emails.getColumnIndex(ContactsContract.Data.DATA1));
//						PostalAddress=PostalAddress.replace("\n"," ");
//						PostalAddress=PostalAddress.replace(",","~");
//					}
//
//					modclass.setAddress(PostalAddress);
//					Address.close();
//					contactList.add(modclass);
//					cursor.moveToNext();
//				}
//				message.obj="success";
//				contactHandler.sendMessage(message);
//
//			}
//		};
//		contactsThread.start();
//	}
//	private Handler contactHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			dialog.dismiss();
//			contactsThread.interrupt();
//			if(msg.obj.toString().equalsIgnoreCase("success")){
//				//				dialog.dismiss();
//				if(import_contact){
//					try{
//						InputStream input = new FileInputStream("/sdcard/contact/newcontact.csv");
//						Log.e("","Aveleble bites"+input.available());
//						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//						String line;
//						int count=0;
//						while ((line = reader.readLine()) != null) {
//							Log.e(""+count++,""+line);
//							String[] RowData = line.split(",");
//							System.out.println(RowData[0]+"=========="+RowData[1]+"=========="+RowData[2]);
//							if(count==1)continue;
//							addcontact(RowData[0],RowData[1],TypeId(RowData[2]),RowData[3],RowData[4]);
//						}
//					}catch (IOException ex) {
//						ex.printStackTrace();
//					}
//				}
//				else if(export_contact){
//					WriteTOSDcard();
//				}
//			}else if(msg.obj.toString().equalsIgnoreCase("error exported")) {
//			}
//
//
//		}
//	};
//	private void WriteTOSDcard(){
//		try{
//			// Create file 
//			Calendar cal = Calendar.getInstance();
//			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
//			File root = Environment.getExternalStorageDirectory();
//			if (root.canWrite()){
//				//				Log.e("***********************","@@@@@@@@@@@@@@@@@@@@@@@@2");
//				time=sdf.format(cal.getTime());
//				File pathOfile = new File(root, "MyContactNumbers("+time+").csv");
//				FileWriter fstream = new FileWriter(pathOfile);
//				BufferedWriter out = new BufferedWriter(fstream);
//				out.write(Constant.NAME+","+Constant.NUMBER+","+Constant.TYPE+","+Constant.E_MAIL+","+Constant.POSTAL_ADDRESS);
//
//				for (int i = 0; i < contactList.size(); i++) {
//
//					//					Log.e("Name :"+contactList.get(i).getName().replace(",", "~"),"Number :"+contactList.get(i).getPhoneNo()+"And Type :"+contactList.get(i).getType()+","+contactList.get(i).getEmail()+","+contactList.get(i).getAddress());		
//					//							out.write("\n"+contactList.get(i).getName().replace(",", "~")+","+Integer.parseInt(contactList.get(i).getPhoneNo().replace("-", ""))+","+contactList.get(i).getType());
//					out.write("\n"+contactList.get(i).getName().replace(",", "~")+","+contactList.get(i).getPhoneNo().replace("-", "")+","+contactList.get(i).getType()+","+contactList.get(i).getEmail()+","+contactList.get(i).getAddress());
//				}
//				out.close();
//				fstream.close();
//
//			}	
//
//		} catch (Exception e){//Catch exception if any
//			System.err.println("Error: " + e.getMessage());
//		}
//	}
//	private Cursor getContacts()
//	{
//		Uri uri = ContactsContract.Contacts.CONTENT_URI;
//		String[] projection = new String[] {
//				ContactsContract.Contacts._ID,
//				ContactsContract.Contacts.DISPLAY_NAME,
//				ContactsContract.Contacts.HAS_PHONE_NUMBER,
//		};
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (mShowInvisible ? "0" : "1") + "'";
//		String[] selectionArgs = null;
//		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//		return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
//	}
//
//
//	private void Disable_user_Intration(){
//		dialog = new ProgressDialog(DNDMobileActivity.this);
//		dialog.setCancelable(false);
//		dialog.setMessage("Loading Contacts  to save ...");
//		dialog.show();
//	}
//
//	@Override
//	protected Dialog onCreateDialog(int id){
//		if(id == Constant.DIALOG_EMAIL_ID){
//			return new AlertDialog.Builder(this)
//			.setIcon(android.R.drawable.ic_dialog_alert)
//			.setMessage(android.text.Html.fromHtml("Backup of your Contacts is saved on the SD card in Excel format.<br><br><b>Do you want to email the backup(recommended)?</b>"))
//			.setCancelable(false)
//			.setPositiveButton ("Email", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int id) {
//					sendEmail();
//					dialog.dismiss();
//					removeDialog(Constant.DIALOG_EMAIL_ID);
//				}
//			})
//			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int id) {
//					dialog.dismiss();
//					removeDialog(Constant.DIALOG_EMAIL_ID);
//				}
//
//			}).create();
//		}
//		return null;
//	}
//	public void sendEmail(){
//		Intent i = new Intent(Intent.ACTION_SEND);
//		i.setType("text/html");
//		i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Phone contacts  Backup");
//		i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"/MyContactNumbers("+time+").csv")));
//		try {
//			startActivity(Intent.createChooser(i, "Send mail..."));
//		} catch (android.content.ActivityNotFoundException ex) {
//			Toast.makeText(DNDMobileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//		}
//	}
//	private String getContactType(int number){
//		String type = null;
//		switch (number) {
//		case 1:
//			type="TYPE_HOME";
//			break;
//		case 2:
//			type="TYPE_MOBILE";
//			break;
//		case 3:
//			type="TYPE_WORK";
//			break;
//
//		case 4:
//			type="TYPE_FAX_WORK";
//			break;
//
//		case 5:
//			type="TYPE_FAX_HOME";
//			break;
//
//		case 6:
//			type="TYPE_PAGER";
//			break;
//
//		case 7:
//			type="TYPE_OTHER";
//			break;
//
//		case 8:
//			type="TYPE_CALLBACK";
//			break;
//
//		case 9:
//			type="TYPE_CAR";
//			break;
//
//		case 10:
//			type="TYPE_COMPANY_MAIN";
//			break;
//
//		case 11:
//			type="TYPE_ISDN";
//			break;
//
//		case 12:
//			type="TYPE_MAIN";
//			break;
//
//		case 17:
//			type="TYPE_WORK_MOBILE";
//			break;
//
//		default:
//			break;
//		}
//		return type;
//
//	}
//	public void addcontact(String name,String number,int Type,String email,String address){
//		Boolean math=false;
//
//		for (int i = 0; i < contactList.size(); i++) {
//			if(name.equalsIgnoreCase(contactList.get(i).getName())){
//				if(number.equalsIgnoreCase(contactList.get(i).getPhoneNo())){
//					Log.e("","Name"+name+" and Number"+number+" Matched");
//					math=true;
//					break;
//				}
//			}
//		}
//		if(!math) {
//			ContentValues personValues = new ContentValues();
//			personValues.put(Contacts.People.NAME, name);
//			personValues.put(Contacts.People.STARRED, 1);
//			Uri newPersonUri = getContentResolver()
//			.insert(Contacts.People.CONTENT_URI, personValues);
//
//
//			//=================insert Mobile Number=============================
//			ContentValues mobileValues = new ContentValues();
//			Uri mobileUri = Uri.withAppendedPath(newPersonUri,
//					Contacts.People.Phones.CONTENT_DIRECTORY);
//			mobileValues.put(Contacts.Phones.NUMBER,
//					number);
//			mobileValues.put(Contacts.Phones.TYPE,
//					Type);
//			Uri phoneUpdate = getContentResolver()
//			.insert(mobileUri, mobileValues);
//
//			// ===============add email======================
//			ContentValues emailValues = new ContentValues();
//			Uri emailUri = Uri
//			.withAppendedPath(
//					newPersonUri,
//					Contacts.People.ContactMethods
//					.CONTENT_DIRECTORY);
//			emailValues.put(Contacts.ContactMethods.KIND,
//					Contacts.KIND_EMAIL);
//			emailValues.put(Contacts.ContactMethods.TYPE,
//					Contacts.ContactMethods.TYPE_HOME);
//			emailValues.put(Contacts.ContactMethods.DATA,
//					email);
//			Uri emailUpdate = getContentResolver()
//			.insert(emailUri, emailValues);
//
//			//==================== add address============================
//			ContentValues addressValues = new ContentValues();
//			Uri addressUri = Uri
//			.withAppendedPath(
//					newPersonUri,
//					Contacts.People.ContactMethods
//					.CONTENT_DIRECTORY);
//			addressValues.put(Contacts.ContactMethods.KIND,
//					Contacts.KIND_POSTAL);
//			addressValues.put(Contacts.ContactMethods.TYPE,
//					Contacts.ContactMethods.TYPE_HOME);
//			addressValues.put(Contacts.ContactMethods.DATA,
//					address);
//			Uri addressUpdate = getContentResolver().insert(addressUri,
//					addressValues);
//		}
//	}
//	public int TypeId(String type){
//		if(type.equalsIgnoreCase("TYPE_CUSTOM")){
//			return 0;
//		}
//		else if(type.equalsIgnoreCase("TYPE_FAX_HOME")){
//			return 5;
//		}
//		else if(type.equalsIgnoreCase("TYPE_FAX_WORK")){
//			return 4;
//		}
//		else if(type.equalsIgnoreCase("TYPE_HOME")){
//			return 1;
//		}
//		else if(type.equalsIgnoreCase("TYPE_MOBILE")){
//			return 2;
//		}
//		else if(type.equalsIgnoreCase("TYPE_OTHER")){
//			return 7;
//		}
//		else if(type.equalsIgnoreCase("TYPE_PAGER")){
//			return 6;
//		}
//		else if(type.equalsIgnoreCase("TYPE_WORK")){
//			return 3;
//		}
//		else {
//			return 7;
//		}
//	}
//}
