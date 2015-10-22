//package getcontact;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.xml.sax.Parser;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.ContactsContract;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.donotdisturb.R;
//
//public class Invite_Friends  extends Activity  {
//	private	ProgressDialog dialog;
//	private	boolean mShowInvisible;
//	private	String hasPhone="";
//	private CheckBox cheackmarkCheckBox;
//	private	ArrayList<String> contact = new ArrayList<String>();
//	@SuppressWarnings("rawtypes")
//	private	Set addContacts = new HashSet();
//	private	Thread contactsThread;
//private int clickposition;
//	private	List<Map<String,?>> contactList = new LinkedList<Map<String,?>>();
//
//	private ListView contactlistview;
//
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.invite_friend);
//
//		contactlistview= (ListView)findViewById(R.id.ContacrfriendlistView1);
//
//		contactList();
//
//	}
//	public void contactList(){
//		contactsThread = new Thread(){
//			//			@SuppressWarnings("unchecked")
//			public void run(){
//
//				Cursor cursor = getContacts();
//				startManagingCursor(cursor);
//				cursor.moveToFirst();
//
//				for (int i = 0; i < cursor.getCount(); i++) 
//				{
//					Log.w("id, name, has number", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))+", "+cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))+", " +cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//					if ( cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1"))
//						hasPhone = "true";
//					else
//						hasPhone = "false" ;
//
//
//
//
//					if (Boolean.parseBoolean(hasPhone)) 
//					{
//						//						Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),null, null);
//						//						startManagingCursor(emails);
//						Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(  ContactsContract.Contacts._ID)) , null, null); 
//						startManagingCursor(phones);
//						while (phones.moveToNext()) { 
//							String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));                 
//							//						while (emails.moveToNext() )
//							//						{
//							Log.e("phone numbers", phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//							Log.e("",cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)+","+phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//							//							addContacts.add(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))+","+emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//							addContacts.add(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))+","+phones.getString(phones.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.Phone.NUMBER)));
//
//						}
//						phones.moveToNext();
//						//						emails.moveToNext();
//
//					}
//
//					cursor.moveToNext();
//				}
//
//				Iterator it = addContacts.iterator();
//				while (it.hasNext()) {
//					// Get element
//					Object o = it.next();
//					contact.add(o.toString());
//				}
//				for (int i = 0; i < contact.size(); i++) 
//				{
//					Map<String,String> item1 = new HashMap<String,String>();  
//					item1.put("NAME", contact.get(i).substring(0, contact.get(i).indexOf(",")));
//					item1.put("PHONENO",contact.get(i).substring(contact.get(i).indexOf(",")+1, contact.get(i).length()));
//					Log.e("value map",item1.get("NAME"));
//					Log.e("value map",item1.get("E-MAIL"));
//					contactList.add(item1); 
//				}
//
//				Message myMessage=new Message();
//				myMessage.obj="successful";
//				contactHandler.sendMessage(myMessage); 
//			}
//		};
//		contactsThread.start();
//	}
//
//	private Handler contactHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//
////			dismissDialog(1);
//			contactsThread.interrupt();
//
//			Searchfriendadapter mylistAdapter = new Searchfriendadapter(Invite_Friends.this,contactList); 
//			contactlistview.setAdapter(mylistAdapter);
//
//
//
//		}
//	};
//
//	private Cursor getContacts()
//	{
//		// Run query
//		Uri uri = ContactsContract.Contacts.CONTENT_URI;
//		String[] projection = new String[] {
//				ContactsContract.Contacts._ID,
//				ContactsContract.Contacts.DISPLAY_NAME,
//				ContactsContract.Contacts.HAS_PHONE_NUMBER,
//
//		};
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (mShowInvisible ? "0" : "1") + "'";
//		String[] selectionArgs = null;
//		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//
//		return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
//	}
//
//
//
//	public class Searchfriendadapter extends BaseAdapter{
//
//
//		private LayoutInflater mInflater;
//		View view;
//		boolean ISFACEBOOK=false;
//
//		private	List<Map<String,?>> Friendslist;
//
//		public Searchfriendadapter(Context context, List<Map<String,?>> results) {
//
//			Friendslist = results;
//			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			Log.e("***********&&&&&&&&&&&", "in searchfriend  constructer ");
//		}
//
//		public int getCount() {
//
//			return Friendslist.size();
//		}
//
//		public Object getItem(int position) {
//
//			return Friendslist.get(position);
//
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			//			ViewHolder holder;
//			view = mInflater.inflate(R.layout.inflatefriens_row, null);
//			TextView  contactname = (TextView) view.findViewById(R.id.ContactNametextView1);
//			TextView  phoneno= (TextView) view.findViewById(R.id.PhoneNotextView1);
//			cheackmarkCheckBox=(CheckBox)view.findViewById(R.id.selectfriendcheckBox1);
//
//			contactname.setText(Friendslist.get(position).get("NAME").toString());
//			phoneno.setText(Friendslist.get(position).get("PHONENO").toString());
//			System.out.println("value  category..."+Friendslist.get(position));
//
//			cheackmarkCheckBox.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					//					TODO  make cheack image 
//					Log.e("dbasbdask","adabdbadba");
//					clickposition=position;
//				}
//			});
//
//			return  view;
//
//		}
//
//
//	}
//
//
//	private static 	class ViewHolder {
//		TextView username;
//
//	}
//
//	public  void Disable_user_Intration(){
//		dialog = new ProgressDialog(Invite_Friends.this);
//		dialog.setCancelable(true);
//		dialog.setMessage("Loading...");
//		dialog.show();
//	}
//
//
//
//}
