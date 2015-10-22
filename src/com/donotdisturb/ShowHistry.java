package com.donotdisturb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.constant.Constant;
import com.database.DataBase;

public class ShowHistry extends ExpandableListActivity {
	private String Mobile_no;
	private String Mobile_name;
	private String NAME="Group",KeyRowId="RowId";
	private int RowId;//typeBlock;
	private ExpandableListView expendable;
	private ExpandableListAdapter mAdapter;
	private List<Map<String, String>> messageData;
	private List<Map<String, String>> message;
	private List<Map<String, String>> calls;
	private List<List<Map<String, String>>> childData;
	private DataBase database;
	private String Number="number",Message="message",Name="name",Type="type",ContactId="id",ACTION="action";
	private String UPDATE="update",DELETE="delete";
	private String key_block_since="block_since";
	private String[] Title;
	private int BlockType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_history);
		database=new DataBase(ShowHistry.this);
		Title=new String[]{"Blocked Message","Blocked Call"};
		TextView name=(TextView)findViewById(R.id.name_txv);
		TextView block_since=(TextView)findViewById(R.id.block_since);
		TextView number=(TextView)findViewById(R.id.number_txv);
		ImageView image=(ImageView)findViewById(R.id.image);
		Bundle bundle=getIntent().getExtras();
		messageData = new ArrayList<Map<String, String>>();
		message = new ArrayList<Map<String, String>>();
		calls = new ArrayList<Map<String, String>>();
		childData = new ArrayList<List<Map<String, String>>>();
		if(bundle!=null){

			Mobile_no=bundle.getString(Number);
			Mobile_no=Mobile_no.replace("-","");
			Mobile_name=bundle.getString(Name);
			String block_date=bundle.getString(key_block_since);
			
			block_since.setText(getResources().getString(R.string.block_person)+" "+block_date);
			RowId=Integer.parseInt(bundle.getString(KeyRowId));
			String[] ids=bundle.getString(ContactId).split("-");
			image.setImageBitmap(loadContactPhoto(ids));
			name.setText(Mobile_name);
			number.setText(Mobile_no);
			String type= bundle.getString(Type);
			if(type.equalsIgnoreCase(Constant.STATUS_ONLY_CALL)){
				BlockType=0;
			} else if(type.equalsIgnoreCase(Constant.STATUS_ONLY_SMS)){
				BlockType=1;
			}else if(type.equalsIgnoreCase(Constant.STATUS_BOTH_BLOCK)){
				BlockType=2;
			} 
		}
		
		blockMessage();
		calllist();
		filllist();
	}
	public void filllist(){
		for (int i = 0; i < Title.length; i++) {
			if(i==BlockType){
				continue;
			}
			Map<String, String> curGroupMap = new HashMap<String, String>();
			curGroupMap.put(NAME, Title[i]);
			messageData.add(curGroupMap);
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			if(i==0){
				for (int j = 0; j<message.size(); j++) {
					Map<String, String> curChildMap = new HashMap<String, String>();
					if(message.get(j).get(Constant.DETAILED_BLOCK_MESSAGE).equalsIgnoreCase("No Messages")){
						curChildMap.put(Message,message.get(j).get( Constant.DETAILED_BLOCK_MESSAGE));
					}
					else{
						curChildMap.put(Message, message.get(j).get(Constant.DETAILED_BLOCK_MESSAGE)+" At "+message.get(j).get(Constant.MESSAGE_TIMESTAMP));
					}
					children.add(curChildMap);
				}
			}
			if(i==1){
				for (int j = 0; j<calls.size(); j++) {
					Map<String, String> curChildMap = new HashMap<String, String>();
					if(calls.get(j).get(Constant.CallType).equalsIgnoreCase("No Calls")){
						curChildMap.put(Message, calls.get(j).get(Constant.CallType));
					}else{
						curChildMap.put(Message, calls.get(j).get(Constant.CallType)+"call At "+calls.get(j).get(Constant.MESSAGE_TIMESTAMP));
					}
					children.add(curChildMap);
				}
			}
			childData.add(children);
		}	
		mAdapter = new SimpleExpandableListAdapter(
				getApplicationContext(),
				messageData,
				R.layout.group,
				new String[] { NAME},
				new int[] { R.id.textView1 },
				childData,
				R.layout.child,
				new String[] { Message},
				new int[] { R.id.blockMessage}
		);
		setListAdapter(mAdapter);
	}
	public void  blockMessage(){   
		message.clear();
		database.open();
		Cursor cursor=database.fetchAllBlockMessageList();
		database.close();
		Log.e("","Block message "+cursor.getCount());
		if(cursor!=null)
		{
			for(int i=0;i<cursor.getCount();i++)
			{
				if(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO)).equalsIgnoreCase(Mobile_no)){

					Map<String, String> item=new HashMap<String, String>();
					item.put(Constant.DETAILED_BLOCK_MESSAGE,cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCKNO_MESSAGE)));
					item.put(Constant.MESSAGE_TIMESTAMP, cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_TIMESTAMP)));
					message.add(item);
				}
				cursor.moveToNext();
			}
		}
		cursor.close();
		if(message.size()==0){
			Map<String, String> item=new HashMap<String, String>();
			item.put(Constant.DETAILED_BLOCK_MESSAGE,"No Messages");
			message.add(item);
		}
	}
	public void  calllist(){   
		calls.clear();
		database.open();
		Cursor cursor=database.fetchBlockCalls();
		database.close();
		Log.e("","call list"+cursor.getCount());
		if(cursor!=null)
		{
			for(int i=0;i<cursor.getCount();i++)
			{
				Map<String, String> item=new HashMap<String, String>();
				item.put(Constant.CallType,cursor.getString(cursor.getColumnIndexOrThrow(Constant.CallType)));
				item.put(Constant.MESSAGE_TIMESTAMP,cursor.getString(cursor.getColumnIndexOrThrow(Constant.MESSAGE_TIMESTAMP)));
				calls.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();
		if(calls.size()==0){
			Map<String, String> item=new HashMap<String, String>();
			item.put(Constant.CallType,"No Calls");
			calls.add(item);
		}
	}//long id, long photoId
	private Bitmap loadContactPhoto(String[] ids) {
		int id=0,photoId=0;
		if(ids.length==2){
			id= Integer.parseInt(ids[0]);
			if(!ids[1].equalsIgnoreCase("null")){
				photoId=Integer.parseInt(ids[1]);
			}
		}
		ContentResolver cr =getContentResolver();
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
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.defaultimage);
		return icon;
	}

	public void click(View view){
		if(view.getId()==R.id.button1){
//			Log.e("","Button clicked"+BlockType);
			final RadioGroup radiogroup= new RadioGroup(getApplicationContext());
			final RadioButton blockSMS= new RadioButton(getApplicationContext());
			blockSMS.setText(getString(R.string.block_sms));
			blockSMS.setButtonDrawable(R.drawable.radio_button);
			final RadioButton blockCalls= new RadioButton(getApplicationContext());
			blockCalls.setText(getString(R.string.block_call));
			blockCalls.setButtonDrawable(R.drawable.radio_button);
			final RadioButton blockBoth= new RadioButton(getApplicationContext());
			blockBoth.setText(getString(R.string.block_both));
			blockBoth.setButtonDrawable(R.drawable.radio_button);
			final RadioButton delete= new RadioButton(getApplicationContext());
			delete.setText(getString(R.string.delete_block_list));
			delete.setButtonDrawable(R.drawable.radio_button);

			radiogroup.addView(blockSMS);
			radiogroup.addView(blockCalls);
			radiogroup.addView(blockBoth);
			radiogroup.addView(delete);

			if(BlockType==0){
				blockCalls.setChecked(true);
			}else if(BlockType==1){
				blockSMS.setChecked(true);
			}else{
				blockBoth.setChecked(true);
			}

			new AlertDialog.Builder(ShowHistry.this)
			.setTitle(getString(R.string.block_setting))
			.setCancelable(false)
			.setView(radiogroup)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{ 
					String status="";
					String action="";

					if(blockCalls.isChecked()==true){
						status=Constant.STATUS_ONLY_CALL; action=UPDATE;
					}
					else if(blockSMS.isChecked()==true){
						status=Constant.STATUS_ONLY_SMS; action=UPDATE;

					}else if(blockBoth.isChecked()==true){
						status=Constant.STATUS_BOTH_BLOCK; action=UPDATE;
					}else if(delete.isChecked()==true){
						action=DELETE;
					}
					database.open();
					database.updateBlockNodata(RowId, Mobile_no, status, Mobile_name);
					database.close();
					dialog.dismiss();
					Intent mIntent = new Intent();
					mIntent.putExtra("blocktype",status);
					mIntent.putExtra(ACTION, action);
					setResult(RESULT_OK, mIntent);
					finish();
				}
			})	
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					dialog.dismiss();
				}
			})	
			.show(); 
		}
		
	}
}
