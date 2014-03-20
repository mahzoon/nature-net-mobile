package net.nature.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import net.nature.client.database.ApplicationData;
import net.nature.client.database.DataManager;
import net.nature.client.database.Note;
import net.nature.client.database.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class GoogleDriveSyncService implements SyncNoteService {
	public GoogleDriveSyncService(Context context){
		super();
		this.context = context;
	}
	
	String TAG = getClass().getSimpleName();

	static String get_access_token_api = "https://accounts.google.com//o/oauth2/token";
	
	// https://developers.google.com/drive/v2/reference/files/insert	
	static String upload_api = "https://www.googleapis.com/upload/drive/v2/files";

	static String refresh_token = "1/dJJ0ZW_mZqp-15v8nyCZBWCQHEZzeN6IQW0PZba9tl4";		
	static String client_id = "598685116719-a9iqkbdq967l6k7711g6rqmk37a47l5a.apps.googleusercontent.com";
	static String client_secret = "rZJKp6bJE2gYA9qnMoGxSEvo";
	//
	static String folder_id = "0B9mU-w_CpbztTmZLMm0wUXhsQVk";
	//static String folder_id = "0B9mU-w_CpbztLTVpYi1QQTNJNlk"; // uncc folder
	//static String folder_id = "0B9mU-w_CpbztUUxtaXVIeE9SbWM";	//test folder
	static String users_file_name = "Users.txt";
	static String users_file_desc = "A Complete List of All the Users";
	static String users_file_title = "Users";
	static String ideas_file_name = "Ideas.txt";
	static String ideas_file_desc = "A Complete List of All the Design Ideas";
	static String ideas_file_title = "Ideas";
	
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

	private String getAccessToken() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(get_access_token_api);

		MultipartEntity entity = null;
		entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			entity.addPart("refresh_token", new StringBody(refresh_token));
			entity.addPart("client_id", new StringBody(client_id));
			entity.addPart("client_secret", new StringBody(client_secret));
			entity.addPart("grant_type", new StringBody("refresh_token"));

			httpPost.setEntity(entity);
			//		httpPost.setHeader("Authorization", "Client-ID " + client_id);

			HttpResponse response = httpClient.execute(httpPost,
					localContext);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							response.getEntity().getContent(), "UTF-8"));

			String sResponse = "";
			String line;
			while ((line = reader.readLine()) != null) {
				sResponse = sResponse + line;
			}

			Log.v("Drive","response:" + sResponse);

			JSONObject JResponse;
			JResponse = new JSONObject(sResponse);
			String accessToken = JResponse.getString("access_token");
			return accessToken;
		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage(), e);
		}
		return null;
	}	

	public String upload(Note note){
		Log.v(TAG, "Uploading a new note to Google Drive: note = " + note);

		if (!isOnline()){
			Log.v(TAG, "Failed -> Not online");
			return null;
		}

		String	accessToken = getAccessToken();
		if (accessToken == null){
			Log.e(TAG, "Failed -> Unable to obtain an Access Token from Google");
			return null;
		}else {
			Log.v(TAG, "Obtained an access token from Google: " + accessToken);
		}

		GoogleCredential credential = new GoogleCredential();
		credential.setAccessToken(accessToken);
		credential.setExpiresInSeconds(3600L);

		Drive service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.setApplicationName("naturenetmobile")
		.build();


		try {
			// File's binary content
			java.io.File fileContent = new java.io.File(note.getPath());

			String mimeType = "";
			if (note.getType() == Note.Type.VIDEO){
				mimeType = "video/3gpp";
			}else if (note.getType() == Note.Type.IDEA){
				mimeType = "image/png";
			}else if (note.getType() == Note.Type.PHOTO){
				mimeType = "image/jpeg";
			}

			FileContent mediaContent = new FileContent(mimeType, fileContent);

			// File's metadata.
			File body = new File();
			body.setTitle(fileContent.getName());
			body.setMimeType(mimeType);
			body.setDescription(getDescription(note));

			File file = service.files().insert(body, mediaContent).execute();
			if (file != null) {	        	 
				String fileId = file.getId();				
				Log.v(TAG, "Note uploaded: " + file.getTitle() + ", id = " + file.getId());

				ParentReference newParent = new ParentReference();
				newParent.setId(folder_id);
				ParentReference parent = service.parents().insert(fileId, newParent).execute();
				if (parent != null){
					Log.v(TAG, "Note inserted into folder " + parent.getId());
					return file.getId();
				}					
			}

		} catch (UserRecoverableAuthIOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}		
		return null;
	}

	public Context getContext() {
		return context;
	}
	
	private String getDescription(Note note){
		User user = ApplicationData.getUser(context, note.getUserId());		
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 		
		JSONObject json = note.toJson();		
		try {
			json.put("androidId", android_id);
			json.put("avatarName", user.getAvatarName());
		} catch (JSONException e) {
		}
		return json.toString();		
	}

	@Override
	public boolean update(Note note) {
		if (!isOnline()){
			Log.v(TAG, "Failed -> Not online");
			return false;
		}

		String	accessToken = getAccessToken();
		if (accessToken == null){
			Log.e(TAG, "Failed -> Unable to obtain an Access Token from Google");
			return false;
		}else {
			Log.v(TAG, "Obtained an Access Token from Google: " + accessToken);
		}

		GoogleCredential credential = new GoogleCredential();
		credential.setAccessToken(accessToken);
		credential.setExpiresInSeconds(3600L);

		Drive service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.setApplicationName("naturenetmobile")
		.build();


		try {

			// File's metadata.
			File file = new File();
			file.setDescription(getDescription(note));			

			String fileId = note.getImgurId();
			Files.Patch patchRequest = service.files().patch(fileId, file);
			patchRequest.setFields("description");

			File updatedFile = patchRequest.execute();		      
			if (updatedFile != null) {	        	 
				Log.v(TAG, "Note updated!");
				return true;
			}

		} catch (UserRecoverableAuthIOException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}		
		return false;	
	}	

	private Drive getService()
	{
		if (!isOnline()){
			Log.v(TAG, "Failed -> Not online");
			return null;
		}

		String	accessToken = getAccessToken();
		if (accessToken == null){
			Log.e(TAG, "Failed -> Unable to obtain an Access Token from Google");
			return null;
		}else {
			Log.v(TAG, "Obtained an access token from Google: " + accessToken);
		}

		com.google.api.client.googleapis.auth.oauth2.GoogleCredential credential = 
				new com.google.api.client.googleapis.auth.oauth2.GoogleCredential();
		credential.setAccessToken(accessToken);
		credential.setExpiresInSeconds(3600L);

		Drive service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.setApplicationName("naturenetmobile")
		.build();

		return service;
	}
	
	private String createIdeasFile(Drive service)
	{
		File body = new File();
		body.setTitle(ideas_file_title);
		body.setDescription(ideas_file_desc);
		body.setMimeType("text/plain");
		try 
		{
			FileOutputStream fos = context.openFileOutput(ideas_file_name, Context.MODE_PRIVATE);
			fos.close();
			java.io.File fileContent = context.getFileStreamPath(ideas_file_name);
			FileContent mediaContent = new FileContent(body.getMimeType(), fileContent);
			File file = service.files().insert(body, mediaContent).execute();
			return file.getId();
		} catch (IOException e)
		{
			System.out.println("createIdeasFile error: " + e);
			return null;
		}
	}
	
	private String getUsersFileId(Drive service)
	{
		try
		{
			Files.List request = service.files().list().setQ("title = '" + users_file_title + "'");
			FileList files = request.execute();
			if (files.getItems().size()>0)
				return files.getItems().get(0).getId();
			return null;
		} catch (Exception e)
		{
			System.out.println("getUserFileId error: " + e);
			return null;
		}
	}
	
	private String getIdeasFileId(Drive service)
	{
		try
		{
			Files.List request = service.files().list().setQ("title = '" + ideas_file_title + "'");
			FileList files = request.execute();
			if (files.getItems().size()>0)
				return files.getItems().get(0).getId();
			return null;
		} catch (Exception e)
		{
			System.out.println("getIdeasFileId error: " + e);
			return null;
		}
	}
	
	public String addUser(User user)
	{
		Drive service = getService();
		if (service == null)
		{
			String text = "Cannot establish connection to the data center";
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.show();		
			return null;
		}
		
		String id = getUsersFileId(service);
		
		if (id == null)
			return null;
			//id = createUserFile(service);
		
		if (findUser(user.getName())!=null)
		{
			//message that this username cannot be taken
			Toast toast = Toast.makeText(context, "This username cannot be taken, try another one.", Toast.LENGTH_LONG);
			toast.show();
			return null;
		}
		
		try
		{
			File file = service.files().get(id).execute();
			com.google.api.client.http.HttpResponse resp = 
					service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			BufferedReader bR = new BufferedReader(new InputStreamReader(resp.getContent()));

			FileOutputStream fos = context.openFileOutput("u2.txt", Context.MODE_PRIVATE);
			fos.close();
			java.io.File f_new = context.getFileStreamPath("u2.txt");
			BufferedWriter bW = new BufferedWriter(new FileWriter(f_new));
			
			String line;
			while ((line = bR.readLine()) != null) {
			   bW.write(line);bW.newLine();
			}
			
			bW.write(user.toString());
            bW.newLine(); bW.flush(); bW.close();
			bR.close();
			
			FileContent mediaContent = new FileContent(file.getMimeType(), f_new);
			File updatedFile = service.files().update(id, file, mediaContent).execute();
			return updatedFile.getId();
		} catch (IOException e)
		{
			System.out.println("addUser error: " + e);
			return null;
		}
	}
	
	public User findUser(String username)
	{
		Drive service = getService();
		String id = getUsersFileId(service);
		if (id == null)
			return null;
		try
		{
			File file = service.files().get(id).execute();
			com.google.api.client.http.HttpResponse resp = 
					service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			BufferedReader bR = new BufferedReader(new InputStreamReader(resp.getContent()));
			
			String line;
			while ((line = bR.readLine()) != null) {
				User u = getUserData(line);
				if (username.equalsIgnoreCase(u.getName()))
					return u;
			}
			bR.close();
			return null;
			
		} catch (Exception e)
		{
			System.out.println("findUser error: " + e);
			return null;
		}
	}
	
	public void updateUserTable()
	{
		//remove all instances from user table
		DataManager db = new DataManager(context);
		
		Drive service = getService();
		if (service == null)
		{
			String text = "Cannot establish connection to the data center";
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.show();		
			return;
		}
		
		String id = getUsersFileId(service);
		if (id == null)
			return;
		
		db.emptyUserTable();
		try
		{
			File file = service.files().get(id).execute();
			com.google.api.client.http.HttpResponse resp = 
					service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			BufferedReader bR = new BufferedReader(new InputStreamReader(resp.getContent()));
			
			String line;
			while ((line = bR.readLine()) != null) {
			   User u = getUserData(line);
			   db.saveUser(u);
			}
			bR.close();
			db.close();
			return;
		} catch (Exception e)
		{
			System.out.println("findUser error: " + e);
			return;
		}
	}
	
	private User getUserData(String line)
	{
		//JSONObject j=new JSONObject(line);
		User u = new User();
		//u.setName(j.getString("name"));
		//u.setAvatarName(j.getString("avatarName"));
		//u.setId(j.getLong("id"));
		line = line.replace('=', ',');
		String[] ss = line.split(",");
		for(int counter=0;counter<ss.length;counter++)
			ss[counter]=ss[counter].trim();
		u.setId(Long.valueOf(ss[1]));
		u.setName(ss[3]);
		String avatar_name =ss[5].substring(0, ss[5].length()-1).toLowerCase();
		if (avatar_name.contains("."))
			avatar_name = avatar_name.substring(0, avatar_name.length()-4);
		u.setAvatarName(avatar_name);
		return u;
	}
	
	public void EmptyUserFile()
	{
		Drive service = getService();
		String id = getUsersFileId(service);
		
		//if (id == null){id = createUserFile(service);return;}
		if (id == null) return;
		
		File body = new File();
		body.setTitle(users_file_title);
		body.setDescription(users_file_desc);
		body.setMimeType("text/plain");
		try 
		{
			File file = service.files().get(id).execute();
			FileOutputStream fos = context.openFileOutput(users_file_name, Context.MODE_PRIVATE);
			fos.close();
			java.io.File fileContent = context.getFileStreamPath(users_file_name);
			FileContent mediaContent = new FileContent(body.getMimeType(), fileContent);
			File updatedFile = service.files().update(id, file, mediaContent).execute();
		} catch (IOException e)
		{
			System.out.println("createUserFile error: " + e);
			return;
		}
	}
	
	public String addIdea(String idea, User user)
	{
		Drive service = getService();
		if (service == null)
		{
			String text = "Cannot establish connection to the data center";
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.show();		
			return null;
		}
		
		String id = getIdeasFileId(service);
		
		if (id == null)
			return null;
			//id = createIdeasFile(service);
		
		try
		{
			File file = service.files().get(id).execute();
			com.google.api.client.http.HttpResponse resp = 
					service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			BufferedReader bR = new BufferedReader(new InputStreamReader(resp.getContent()));

			FileOutputStream fos = context.openFileOutput("u2.txt", Context.MODE_PRIVATE);
			fos.close();
			java.io.File f_new = context.getFileStreamPath("u2.txt");
			BufferedWriter bW = new BufferedWriter(new FileWriter(f_new));
			
			String line;
			while ((line = bR.readLine()) != null) {
			   bW.write(line);bW.newLine();
			}
			
			bW.write("{idea: username= "+user.getName()+", avatarName= "+user.getAvatarName()+", idea= "+idea+"}");
            bW.newLine(); bW.flush(); bW.close();
			bR.close();
			
			FileContent mediaContent = new FileContent(file.getMimeType(), f_new);
			File updatedFile = service.files().update(id, file, mediaContent).execute();
			return updatedFile.getId();
		} catch (IOException e)
		{
			System.out.println("addIdea error: " + e);
			return null;
		}
	}
}
