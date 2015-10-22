package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.constant.Constant;

public class DataBase {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private static final String DATABASE_NAME = "blockNumberdata";
	private static final String DATABASE_TABLE_PROFILE_CHANGER = "profile_changer_table";

	private static final int DATABASE_VERSION = 12;
	private final Context mCtx;
	private static final String DATABASE_CREATE_BLOCKLIST_TABLE = "create table "
		+ Constant.DATABASE_BLOCKLIST_TABLE
		+ " ("
		+ Constant.ROWID
		+ " integer primary key autoincrement,"
		+ Constant.BLOCK_NO
		+ " text,"
		+ Constant.BLOCK_SINCE
		+ " text,"
		+ Constant.ContactId
		+ " text,"
		+ Constant.BLOCKNO_NAME
		+ " text, "
		+ Constant.PHONENUMBER_STATUS + " text )";
	private static final String DATABASE_CREATE_BLOCKLIST_MESSAGETABLE = "create table "
		+ Constant.DATABASE_BLOCKLIST_MESSAGETABLE
		+ " ("
		+ Constant.ROWID
		+ " integer primary key autoincrement,"
		+ Constant.BLOCK_NO
		+ " text,"
		+ Constant.BLOCKNO_MESSAGE
		+ " text,"
		+ Constant.MESSAGE_TIMESTAMP
		+ " text,"
		+ Constant.BLOCK_PHONENO_NAME + " text)";
	private static final String DATABASE_CREATE_PHONELIST_TOMESSAGE_TABLE = "create table "
		+ Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE
		+ " ( "
		+ Constant.ROWID
		+ " integer primary key autoincrement, "
		+ Constant.PHONENO_FORMESSAGE
		+ " text, "
		+ Constant.MobileUser
		+ " text, "
		+ Constant.MESSAGE_TO_SEND
		+ " text, "
		+ Constant.MESSAGE_SENDING_DATE
		+ " text, "
		+ Constant.MESSAGE_STATUS + " text )";
	private static final String DATABASE_CREATE_PROFILE_CHANGER_TABLE = "create table "
		+ DATABASE_TABLE_PROFILE_CHANGER
		+ " ("
		+ Constant.ROWID
		+ " integer primary key autoincrement,"
		+ Constant.SET_PROFILE_FULL_DATE_TIME
		+ " datetime,"
		+ Constant.SET_PROFILE_VIBRATE_RING
		+ " text,"
		+ Constant.SET_PROFILE_END_TIME + " datetime)";
	private static final String DATABASE_CREATE_BLOCKLIST_CALL_TABLE = "create table "
		+ Constant.DATABASE_BLOCKLIST_CALL_TABLE
		+ " ( "
		+ Constant.ROWID
		+ " integer primary key autoincrement,"
		+ Constant.MESSAGE_TIMESTAMP
		+ " text ,"
		+ Constant.BLOCK_NO
		+ " text ,"
		+Constant.CallType
		+ " text )";

	public class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		};

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_BLOCKLIST_TABLE);
			db.execSQL(DATABASE_CREATE_BLOCKLIST_MESSAGETABLE);
			db.execSQL(DATABASE_CREATE_PHONELIST_TOMESSAGE_TABLE);
			db.execSQL(DATABASE_CREATE_PROFILE_CHANGER_TABLE);
			db.execSQL(DATABASE_CREATE_BLOCKLIST_CALL_TABLE);
			Log.e("in on create method ", "dtabase ");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS emp_table");
			onCreate(db);
		}
	}

	public DataBase(Context ctx) {
		mCtx = ctx;
	}

	public DataBase open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null,
				DATABASE_VERSION);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long inserteventvalue(String tableName, ContentValues initialvalues) {
		return mDb.insert(tableName, null, initialvalues);
	}

	public Cursor fetchData(int id) {
		Cursor mCursor = mDb.rawQuery("select * from "
				+ Constant.DATABASE_BLOCKLIST_TABLE + " where "
				+ Constant.ROWID + "='" + id + "'", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllBlockList() {

		Cursor mCursor = mDb.rawQuery("select " + Constant.ROWID + ","
				+ Constant.BLOCK_NO + " ," + Constant.BLOCKNO_NAME + "," + Constant.ContactId + ","
				+ Constant.PHONENUMBER_STATUS +" ,"
				+Constant.BLOCK_SINCE
				+ " from "
				+ Constant.DATABASE_BLOCKLIST_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllBlockMessageList() {

		Cursor mCursor = mDb.rawQuery("select " + Constant.ROWID + ","
				+ Constant.BLOCK_NO + " ," + Constant.BLOCKNO_MESSAGE + ","
				+ Constant.BLOCK_PHONENO_NAME + " ,"
				+ Constant.MESSAGE_TIMESTAMP + " from "
				+ Constant.DATABASE_BLOCKLIST_MESSAGETABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean deleteNo(int id) {
		return mDb.delete(Constant.DATABASE_BLOCKLIST_TABLE, Constant.ROWID
				+ "=" + id, null) > 0;

	}

	public boolean wholeListdelete() {
		return mDb.delete(Constant.DATABASE_BLOCKLIST_TABLE, null, null) > 0;

	}

	public boolean updateBlockNodata(int id, String no, String status,
			String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(Constant.BLOCK_NO, no);
		initialValues.put(Constant.PHONENUMBER_STATUS, status);
		initialValues.put(Constant.BLOCKNO_NAME, name);
		return mDb.update(Constant.DATABASE_BLOCKLIST_TABLE, initialValues,
				Constant.ROWID + "=" + id, null) > 0;
	}

	public Cursor fetchAllNOList() {

		Cursor mCursor = mDb.rawQuery("select " + Constant.ROWID + ","
				+ Constant.PHONENO_FORMESSAGE + " ," + Constant.MESSAGE_TO_SEND
				+ " from " + Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetch_Data2(long rowId) throws SQLException {
		Log.w("DBAdapter", Long.toString(rowId));

		Cursor mCursor = mDb.rawQuery("select * from "
				+ DATABASE_TABLE_PROFILE_CHANGER + " where " + Constant.ROWID
				+ "= " + rowId, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetch_DataMessage2(long rowId) throws SQLException {
		Log.w("DBAdapter", Long.toString(rowId));

		Cursor mCursor = mDb.rawQuery("select * from "
				+ Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE + " where "
				+ Constant.ROWID + "= " + rowId, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public long insertProfileChangerData(
			String full_date_time, String mode,
			String endTime) {
		ContentValues initialvalues = new ContentValues();
		//		initialvalues.put(Constant.USER_ID, u_id);
		//		initialvalues.put(Constant.SET_PROFILE_DATE, date);
		//		initialvalues.put(Constant.SET_PROFILE_TIME, time);
		initialvalues.put(Constant.SET_PROFILE_FULL_DATE_TIME, full_date_time);
		initialvalues.put(Constant.SET_PROFILE_VIBRATE_RING, mode);

		//		initialvalues.put(Constant.SET_DAY, s_day);
		//		initialvalues.put(Constant.SET_MONTH, s_month);
		//		initialvalues.put(Constant.SET_YEAR, s_year);

		//		initialvalues.put(Constant.SET_HOUR, s_hr);
		//		initialvalues.put(Constant.SET_MIN, s_min);
		initialvalues.put(Constant.SET_PROFILE_END_TIME, endTime);

		return mDb.insert(DATABASE_TABLE_PROFILE_CHANGER, null, initialvalues);
	}

	public Cursor sortDate2() {
		String Query="SELECT * FROM "
			+ DATABASE_TABLE_PROFILE_CHANGER + " where "
			+ Constant.SET_PROFILE_FULL_DATE_TIME
			+ " > datetime('now','localtime') order by "
			+ Constant.SET_PROFILE_FULL_DATE_TIME + " limit 1";

		Cursor mCursor = mDb.rawQuery(Query, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllProfile() {
		return mDb.query(DATABASE_TABLE_PROFILE_CHANGER, new String[] {
				Constant.SET_PROFILE_FULL_DATE_TIME, Constant.SET_PROFILE_END_TIME, },
				null, null, null, null, null);
	}

	public Cursor sortDateMessage() {
		Cursor mCursor = mDb.rawQuery("SELECT * FROM "
				+ Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE + " where "
				+ Constant.MESSAGE_SENDING_DATE
				+ "> datetime('now','localtime') order by "
				+ Constant.MESSAGE_SENDING_DATE + " limit 1", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean deleteTaskData2(int u_id) {
		return mDb.delete(DATABASE_TABLE_PROFILE_CHANGER, Constant.ROWID + "="
				+ u_id, null) > 0;
	}

	public Cursor fetchAllList2() {
		Cursor mCursor = mDb.rawQuery("select * from "
				+ DATABASE_TABLE_PROFILE_CHANGER, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllMessageReminderList() {

		Cursor mCursor = mDb.rawQuery("select " + Constant.ROWID + ","
				+ Constant.PHONENO_FORMESSAGE + " ," + Constant.MESSAGE_STATUS
				+ "," + Constant.MESSAGE_TO_SEND + "," + Constant.MobileUser + ","
				+ Constant.MESSAGE_SENDING_DATE + " from "
				+ Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateMessageTosendStatus(int id, String no, String msg,
			String datetime, String status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(Constant.PHONENO_FORMESSAGE, no);
		initialValues.put(Constant.MESSAGE_TO_SEND, msg);
		initialValues.put(Constant.MESSAGE_SENDING_DATE, datetime);
		initialValues.put(Constant.MESSAGE_STATUS, status);
		return mDb.update(Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE,
				initialValues, Constant.ROWID + "=" + id, null) > 0;
	}

	public boolean deleteSendingMessage(int u_id) {
		return mDb.delete(Constant.DATABASE_PHONENO_LIST_TOMESSAGETABLE,
				Constant.ROWID + "=" + u_id, null) > 0;
	}

	public boolean deleteBlockMessage(int u_id) {
		return mDb.delete(Constant.DATABASE_BLOCKLIST_MESSAGETABLE,
				Constant.ROWID + "=" + u_id, null) > 0;
	}
	public Cursor fetchBlockMessage(String number){


		return  mDb.query(true,Constant.DATABASE_BLOCKLIST_MESSAGETABLE, new String[] {Constant.BLOCKNO_MESSAGE,
				Constant.MESSAGE_TIMESTAMP}, Constant.BLOCK_NO + "='" + number+"'", null,
				null, null, null, null);
	}
	public Cursor fetchBlockCalls(){
		Cursor mCursor = mDb.rawQuery("select " + Constant.ROWID + ","
				+ Constant.CallType + " ," + Constant.MESSAGE_TIMESTAMP
				+ " from "
				+ Constant.DATABASE_BLOCKLIST_CALL_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}
