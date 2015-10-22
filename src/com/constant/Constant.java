package com.constant;

public class Constant {

	public static final String  ROWID = "_id";
	public static final String  BLOCK_NO = "block_no";
	public static final String  BLOCKNO_NAME = "block_name";
	public  static final String DATABASE_BLOCKLIST_TABLE = "blockphonelist_table"; 
	public  static final String DATABASE_BLOCKLIST_MESSAGETABLE = "blockphonelist_messagetable";
	public  static final String DATABASE_PHONENO_LIST_TOMESSAGETABLE = "messagephonelist_table";
	public static final String DATABASE_BLOCKLIST_CALL_TABLE="blockphonelist_calltable";
	public static final String  BLOCKNO_MESSAGE = "block_message";
	public static final String  MESSAGE_TIMESTAMP = "message_timestamp";
	public static final String BLOCK_SINCE="block_since";
	public static final String  PHONENUMBER_STATUS = "number_status";
	public static final String  MESSAGE_STATUS = "message_status";

	public static final String MobileUser="user";
	public static final String CallType="callType";
	public static final String ContactId="contact_id";

	public static final String USER_ID = "user_id";
	public static final String E_MAIL = "E_MAIL";
	public static final String PASSWORD = "pass_word";

	public static final String TASK_NAME = "task_name";

	public static final String SET_DAY = "set_day";
	public static final String SET_MONTH = "set_month";
	public static final String SET_YEAR = "set_year";

	public static final String SET_DATE = "set_date";

	public static final String SET_HOUR = "set_hr";
	public static final String SET_MIN = "set_min";

	public static final String SET_TIME = "set_time";

	public static final String SET_PROFILE_DATE = "profile_date";
	public static final String SET_PROFILE_TIME = "profile_time";
	public static final String SET_PROFILE_END_TIME="profile_end_time";
	public static final String SET_PROFILE_VIBRATE_RING = "profile_vibrate_ring";
	public static final String SET_PROFILE_FULL_DATE_TIME = "set_full_date_time";

	public static final String POSTAL_ADDRESS="POSTAL_ADDRESS";
	public static final String SET_STREET = "set_street";
	public static final String SET_CITY = "set_city";
	public static final String SET_STATE = "set_state";
	public static final String SET_ZIP = "set_zip";
	public static final String SET_COUNTRY = "set_country";
	public static final String SET_SUPERVISOR_EMAIL = "set_mail";
	public static final String SET_SUPERVISOR_NO = "set_no";
	public static final String SET_LATITUDE = "set_lat";
	public static final String SET_LONGITUDE = "set_lon";
	public static final String SET_MAIL = "click_mail";
	public static final String SET_SMS = "click_sms";
	public static final String SET_FULL_DATE_TIME = "full_date_time";


	public static  Boolean IncommingCall=false;

	public static final String DEFAULT_USERID="DEFAULT";

	public static final String  PHONENO_FORMESSAGE="phonne_tosend_message";
	public static final String  MESSAGE_TO_SEND="message_tosend";
	public static final String  MESSAGE_SENDING_DATE="date";
	public static final String  MESSAGE_SENDING_TIME="time";
	public static final String  PENDING_RESULTCODE="pending_id";

	public static final String   BLOCK_PHONENO_NAME="name";
	public static final String   BLOCK_PHONENO="no";
	public static final String   BLOCK_PHONENO_STATUS="status";
	public static final String   BLOCK_PHONENO_ROWID="rowId";

	public static final int  REQUESTCODE_FORBLOCKN_DETAIL=101;
	public static final int  REQUESTCODE_TO_BLOCKNUMBERS=102;
	public static final int  REQUESTCODE_TO_BLOCKMESSAGE_DETAIL=103;
	public static final int  REQUESTCODE_TO_BLOCK_INPUT_NO=105;

	public static final String  SELECT_TITLE = "TITLE";
	public static final String  DELETEKEY = "DELETE";

	public static final String  DETAILED_BLOCK_MESSAGE="messsage";
	public static final String  DETAILED_BLOCK_MESSAGE_NO="mno";
	public static final String  DETAILED_BLOCK_MESSAGE_NAME="mname";
	public static final String  DETAILED_BLOCK_MESSAGE_TIME="mtime";
	public static final String  DETAILED_BLOCK_MESSAGE_ROWID="mrowId";


	public static final String MESSAGE_STAUS_SEND="Sent";
	public static final String MESSAGE_STAUS_TO_be_SEND="To be send";
	public static final String  NAME="NAME";
	public static final String  NUMBER="NUMBER";
	public static final String  TYPE="CONTACT NUMBER TYPE";

	public static final int DIALOG_EMAIL_ID=1001;
	public static final int DELETE_MESSAGE=105;
	public static final int DELETE_PHONE_NO=110;
	public static final int DATE_DIALOG_ID=0;
	public static  final int TIME_DIALOG_ID=1;
	public static final int EMPTY_FIELD_DIALOG = 200;

	public static final String STATUS_ONLY_CALL="only call";
	public static final String STATUS_ONLY_SMS="only sms";
	public static final String STATUS_BOTH_BLOCK="both";

	public static final int VIBRATE_SETTING_OFF = 0;
	public static final int RINGER_MODE_VIBRATE_TRUE = 1;
	public static final int RINGER_MODE_VIBRATE_FALSE = 0;

	public static final String VIBRATE="Vibrate";
	public static final String RING="Ring";
	public static final String SILENT="Silent";
	public static final String VIBRATE_RING="vibrate_ring";

	public static final String MESSAGE_URI="content://sms/inbox";
	public static final String KEY_FILE="file";
	public static final String KEY_PROFILE_START="profile_start",KEY_ID="id";
}