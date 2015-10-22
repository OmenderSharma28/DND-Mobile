package com.donotdisturb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class OutgoingMessage  extends BroadcastReceiver{
//	private static final String ACTION = "android.provider.Telephony.SEND_SMS";
//	public static int MSG_TPE=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		String MSG_TYPE=intent.getAction();
		if(MSG_TYPE.equals("android.provider.Telephony.SMS_RECEIVED"))
		{
			Log.e("in mesage action recieved ", "****");
			Bundle bundle = intent.getExtras();
			Object messages[] = (Object[]) bundle.get("pdus");
			SmsMessage smsMessage[] = new SmsMessage[messages.length];
			for (int n = 0; n < messages.length; n++) 
			{
				smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			}

			Toast toast = Toast.makeText(context,"BLOCKED Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
			toast.show();
			abortBroadcast();
			for(int i=0;i<8;i++)
			{
				System.out.println("Blocking SMS   recieved **********************");
			}

		}
		if(MSG_TYPE.equals("android.provider.Telephony.SEND_SMS"))
		{
			Log.e("in mesage action send ", "****");
			        Toast toast = Toast.makeText(context,"SMS SENT: "+MSG_TYPE , Toast.LENGTH_LONG);
			        toast.show();
			abortBroadcast();
			        for(int i=0;i<8;i++)
			        {
			            System.out.println("Blocking SMS  in send sms  in if condition ");
			        }

		}
		    else
		    {
		
		        Toast toast = Toast.makeText(context,"SIN ELSE: "+MSG_TYPE , Toast.LENGTH_LONG);
		        toast.show();
		        abortBroadcast();
		        for(int i=0;i<8;i++)
		        {
		            System.out.println("Blocking SMS   in send sms  in else  **********************");
		        }
		
		    }

	}
}
