package net.nature.client.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.nature.client.GoogleDriveSyncService;
import net.nature.client.Mission;
import net.nature.client.R;
import net.nature.client.R.raw;
import net.nature.client.activities.UserActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;

public class ApplicationData {
	public static User getCurrentUser(Context context){
		SharedPreferences settings = context.getSharedPreferences(UserActivity.PREFS_NAME, 0);
		long userId = settings.getLong("userId", -1);
		if (userId < 0){
			return null;
		}		
		return getUser(context, userId);
	}

	/**
	 * Return the current landmark (Default to landmark -1)
	 * 
	 * @param context
	 * @return
	 */
	public static Landmark getCurrentLandmark(Context context){
		SharedPreferences settings = context.getSharedPreferences(UserActivity.PREFS_NAME, 0);
		// default to landmark -1
		int landmarkId = settings.getInt("landmarkId", -1);
		//String text ="landmarkid="+landmarkId;
		//android.widget.Toast toast = android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_LONG);
		//toast.show();
		
		LandmarkLibrary library = new LandmarkLibrary(context.getResources());
		return library.getLandmark(landmarkId);
	}
	
	public static ParkActivity getCurrentParkActivity(Context context){
		SharedPreferences settings = context.getSharedPreferences(UserActivity.PREFS_NAME, 0);
		// default to activity 1
		int id = settings.getInt("parkActivityId", 1);	
		return getParkActivity(context, id);
	}
	
	public static Landmark getNextLandmark(Context context, int landmarkId){
		landmarkId = landmarkId + 1;
		if (landmarkId > 11){
			landmarkId = 0;
		}
		return getLandmark(context, landmarkId);		
	}
	
	public static Landmark getPreviousLandmark(Context context, int landmarkId){
		landmarkId = landmarkId - 1;
		if (landmarkId < 0){
			landmarkId = 11;
		}
		return getLandmark(context, landmarkId);		
	}


	public static void setCurrentLandmark(Context context, int landmarkId){
		SharedPreferences settings = context.getSharedPreferences("NatureNetPrefs",  0);
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putInt("landmarkId", landmarkId);
		editor.commit();		
	}
	
	public static void setCurrentParkActivity(Context context, int parkActivityId){
		SharedPreferences settings = context.getSharedPreferences("NatureNetPrefs",  0);
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putInt("parkActivityId", parkActivityId);
		editor.commit();
	}

	public static Landmark getLandmark(Context context, int landmarkId) {
		LandmarkLibrary library = new LandmarkLibrary(context.getResources());
		return library.getLandmark(landmarkId);		
	}
	
	public static ParkActivity getParkActivity(Context context, int activityId) {
		ParkActivityLibrary library = new ParkActivityLibrary(context.getResources());
		return library.getParkActivity(activityId);		
	}	


	public static User createNewUser(Context context, String username, String avatarName){
		User newUser = new User();
		newUser.setName(username);
		newUser.setAvatarName(avatarName);
		
		/// Write user in a file in GoogleDrive 
		GoogleDriveSyncService gdss = new GoogleDriveSyncService(context);
		String id = gdss.addUser(newUser);
		
		if (id == null) return null;

		DataManager db = new DataManager(context);
		db.saveUser(newUser);
		db.close();
		return newUser;
	}

	public static User getUser(Context context, long id){
		DataManager db = new DataManager(context);
		User user = db.getUserDao().get(id);
		db.close();
		return user;
	}

	public static User getUserByName(Context context, String name){
		DataManager db = new DataManager(context);
		User user = db.getUserByName(name);
		db.close();
		return user;
	}


	public static void setCurrentUser(Context context, User user) {
		Log.v(ApplicationData.class.getSimpleName(), "Current user is set to : " + user);
		SharedPreferences settings = context.getSharedPreferences("NatureNetPrefs",  0);
		SharedPreferences.Editor editor = settings.edit(); 
		if (user != null){
			editor.putLong("userId", user.getId());
		}else{
			editor.remove("userId");
		}
		editor.commit();
	}

	public static void updateUser(Context context, User user) {
		DataManager db = new DataManager(context);
		db.updateUser(user);
		db.close();		
	}

	public static Mission[] getMissions(Context context){
		Log.v("parse", "parsing getMissions ");
		
		List<Mission> missions = new ArrayList<Mission>();
		InputStream is = context.getResources().openRawResource(R.raw.missions);
		byte[] buffer;
		try {
			buffer = new byte[is.available()];
			while (is.read(buffer) != -1);
			String json = new String(buffer);
			Log.v("parse", "json = " + json);
			parseMissionsJSON(json, missions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return missions.toArray(new Mission[]{});
	}

	static private void parseMissionsJSON(String jsonText, List<Mission> missions){
		
		
		try {
			JSONObject root = new JSONObject(jsonText);
			JSONArray jArray = root.getJSONArray("Missions");
			Log.v("parse", "json objects " + jArray.length());
			for (int i = 0; i < jArray.length(); i++) {
				Log.v("parse", "mission ");
				
				JSONObject json = jArray.getJSONObject(i);				
				String question = json.getString("question");
				String objective = json.getString("objective");

				Mission mission = new Mission();
				mission.setQuestion(question);
				mission.setObjective(objective);
				missions.add(mission);
			}

		}catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static List<User> getAllUsers(Context context) {
		DataManager db = new DataManager(context);
		List<User> ret = db.getUserDao().getAllbyClause(null, null, null, null, "NAME ASC");
		db.close();
		return ret;
	}

	public static List<ParkActivity> getAllActivities(Context context) {
		ParkActivityLibrary library = new ParkActivityLibrary(context.getResources());
		return library.getAll();
	}

	public static List<Landmark> getAllLandmarks(Context context) {
		LandmarkLibrary library = new LandmarkLibrary(context.getResources());
		return library.getLandmarks();
	}
}