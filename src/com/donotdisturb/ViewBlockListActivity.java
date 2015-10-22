package com.donotdisturb;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import model.BlockNumberBean;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.constant.Constant;
import com.database.DataBase;

public class ViewBlockListActivity extends ListActivity {
	private List<BlockNumberBean> list_map = new LinkedList<BlockNumberBean>();
	private DataBase database;
	//	private Cursor cursor;
	private ListView listview;
	private MyListAdapter mylistAdapter ;
	private String Name = "name", Number = "number", Type = "type",RowId="RowId",ContactId="id",ACTION="action";
	private String block_since="block_since";
	private String DELETE="delete";
	private final int UPDATE=0;//,DELETE_ID=1;
	private int clickedPosition;
	private int ALL=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.block_list);
		database = new DataBase(ViewBlockListActivity.this);
		listview = getListView();

		showAllList(ALL);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				final int position_item=position;
				//				Log.e("","@@@@@@@@@"+position+list_map.get(position).getName());
				new AlertDialog.Builder(ViewBlockListActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Attention!")
				.setMessage(getResources().getString(R.string.block_list_message))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						Log.e("","Ok");
						database.open();
						database.deleteNo(Integer.parseInt(list_map.get(position_item).getRowId()));
						database.close();
						list_map.remove(position_item);
						mylistAdapter.notifyDataSetChanged();
					}
				})	
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						dialog.dismiss();
					}
				})	
				.show(); 
				return true;
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		clickedPosition=position;
		Intent intent = new Intent(ViewBlockListActivity.this,
				ShowHistry.class);
		intent.putExtra(Number, list_map.get(position).getNumber());
		intent.putExtra(Name,list_map.get(position).getName());
		intent.putExtra(Type, list_map.get(position).getType());
		intent.putExtra(ContactId, list_map.get(position).getContactId());
		intent.putExtra(RowId, list_map.get(position).getRowId());
		intent.putExtra(block_since, list_map.get(position).getBlock_since());
		startActivityForResult(intent, UPDATE);

	}
	public void click(View view){
		switch (view.getId()) {
		
		case R.id.btn_option:
			final RadioGroup radiogroup = new RadioGroup(ViewBlockListActivity.this);
			final RadioButton blockedCalls = new RadioButton(ViewBlockListActivity.this);
			blockedCalls.setText(getResources().getString(R.string.view_blocked_calls));
			blockedCalls.setButtonDrawable(R.drawable.radio_button);
			final RadioButton blockedSMS = new RadioButton(ViewBlockListActivity.this);
			blockedSMS.setText(getResources().getString(R.string.view_blocked_sms));
			blockedSMS.setButtonDrawable(R.drawable.radio_button);
			final RadioButton viewALl = new RadioButton(ViewBlockListActivity.this);
			viewALl.setText(getResources().getString(R.string.view_all_blocked));
			viewALl.setButtonDrawable(R.drawable.radio_button);
			radiogroup.addView(blockedCalls);
			radiogroup.addView(blockedSMS);
			radiogroup.addView(viewALl);
			viewALl.setChecked(true);

			new AlertDialog.Builder(ViewBlockListActivity.this)
			.setTitle(getResources().getString(R.string.view_list))
			.setCancelable(false)
			.setView(radiogroup)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					int blockType =0;

					if (blockedCalls.isChecked() == true) {
						blockType =1;
					} else if (blockedSMS.isChecked() == true) {
						blockType = 2;

					}
					else if (viewALl.isChecked() == true) {
						blockType = 0;
					}
					showAllList(blockType);
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

		default:
			break;
		}
	}

	public void showAllList(int type) {
		int isfrom=type;
		list_map.clear();
		database.open();
		Cursor cursor = database.fetchAllBlockList();
		database.close();
		if(cursor.getCount()>0){
			Log.e("@@@",""+cursor.getCount());
			startManagingCursor(cursor);
			cursor.moveToFirst();
			if(isfrom==0){
				//			Map<String, String> map;
				BlockNumberBean number;
				for (int i = 0; i < cursor.getCount(); i++) {
					//				map = new HashMap<String, String>();
					//					Log.e("",""+cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
					number=new BlockNumberBean();
					number.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO)));
					number.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ContactId)));
					number.setName(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCKNO_NAME)));
					number.setRowId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ROWID)));
					number.setType(cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
					number.setBlock_since(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_SINCE)));
					list_map.add(number);
					cursor.moveToNext();
				}
			}else if(isfrom==1){
				BlockNumberBean number;
				for (int i = 0; i < cursor.getCount(); i++) {
					//					Log.e("",""+cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
					if(cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)).equalsIgnoreCase(Constant.STATUS_ONLY_CALL)){
						number=new BlockNumberBean();
						number.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO)));
						number.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ContactId)));
						number.setName(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCKNO_NAME)));
						number.setRowId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ROWID)));
						number.setType(cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
						list_map.add(number);
					}
					cursor.moveToNext();

				}
			}else if(isfrom==2){
				BlockNumberBean number;
				for (int i = 0; i < cursor.getCount(); i++) {
					//					Log.e("",""+cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
					if(cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)).equalsIgnoreCase(Constant.STATUS_ONLY_SMS)){
						number=new BlockNumberBean();
						number.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO)));
						number.setContactId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ContactId)));
						number.setName(cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCKNO_NAME)));
						number.setRowId(cursor.getString(cursor.getColumnIndexOrThrow(Constant.ROWID)));
						number.setType(cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
						list_map.add(number);
					}
					cursor.moveToNext();

				}
			}

			mylistAdapter = new MyListAdapter(this, list_map,
					R.layout.blocklist_item, new String[] { Name, Number },
					new int[] { R.id.txv_name, R.id.txv_number });
			setListAdapter(mylistAdapter);
		}


	}

	public class MyListAdapter extends BaseAdapter {
		List<BlockNumberBean> mData;
		Context mContext;
		String[] mFrom;
		int[] mTo;
		LayoutInflater mInflater;
		View view;
		ImageView image, callblock, smsblock;
		TextView name;
		TextView number;

		public MyListAdapter(Context context, List<BlockNumberBean> data,
				int resource, String[] from, int[] to) {

			mData = data;
			mContext=context;
			mFrom = from;
			mTo = to;

			mInflater = (LayoutInflater) ViewBlockListActivity.this
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			view = mInflater.inflate(R.layout.blocklist_item, null, false);
			image = (ImageView) view.findViewById(R.id.Contact_image);
			name = (TextView) view.findViewById(R.id.txv_name);
			number = (TextView) view.findViewById(R.id.txv_number);
			callblock = (ImageView) view.findViewById(R.id.image_blockcall);
			smsblock = (ImageView) view.findViewById(R.id.image_blocksms);
			String[] ids=mData.get(position).getContactId().split("-");
			try {
				int _id=Integer.parseInt(ids[0]);
				int _photoid=Integer.parseInt(ids[1]);
				image.setImageBitmap(loadContactPhoto(_id,_photoid));
			} catch (Exception e) {
				//				e.printStackTrace();
			}
			name.setText(mData.get(position).getName());
			number.setText(mData.get(position).getNumber());
			if (mData.get(position).getType().toString()
					.equalsIgnoreCase(Constant.STATUS_ONLY_SMS)) {
				callblock.setVisibility(View.GONE);
			}
			if (mData.get(position).getType().toString()
					.equalsIgnoreCase(Constant.STATUS_ONLY_CALL)) {
				smsblock.setVisibility(View.GONE);
			}
			if(position%2==0){
			view.setBackgroundResource(R.drawable.contacthint_list_bg1);	
			}
			else{
			view.setBackgroundResource(R.drawable.contacthint_list_bg2);
			}
			return view;
		}
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
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==UPDATE){
			if(resultCode==RESULT_OK){
				Bundle bundle= data.getExtras();
				if(bundle.getString(ACTION).equalsIgnoreCase(DELETE)){
					database.open();
					database.deleteNo(Integer.parseInt(list_map.get(clickedPosition).getRowId()));
					database.close();
					list_map.remove(clickedPosition);
					mylistAdapter.notifyDataSetChanged();
				}else {
					String blocktype=bundle.getString("blocktype");
					list_map.get(clickedPosition).setType(blocktype);
					mylistAdapter.notifyDataSetChanged();
				}
			}
		}
	}
}



