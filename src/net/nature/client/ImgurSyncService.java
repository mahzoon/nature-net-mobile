package net.nature.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.nature.client.database.ApplicationData;
import net.nature.client.database.Note;
import net.nature.client.database.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;

public class ImgurSyncService implements SyncNoteService {
	public ImgurSyncService(Context context) {
		super();
		this.context = context;
	}
	
	
	static String get_access_token_api = "https://api.imgur.com/oauth2/token";
	static String image_upload_api = "https://api.imgur.com/3/image";
	static String image_update_api = "https://api.imgur.com/3/image";

	static String refresh_token = "6a787787da043c4dcf9639a53f7f148f67f29334";		
	static String client_id = "c2ac569e09963d7";
	static String client_secret = "2539e70cec12f0b9c4ef3063b0b6868b29dda090";
	static String album_id = "MRrkk";
	
	final private Context context;

	public boolean isOnline(){
	    ConnectivityManager cm =
		        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;

	}
	
	private String callImgurAPI_GetAccessToken() throws IllegalStateException, IOException, JSONException{
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(get_access_token_api);

		MultipartEntity entity = null;
		entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("refresh_token", new StringBody(refresh_token));
		entity.addPart("client_id", new StringBody(client_id));
		entity.addPart("client_secret", new StringBody(client_secret));
		entity.addPart("grant_type", new StringBody("refresh_token"));
		
		httpPost.setEntity(entity);
		httpPost.setHeader("Authorization", "Client-ID " + client_id);

		HttpResponse response = httpClient.execute(httpPost,
				localContext);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						response.getEntity().getContent(), "UTF-8"));

		String sResponse = reader.readLine();
		JSONObject JResponse = new JSONObject(sResponse);
		String accessToken = JResponse.getString("access_token");	
		return accessToken;
	}	
	
	
	public boolean update(Note note){
		Log.v(getClass().getSimpleName(), "Updating note on Imgur: note = " + note);
		
		if (!isOnline()){
			Log.v(getClass().getSimpleName(), "Failed -> Not online");
			return false;
		}
		
		try {
			String	accessToken = callImgurAPI_GetAccessToken();
			if (accessToken == null){
				Log.e(getClass().getSimpleName(), "Failed -> Unable to obtain an Access Token from imgur");
				return false;
			}else {
				Log.v(getClass().getSimpleName(), "Obtained an Access Token from imgur: " + accessToken);
			}
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(image_update_api + "/" + note.getImgurId());

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);			
			entity.addPart("description", new StringBody(genereatePhotoDescription(note)));
			httpPost.setEntity(entity);
			httpPost.setHeader("Authorization", "Bearer " + accessToken);

			HttpResponse response = httpClient.execute(httpPost,
					localContext);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							response.getEntity().getContent(), "UTF-8"));

			String sResponse = reader.readLine();
			Log.v(getClass().getSimpleName(), "response from Imgur = " + sResponse);
			if (sResponse != null) {
				JSONObject JResponse = new JSONObject(sResponse);
				boolean  success = JResponse.getBoolean("success");				
				if (success) {
					Log.v(getClass().getName(), "update is successful");
					return true;					
				}
			}
		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
		
		return false;
	}
	
	public String upload(Note note){
		Log.v(getClass().getSimpleName(), "Uploading a new note: note = " + note);
		
		if (!isOnline()){
			Log.v(getClass().getSimpleName(), "Failed -> Not online");
			return null;
		}
		
		try {
			String	accessToken = callImgurAPI_GetAccessToken();
			if (accessToken == null){
				Log.e(getClass().getSimpleName(), "Failed -> Unable to obtain an Access Token from imgur");
				return null;
			}else {
				Log.v(getClass().getSimpleName(), "Obtained an access token from Imgur: " + accessToken);
			}
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(image_upload_api);

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
						
			Bitmap bitmap = decodeImageFile(note.getPath());
			if (bitmap == null){
				Log.v(getClass().getSimpleName(), note.getPath());
			}
			bitmap.compress(CompressFormat.JPEG, 100, bos);			
			byte[] data = bos.toByteArray();
			entity.addPart("image", new ByteArrayBody(data, (new File(note.getPath())).getName()));	
			entity.addPart("title", new StringBody(note.getComment()));
			entity.addPart("album", new StringBody("MRrkk"));
			entity.addPart("description", new StringBody(genereatePhotoDescription(note)));
			httpPost.setEntity(entity);
			httpPost.setHeader("Authorization", "Bearer " + accessToken);

			HttpResponse response = httpClient.execute(httpPost,
					localContext);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							response.getEntity().getContent(), "UTF-8"));

			String sResponse = reader.readLine();
			Log.v(getClass().getSimpleName(), "Imgur response: " + sResponse);
			if (sResponse != null) {
				JSONObject JResponse = new JSONObject(sResponse);
				boolean  success = JResponse.getBoolean("success");				
				if (success) {
					JSONObject imgurData = JResponse.getJSONObject("data");
					String imgurId = imgurData.getString("id");
					Log.v(getClass().getSimpleName(), "Upload to Imgur is successful");
					return imgurId;
				}
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage(), e);
		}
		
		return null;
	}

	public Context getContext() {
		return context;
	}

	String genereatePhotoDescription(Note note){
		User user = ApplicationData.getUser(context, note.getUserId());		
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 		
		JSONObject json = note.toJson();		
		try {
			json.put("androidId", android_id);
			json.put("avatarName", user.getAvatarName());
		} catch (JSONException e) {
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
		return json.toString();
	}
	
	public Bitmap decodeImageFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeFile(filePath, o2);
	}	

}
