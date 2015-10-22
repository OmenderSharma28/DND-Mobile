package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Model implements Parcelable {
private static final String TAG="MODEL";
	private String name;
	private String phoneno,readStatus,messageBody,date,type;
	private String Id,PhotoId;
	public String getPhotoId() {
		return PhotoId;
	}
	public void setPhotoId(String photoId) {
		PhotoId = photoId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReadStatus() {
		return readStatus;
	}
	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	private boolean selected;

	public Model(String name) {
		this.name = name;
		selected = false;
	}
	public Model(Parcel in) {
		
		this.name=in.readString();
		this.phoneno=in.readString();
	}		

	public Model(){
		
	}

	public String getName() {
		return name;
	}
	
public void setName(String name) {
		this.name = name;
	}
	public String getId(){
		return Id;
	}
	public void setId(String id){
		this.Id=id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public static final Parcelable.Creator<Model> CREATOR
    = new Parcelable.Creator<Model>() 
   {
         public Model createFromParcel(Parcel in) 
         {
            Log.d (TAG, "createFromParcel()");
             return new Model(in);
         }

         public Model[] newArray (int size) 
         {
            Log.d (TAG, "createFromParcel() newArray ");
             return new Model[size];
         }
    };

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
		dest.writeString(name);
		dest.writeString(phoneno);
		
	}


}

