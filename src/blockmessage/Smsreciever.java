package blockmessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.Model;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.constant.Constant;
import com.database.DataBase;

public class Smsreciever extends BroadcastReceiver {
	private DataBase database;
	private static final String TAG = "SMSApp";
	private String no;
	private String message;
	private long messageTimestamp;
	private String messageTime;
	private String BlockNumber="blockNumber",STATUS="status",NAME="name";
	List<Model>   contactList;
	static final String ACTION =
		"android.provider.Telephony.SMS_RECEIVED";
	public void onReceive(Context context, Intent intent) {
		//---get the SMS message passed in---
		database=new DataBase(context);
		contactList=new LinkedList<Model>();

		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;
		String str = "";            
		if (bundle != null)
		{
			//---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];            
			for (int i=0; i<msgs.length; i++){
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);      
				no =msgs[i].getOriginatingAddress();
				messageTimestamp=msgs[i].getTimestampMillis();
				messageTime	=new SimpleDateFormat()
				.format(new Date(messageTimestamp));
				str += "SMS from " + no;                     
				str += " :";
				message = msgs[i].getMessageBody().toString();
				str += "\n";
				Log.e( TAG+"message ",str+messageTime );
			}

			List<Map<String, Object>>  blocknolist=TofetchAllBlockList();
			for (int i = 0; i < blocknolist.size(); i++) {

				//				Log.e("List no  ",blocknolist.get(i)+"");
				if(blocknolist.get(i).get(BlockNumber).toString().equalsIgnoreCase(no)){
					Log.e("status in reciver  ",blocknolist.get(i).get(STATUS).toString());
					if(blocknolist.get(i).get(STATUS).toString().equalsIgnoreCase(Constant.STATUS_BOTH_BLOCK)||blocknolist.get(i).get(STATUS).toString().equalsIgnoreCase(Constant.STATUS_ONLY_SMS)){	

						try {
							database.open();
							ContentValues intialvalues=new ContentValues();
							intialvalues.put(Constant.BLOCK_NO,no);
							intialvalues.put(Constant.BLOCKNO_MESSAGE,message);
							intialvalues.put(Constant.MESSAGE_TIMESTAMP,messageTime);
							intialvalues.put(Constant.BLOCK_PHONENO_NAME,blocknolist.get(i).get("name").toString());
							database.inserteventvalue(Constant.DATABASE_BLOCKLIST_MESSAGETABLE,intialvalues);							    
							database.close();
							Log.e(TAG," match");
							this.abortBroadcast(); 

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}else Log.e("*******************","&&&&&&&");
			}
		}                         
	}
	public List<Map<String, Object>> TofetchAllBlockList(){   
		List<Map<String, Object>>  phonelist=new LinkedList<Map<String,Object>>() ;
		database.open();
		Cursor cursor=database.fetchAllBlockList();
		database.close();

		if(cursor!=null)
		{
			for(int i=0;i<cursor.getCount();i++)
			{
				Map<String ,Object>  item=new HashMap<String,Object>();
				String Blocknumber=cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO));
				Blocknumber=Blocknumber.replace("-","");
				Log.e("no from db", cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCK_NO)));

				item.put(BlockNumber,Blocknumber);
				item.put(STATUS,cursor.getString(cursor.getColumnIndexOrThrow(Constant.PHONENUMBER_STATUS)));
				item.put(NAME,cursor.getString(cursor.getColumnIndexOrThrow(Constant.BLOCKNO_NAME)));
				phonelist.add(item);
				cursor.moveToNext();
			}
		}
		return phonelist;
	}

}
