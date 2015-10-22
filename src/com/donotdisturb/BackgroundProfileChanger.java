package com.donotdisturb;

import com.constant.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//		broadcast receiver class
public class BackgroundProfileChanger extends BroadcastReceiver 
{
	private Context myContext;
	private String id;
	private Boolean profile_start=null;
	private Bundle b;
	
	@Override                        
	public void onReceive(Context context, Intent intent)
	{				
		myContext = context;
		b = intent.getExtras();
		id=b.getString(Constant.KEY_ID);	
		profile_start=b.getBoolean(Constant.KEY_PROFILE_START);
		callIntent();	
	}

public void callIntent()
	{
		Intent newIntent = new Intent();		
		newIntent.setClass(myContext, AfterProfileReciever.class);
		newIntent.setAction(AfterProfileReciever.class.getName());
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		newIntent.putExtra(Constant.KEY_ID, id);	
		newIntent.putExtra(Constant.KEY_PROFILE_START, profile_start);
		myContext.startActivity(newIntent);
}
}
