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

public class LandmarkLibrary {

	private List<Landmark> landmarks;
	public LandmarkLibrary(Resources resources){
		InputStream is = resources.openRawResource(R.raw.landmarks);
		byte[] buffer;
		try {
			buffer = new byte[is.available()];
			while (is.read(buffer) != -1);
			String json = new String(buffer);
			parseJSON(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
//		landmarks = new Landmark[]{
//				new Landmark(new Rect(630, 1890, 720, 1950), 1, "Hub of Activities"),
//				new Landmark(new Rect(732, 1831, 780, 1876), 2, "Golden Eagle"),
//				new Landmark(new Rect(380, 1410, 486, 1510), 3, "A Safe Haven"),
//				new Landmark(new Rect(500, 1110, 600, 1205), 4, "Beavers"),
//				new Landmark(new Rect(800, 1100, 900, 1200), 5, "Outdoor Classroom"),
//				new Landmark(new Rect(980, 890, 1080, 980), 6, "Past to Present"),
//				new Landmark(new Rect(1130, 790, 1185, 895), 7, "Overlook"),
//				new Landmark(new Rect(1245, 930, 1345, 1035), 8, "Bird Hollow"),
//				new Landmark(new Rect(1235, 1071, 1335, 1182), 9, "Where Rivers Come Together"),
//				new Landmark(new Rect(900, 1690, 985, 1755), 10, "Birds of Prey"),
//				new Landmark(new Rect(945, 1805, 1041, 1920), 11, "Journey's End")
//		};
//
//		

//		try {
//			System.out.println("json= " + toJSON().toString(4));
//			Log.v(getClass().getName(), "json= " + toJSON().toString(4));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
		//landmarks =
		//		landmarks = new ArrayList<Landmark>();
		//		
		//		
		//		try {  
		//			Resources res = context.getResources();  
		//			XmlResourceParser xrp = res.getXml(R.xml.landmarks);  
		//			xrp.next();  
		//			int eventType = xrp.getEventType();  
		//			while (eventType != XmlPullParser.END_DOCUMENT) {  
		//				if (eventType == XmlPullParser.START_DOCUMENT) {  
		//					//  
		//					// Code To handle string of your xml document  
		//					//  
		//					//System.out.pritnln(xrp.getName());  
		//				} else if (eventType == XmlPullParser.START_TAG) {  
		//					//  
		//					// Code To handle start tags  
		//					//
		//					if (xrp.getName)
		//					Landmark newLandmark = new Landmark();
		//					
		//					//			     System.out.pritnln(xrp.getName());  
		//				} else if (eventType == XmlPullParser.END_TAG) {  
		//					//  
		//					// Code To handle End tags  
		//					//  
		//					//			     System.out.pritnln(xrp.getName());  
		//				} else if (eventType == XmlPullParser.TEXT) {  
		//					//  
		//					// Code To handle texts  
		//					//  
		//					//			     System.out.pritnln(xrp.getText());  
		//				}  
		//				eventType = xrp.next();  
		//			}  
		//
		//			return listSampleClass;
		//
		//		} catch (Exception ex) {  
		//			//			   System.out.println(" Exaception Occured : - " + ex.toString());  
		//			ex.printStackTrace();  
		//			//			   return null;  
		//		}	
	}

	/**
	 * Get the landmark whose bounds contains the given x-y coordinate.
	 * The coordinate system is relative to the drawable area of the 
	 * resolution 1806 x 2278.
	 * 
	 * This method returns false if no such landmark exists.
	 * 
	 * @param x	
	 * @param y
	 * @return landmark
	 */
	public Landmark getLandmarkAt(int x, int y){
		for (Landmark landmark : landmarks){								
			if (landmark.getRect().contains(x,y)){
				return landmark;
			}																
		}
		return null;
	}

	/**
	 * Get the landmark associated with the given id.
	 * 
	 * This method returns null if the id is invalid
	 * 
	 * @param id
	 * @return landmark
	 */
	public Landmark getLandmark(int id){		
		for (Landmark landmark : landmarks){
			if (landmark.getId() == id)
				return landmark;
		}
		return null;
	}

	private void parseJSON(String jsonText){
		try {
			JSONObject root = new JSONObject(jsonText);
	                //byteArrayOutputStream.toString());
	        //JSONObject jObjectResult = jObject.getJSONObject("Categories");
	        JSONArray jArray = root.getJSONArray("Landmarks");
	        this.landmarks = new ArrayList<Landmark>();
	        
	        for (int i = 0; i < jArray.length(); i++) {
	        	JSONObject json = jArray.getJSONObject(i);
	            int id = json.getInt("id");
	            int d = 75;
	            String name = json.getString("name");
	            int left = json.getInt("left") - d;
	            int right = json.getInt("left") + d;
	            int bottom = json.getInt("top") + d;
	            int top = json.getInt("top") - d;
	            
	            String lines = "";
	            JSONArray a = json.getJSONArray("description");
	            for (int j=0 ; j < a.length(); ++j){
	            	String line = a.getString(j);
	            	lines = lines + line;
	            	if (j < a.length()-1){
	            		lines = lines + "\n\n";
	            	}
	            }
	            
	            a = json.getJSONArray("targets");
	            String[] targets = new String[a.length()];
	            for (int j=0 ; j < a.length(); ++j){
	            	targets[j] = a.getString(j);
	            }
	            
	            
	            Landmark landmark = new Landmark();
	            landmark.setId(id);
	            landmark.setName(name);
	            landmark.setRect(new Rect(left,top,right,bottom));
	            landmark.setDescription(lines);
	            landmark.setTargets(targets);
	            landmarks.add(landmark);
	        }
	        
	        }catch (JSONException e) {
				e.printStackTrace();
			}
		
	}

	public JSONObject toJSON(){
		try {
			JSONArray landmarksJSON = new JSONArray();
			for (Landmark landmark : landmarks){
				JSONObject json = new JSONObject();
				json.put("name", landmark.getName());
				json.put("id",  landmark.getId());
				json.put("left",  landmark.getRect().left);
				json.put("right",  landmark.getRect().right);
				json.put("top",  landmark.getRect().top);
				json.put("bottom",  landmark.getRect().bottom);
				landmarksJSON.put(json);
			}
						
			JSONObject ret = new JSONObject();
			ret.put("Landmarks", landmarksJSON);
			return ret;			
		}catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Landmark> getLandmarks() {
		return landmarks;
	}		

	
}
