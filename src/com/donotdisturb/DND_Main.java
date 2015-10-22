package com.donotdisturb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import model.PhoneContactsBackupBean;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.constant.Constant;

public class DND_Main  extends Activity{
	private Thread contactsThread;
	private	String hasPhone="";
	private	boolean mShowInvisible;
	private String time;
	private String DATE_FORMAT_NOW = "dd-MM-yyyy HH.mm";
	private	ProgressDialog dialog;
	private	List<PhoneContactsBackupBean> contactList;
	private boolean import_contact=false,export_contact=false;
	private int REQUEST_CODE=0;
	private int ContactAdded=0;
	private String fileName="MyContactNumbers";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		contactList = new LinkedList<PhoneContactsBackupBean>();
	}
	//=======================================CLICK EVENT ON BUTTONS========
	public void click(View view){
		Intent intent;
		switch (view.getId()) {
		case R.id.add_to_block:
			intent =new Intent(DND_Main.this,AddBlockList.class);
			startActivity(intent);
			break;
		case R.id.send_message:
			intent =new Intent(DND_Main.this,Sendmessage.class);
			startActivity(intent);
			break;
		case R.id.block_list:
			intent =new Intent(DND_Main.this,ViewBlockListActivity.class);
			startActivity(intent);
			break;
		case R.id.backup:
			final Dialog mydialog= new Dialog(DND_Main.this,R.style.PauseDialog);
			mydialog.setContentView(R.layout.backup);
			mydialog.setTitle(getString(R.string.backup));
			mydialog.show();
			final Button import_Button= (Button) mydialog.findViewById(R.id.button1);
			final Button export_Button= (Button) mydialog.findViewById(R.id.button2);
			import_Button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mydialog.cancel();
					Disable_user_Intration();
					contactList();
					import_contact=true;
					export_contact=false;

				}
			});
			export_Button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mydialog.cancel();
					Disable_user_Intration();
					import_contact=false;
					export_contact=true;
					contactList();

				}
			});
			break;
		case R.id.profile:
			intent =new Intent(DND_Main.this,Profile.class);
			startActivity(intent);	
			break;

		}
	}
	//===========================GATHERING CONTACT DETAILS TO SAVE==========================
	public void contactList(){
		contactList.clear();
		contactsThread = new Thread(){
			public void run(){
				Message message=new Message();
				try
				{
					Cursor cursor = getContacts();
					startManagingCursor(cursor);
					cursor.moveToFirst();
					for (int i = 0; i < cursor.getCount(); i++) 
					{
						PhoneContactsBackupBean modclass=new PhoneContactsBackupBean();
						Long contactId=(long) 0.00;
						if ( cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1"))
							hasPhone = "true";
						else
							hasPhone = "false" ;
						if (Boolean.parseBoolean(hasPhone)) 
						{
							Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)) , null, null);
							if(phones.getCount()>0&&phones!=null)
							{
								startManagingCursor(phones);
								phones.moveToFirst();
								if(!(phones==null)&&phones.getCount()>=0){
									for (int j = 0; j < phones.getCount(); j++) {
										modclass.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
										modclass.setPhoneNo(phones.getString(phones.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-", ""));
										int type=phones.getInt(phones.getColumnIndex(Phone.TYPE));
										Log.e("contact id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))+"=" +cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));

										modclass.setType(getContactType(type));
										contactId=cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
										phones.moveToNext();
									}
								}
								phones.close();
							}
						}

											Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
											if(emails.getCount()>0){
												startManagingCursor(emails);
											while (emails.moveToNext()) { 
												String emailAddress = emails.getString( 
														emails.getColumnIndex(ContactsContract.Data.DATA1));
												Log.e("Email",""+emails.getCount());
												if(emailAddress.equalsIgnoreCase("null")){
													emailAddress="";
												}
												modclass.setEmail(emailAddress); 
											}}
											emails.close(); 
											Cursor Address = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
											String PostalAddress ="";
											if(Address.getCount()>0){
											while (Address.moveToNext()) { 
												PostalAddress=PostalAddress+ Address.getString( 
														Address.getColumnIndex(ContactsContract.Data.DATA1));
												PostalAddress=PostalAddress.replace("\n"," ");
												PostalAddress=PostalAddress.replace(",","~");
											}}
						
											modclass.setAddress(PostalAddress);
											Address.close();
						if(modclass.getName()==null){
							cursor.moveToNext();
							continue;
						}
						contactList.add(modclass);
						cursor.moveToNext();
					}
					cursor.close();
				}
				catch(Exception E)
				{

				}
				message.obj="success";
				contactHandler.sendMessage(message);

			}
		};
		contactsThread.start();
	}
	private Handler contactHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			//			contactsThread.interrupt();
			if(msg.obj.toString().equalsIgnoreCase("success")){
				if(import_contact){
					startActivityForResult((new Intent(DND_Main.this,ListFile.class)), REQUEST_CODE);
				}
				else if(export_contact){
					WriteTOSDcard();
				}
			}else if(msg.obj.toString().equalsIgnoreCase("error exported")) {
			}
		}
	};
	//====================================WRITING TO THE SDCARD AT ROOT DIRECTORY======================
	private void WriteTOSDcard(){
		try{
			// Create file 
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()){
				time=sdf.format(cal.getTime());
				File pathOfile = new File(root, fileName+"("+time+").csv");
				//				ProgressBar pgb;
				//				TextView current;
				//				TextView total;
				FileWriter fstream = new FileWriter(pathOfile);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(Constant.NAME+","+Constant.NUMBER+","+Constant.TYPE+","+Constant.E_MAIL+","+Constant.POSTAL_ADDRESS);
				//				final Dialog mydialog= new Dialog(DND_Main.this);
				//				mydialog.setContentView(R.layout.exportcontacts);
				//				mydialog.setTitle("Progress");
				//				pgb=(ProgressBar)mydialog.findViewById(R.id.progressBar1);
				//				current=(TextView)mydialog.findViewById(R.id.txv_current);
				//				total=(TextView)mydialog.findViewById(R.id.txv_Total);
				//				mydialog.show();
				//				pgb.setMax(contactList.size()); 
				//				total.setText("/"+contactList.size());
				Log.e("",""+contactList.size());
				for (int i = 0; i < contactList.size(); i++) {
					String email="";
					if(contactList.get(i).getEmail()!=null)
						email=contactList.get(i).getEmail();
					out.write("\n"+contactList.get(i).getName().replace(",", "~")+","+contactList.get(i).getPhoneNo().replace("-", "")+","+contactList.get(i).getType()+","+email+","+contactList.get(i).getAddress());
//					Log.e("Now contact is ",""+i);
					//					current.setText(String.valueOf(i));
					//					pgb.setProgress(i);
				}
				out.close();
				fstream.close();
				//				mydialog.dismiss();
				showDialog(Constant.DIALOG_EMAIL_ID); 
			}
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}
	private void WriteContactes(String file){
		try{
			InputStream input = new FileInputStream(file);
			Log.e("","Aveleble bytes"+input.available());
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			int count=0;
			ContactAdded=0;
			while ((line = reader.readLine()) != null) {
				count++;
				if(count==1){
					if(!ChekFormate(line)){
						throw new FormateException(getResources().getString(R.string.formate_not_supported));}
					continue;
				}
				//							Log.e(""+count++,""+line);
				String[] RowData = line.split(",");
				//				System.out.println(RowData[0]+"=========="+RowData[1]+"=========="+RowData[2]);
				if(RowData.length==5){
					addcontact(RowData[0],RowData[1],TypeId(RowData[2]),RowData[3],RowData[4]);
				}else if(RowData.length==4)
				{   addcontact(RowData[0], RowData[1], TypeId(RowData[2]), RowData[3], "");
				}else if(RowData.length==3){
					addcontact(RowData[0], RowData[1], TypeId(RowData[2]), "", "");
				}else if(RowData.length==2){
					addcontact(RowData[0], RowData[1],1, "", "");//(1=for "TYPE_HOME")DEFAULT
				}
			}
			Toast.makeText(getApplicationContext(), ContactAdded+" Contacts Added", Toast.LENGTH_SHORT).show();
			//			dialog.dismiss();
		}catch (IOException ex) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.File_not_found), Toast.LENGTH_SHORT).show();
			Log.e("","Ioexception");
			ex.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (FormateException e) {
			Log.e("","Formate exception");
			Toast.makeText(DND_Main.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	private Boolean ChekFormate(String line) {
		String[] rowStrings=line.split(",");
		Log.e(""+rowStrings.length,""+line);
		if(rowStrings.length==5)
			return true;
		else 
			return false;
	}
	class FormateException extends Exception {

		private static final long serialVersionUID = 1L;
		public FormateException(String msg){
			super(msg);
		}
	}
	private Cursor getContacts()
	{
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


	private void Disable_user_Intration(){
		dialog = new ProgressDialog(DND_Main.this);
		dialog.setCancelable(false);
		dialog.setMessage(getResources().getString(R.string.please_wait));
		dialog.show();
	}

	@Override
	protected Dialog onCreateDialog(int id){
		if(id == Constant.DIALOG_EMAIL_ID){
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage(android.text.Html.fromHtml(getResources().getString(R.string.email_message)))
			.setCancelable(false)
			.setPositiveButton ("Email", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					sendEmail();
					dialog.dismiss();
					removeDialog(Constant.DIALOG_EMAIL_ID);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					removeDialog(Constant.DIALOG_EMAIL_ID);
				}

			}).create();
		}
		return null;
	}
	//==================================SENDING THE BACKUP TO EMAIL ID=======================
	public void sendEmail(){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/html");
		i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Phone contacts  Backup");
		i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"/MyContactNumbers("+time+").csv")));
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(DND_Main.this, getResources().getString(R.string.no_email_client), Toast.LENGTH_SHORT).show();
		}
	}
	private String getContactType(int number){
		String type = null;
		switch (number) {
		case 1:
			type="TYPE_HOME";
			break;
		case 2:
			type="TYPE_MOBILE";
			break;
		case 3:
			type="TYPE_WORK";
			break;

		case 4:
			type="TYPE_FAX_WORK";
			break;

		case 5:
			type="TYPE_FAX_HOME";
			break;

		case 6:
			type="TYPE_PAGER";
			break;

		case 7:
			type="TYPE_OTHER";
			break;

		case 8:
			type="TYPE_CALLBACK";
			break;

		case 9:
			type="TYPE_CAR";
			break;

		case 10:
			type="TYPE_COMPANY_MAIN";
			break;

		case 11:
			type="TYPE_ISDN";
			break;

		case 12:
			type="TYPE_MAIN";
			break;

		case 17:
			type="TYPE_WORK_MOBILE";
			break;

		default:
			break;
		}
		return type;

	}

	//================================ADDING CONTACT FROM FILE===========================================
	@SuppressWarnings("deprecation")
	public void addcontact(String name,String number,int Type,String email,String address){
		Boolean match=false;

		for (int i = 0; i < contactList.size(); i++) {
			if(name.equalsIgnoreCase(contactList.get(i).getName())){
				if(number.equalsIgnoreCase(contactList.get(i).getPhoneNo())){
					Log.e("","Name"+name+" and Number"+number+" Matched");
					match=true;
					break;
				}
			}
		}
		if(!match) {
			ContactAdded++;
			Log.e("Added",""+name);
			ContentValues personValues = new ContentValues();
			personValues.put(Contacts.People.NAME, name);
			personValues.put(Contacts.People.STARRED, 1);
			Uri newPersonUri = getContentResolver()
			.insert(Contacts.People.CONTENT_URI, personValues);


			//=================insert Mobile Number=============================
			ContentValues mobileValues = new ContentValues();
			Uri mobileUri = Uri.withAppendedPath(newPersonUri,
					Contacts.People.Phones.CONTENT_DIRECTORY);
			mobileValues.put(Contacts.Phones.NUMBER,
					number);
			mobileValues.put(Contacts.Phones.TYPE,
					Type);
			Uri phoneUpdate = getContentResolver()
			.insert(mobileUri, mobileValues);

			// ===============add email======================
			if(!email.equalsIgnoreCase("")){
				ContentValues emailValues = new ContentValues();
				Uri emailUri = Uri
				.withAppendedPath(
						newPersonUri,
						Contacts.People.ContactMethods
						.CONTENT_DIRECTORY);
				emailValues.put(Contacts.ContactMethods.KIND,
						Contacts.KIND_EMAIL);
				emailValues.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_HOME);
				emailValues.put(Contacts.ContactMethods.DATA,
						email);
				Uri emailUpdate = getContentResolver()
				.insert(emailUri, emailValues);
			}

			//==================== add address============================
			if(!address.equalsIgnoreCase("")){
				ContentValues addressValues = new ContentValues();
				Uri addressUri = Uri
				.withAppendedPath(
						newPersonUri,
						Contacts.People.ContactMethods
						.CONTENT_DIRECTORY);
				addressValues.put(Contacts.ContactMethods.KIND,
						Contacts.KIND_POSTAL);
				addressValues.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_HOME);
				addressValues.put(Contacts.ContactMethods.DATA,
						address);
				Uri addressUpdate = getContentResolver().insert(addressUri,
						addressValues);
			}
		}
	}
	public int TypeId(String type){
		if(type.equalsIgnoreCase("TYPE_CUSTOM")){
			return 0;
		}
		else if(type.equalsIgnoreCase("TYPE_FAX_HOME")){
			return 5;
		}
		else if(type.equalsIgnoreCase("TYPE_FAX_WORK")){
			return 4;
		}
		else if(type.equalsIgnoreCase("TYPE_HOME")){
			return 1;
		}
		else if(type.equalsIgnoreCase("TYPE_MOBILE")){
			return 2;
		}
		else if(type.equalsIgnoreCase("TYPE_OTHER")){
			return 7;
		}
		else if(type.equalsIgnoreCase("TYPE_PAGER")){
			return 6;
		}
		else if(type.equalsIgnoreCase("TYPE_WORK")){
			return 3;
		}
		else {
			return 7;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0){
			if(resultCode==RESULT_OK){
				Bundle bundle=data.getExtras();
				//				Log.e("",""+bundle.getString("file"));
				WriteContactes(bundle.getString(Constant.KEY_FILE));
			}
		}



		super.onActivityResult(requestCode, resultCode, data);
	}
}

