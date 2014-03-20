package net.nature.client.database;

import org.droidpersistence.annotation.Column;
import org.droidpersistence.annotation.ForeignKey;
import org.droidpersistence.annotation.PrimaryKey;
import org.droidpersistence.annotation.Table;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@Table(name="NOTE")
public class Note {
	
	static public class Type {
		final static public int PHOTO = 0;
		final static public int VIDEO = 1;
		final static public int AUDIO = 2;
		public static final int IDEA = 3;
	};

	@PrimaryKey
	@Column(name="ID")
	private long id = -1;

	@Column(name="COMMENT")
	private String comment = "";
	
	@Column(name="TYPE")
	private int type = 0;

	@Column(name="USERNAME")
	private String username = "";	 

	@Column(name="LANDMARKID")
	private int landmarkId = -1;	

	@Column(name="UPLOADED")
	private boolean uploaded; 

	@Column(name="MODIFIED")
	private boolean modified; 

	@Column(name="CATEGORY")
	private int category = -1;

	@Column(name="LONGITUDE")
	private double longitude = 0;

	@Column(name="LATITUDE")
	private double latitude = 0;

	@Column(name="PATH")
	private String path = "";

	@Column(name="IMGURID")
	private String imgurid = "";

	@Column(name="USER_ID")
	@ForeignKey(tableReference="USER", onDeleteCascade=true, columnReference="ID")
	private long userId;	
	
	@Column(name="CATEGORIES")
	private String categories = "";
	
	@Column(name="ACTIVITIYID")
	private int activityId = -1;
		
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}


	public String toString(){		
		return "{note: id= " + id 
				+ ", type= " + getType()
				+ ", username= " + username
				+ ", userId= " + userId
				+ ", activityId= " + getActivityId()
				+ ", landmarkId= " + landmarkId
				+ ", category= " + category
				+ ", categories= " + categories
				+ ", comment= " + comment
				+ ", longitude= " + longitude
				+ ", latitude= " + latitude
				+ ", uploaded= " + uploaded
				+ ", modified= " + modified
				+ ", imgurId= " + imgurid
				+ ", path= " + path
				+ " }";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getLandmarkId() {
		return landmarkId;
	}

	public void setLandmarkId(int landmarkId) {
		this.landmarkId = landmarkId;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public JSONObject toJson(){
		JSONObject desc = new JSONObject();
		try {
			desc.put("id", id);
			desc.put("lat", latitude);
			desc.put("long", longitude);
			desc.put("landmarkId", landmarkId);
			desc.put("username", username);
			desc.put("category", category);
			desc.put("comment", comment);
			desc.put("categories", categories);
			desc.put("activityId", getActivityId());
		} catch (JSONException e) {
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
		return desc;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getImgurId() {
		return imgurid;
	}

	public void setImgurId(String imgurid) {
		this.imgurid = imgurid;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

}
