package net.nature.client.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.nature.client.R;
import net.nature.client.R.raw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.util.Log;

public class ParkActivityLibrary {

	private List<ParkActivity> parkActivities;
	public ParkActivityLibrary(Resources resources){
		InputStream is = resources.openRawResource(R.raw.activities);
		byte[] buffer;
		try {
			buffer = new byte[is.available()];
			while (is.read(buffer) != -1);
			String json = new String(buffer);
			parseJSON(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	

	/**
	 * Get the landmark associated with the given id.
	 * 
	 * This method returns null if the id is invalid
	 * 
	 * @param id
	 * @return landmark
	 */
	public ParkActivity getParkActivity(int id){		
		for (ParkActivity parkActivity : parkActivities){
			if (parkActivity.getId() == id)
				return parkActivity;
		}
		return null;
	}
	
	public List<ParkActivity> getAll(){
		return parkActivities;
	}

	private void parseJSON(String jsonText){
		try {
			JSONObject root = new JSONObject(jsonText);
	                //byteArrayOutputStream.toString());
	        //JSONObject jObjectResult = jObject.getJSONObject("Categories");
	        JSONArray jArray = root.getJSONArray("Activities");
	        this.parkActivities = new ArrayList<ParkActivity>();
	        
	        for (int i = 0; i < jArray.length(); i++) {
	        	JSONObject json = jArray.getJSONObject(i);
	            int id = json.getInt("id");
	            String name = json.getString("name");
	            
	            String lines = "";
	            JSONArray a = json.getJSONArray("description");
	            for (int j=0 ; j < a.length(); ++j){
	            	String line = a.getString(j);
	            	lines = lines + line;
	            	if (j < a.length()-1){
	            		lines = lines + "\n\n";
	            	}
	            }
	            
	            ParkActivity parkActivity = new ParkActivity();
	            parkActivity.setId(id);
	            parkActivity.setName(name);
	            parkActivity.setDescription(lines);
	            parkActivities.add(parkActivity);
	        }
	        
	        }catch (JSONException e) {
				e.printStackTrace();
			}
		
	}

	
}
