package net.nature.client.database;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.nature.client.AlbumStorageDirFactory;
import net.nature.client.FroyoAlbumDirFactory;
import net.nature.client.GoogleDriveSyncService;
import net.nature.client.ImgurSyncService;
import net.nature.client.SyncNoteService;
import net.nature.client.activities.UserActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class Notebook {

	private AlbumStorageDirFactory mAlbumStorageDirFactory;
	private DataManager mDataManager;
	private SyncNoteService mImgurService;
	private SyncNoteService mGoogleDriveService;
	private Context mContext;
	
	public Notebook(Context context){
		mDataManager = new DataManager(context);
		mAlbumStorageDirFactory = new FroyoAlbumDirFactory();

		mImgurService = new ImgurSyncService(context);
		mGoogleDriveService = new GoogleDriveSyncService(context);
		
		mContext = context;
	}
	
	public Note getNote(String photoPath){
		long id = getNoteIdFromPath(photoPath);
		Note dbNote = getNote(id);
//		if (dbNote != null){
//			return dbNote;
//		}else{			
//			Note newNote = createNewNoteFromFile(photoPath);
//			return newNote;
//		}
		return dbNote;
	}
	
	public Note getNote(long id){
		return mDataManager.getNote(id);
	}
	
	public Note createNewNodeFromFile(String filePath, int type){
		long id = getNoteIdFromPath(filePath);
		
		SharedPreferences settings = mContext.getSharedPreferences(UserActivity.PREFS_NAME, 0);
//		String username = settings.getString("username", "");
//		String avatarName = settings.getString("avatarName", "");
		double longitude = Double.valueOf(settings.getString("long","0"));
		double latitude = Double.valueOf(settings.getString("lat","0"));		
		int landmarkId = -1;//settings.getInt("landmarkId", 0);
		int activityId = settings.getInt("parkActivityId", -1);
		
		User user = ApplicationData.getCurrentUser(mContext);
		
		Note newNote = new Note();
		newNote.setId(id);
		newNote.setUserId(user.getId());
		newNote.setUsername(user.getName());		
		newNote.setType(type);
		newNote.setPath(filePath);
		newNote.setLongitude(longitude);
		newNote.setLatitude(latitude);
		newNote.setLandmarkId(landmarkId);
		newNote.setActivityId(activityId);
		mDataManager.saveNote(newNote);
		return newNote;
	}
	
	public Note createNewNoteFromFile(String filePath){
		int type = getNoteTypeFromPath(filePath);
		return createNewNodeFromFile(filePath, type);
	}
	
	private String getAlbumName() {
		return "NatureNet";
	}
	
	public long getNoteIdFromPath(String path){
		String name = (new File(path)).getName();
		String[] parts = name.split("\\.");
		String idString = parts[0];
		return Long.parseLong(idString);
	}
	
	public int getNoteTypeFromPath(String path){
		String name = (new File(path)).getName();
		String[] parts = name.split("\\.");
		String ext = parts[1];
		if (ext.compareToIgnoreCase("jpg") == 0){
			return Note.Type.PHOTO;
		}else if (ext.compareToIgnoreCase("3gp") == 0){
			return Note.Type.VIDEO;
		}
		return -1;
	}
	
	private long generateId(){
		long id = (new Date()).getTime();
		return id;
	}
	
	public File createNewVideoNoteFile() {
		long id = generateId();
		return new File(getAlbumDir(), "" + id + ".3gp");
	}

	
	public File createNewPhotoNoteFile() {
		long id = generateId();
		return new File(getAlbumDir(), "" + id + ".jpg");
	}
	
	public File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("NatureNetAlbum", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v("NatureNetAlbum", "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}

	
	// refresh the database
	// remove notes whose photos have been deleted from the file system
	private void refresh(){
		List<Note> allNotes 
		= mDataManager.getNoteDao().getAll();		
		for (Note note : allNotes){			
			File file = new File(note.getPath());
			if(!file.exists()){				
				Log.v(getClass().getSimpleName(), "Deleted a note because its media file no longer exists: " + note.getPath());
				mDataManager.getNoteDao().delete(note.getId());
			}
		}
	}
	
	public void sync(){
		refresh();
		
		
		List<Note> unuploadedNotes 
		= mDataManager.getNoteDao().getAllbyClause("UPLOADED = ?", new String[]{"0"}, "","","");
		List<Note> modifiedNotes 
		= mDataManager.getNoteDao().getAllbyClause("MODIFIED = ? AND UPLOADED = ?", new String[]{"1","1"}, "","","");
		
		Log.v(getClass().getSimpleName(), "Sync status: unuploaded: " + unuploadedNotes.size() + ", modified: " + modifiedNotes.size());
		
		for (Note note : unuploadedNotes){
			
			String syncId = null;
			
			if (note.getType() == Note.Type.PHOTO){
				syncId = mGoogleDriveService.upload(note);
			}else if (note.getType() == Note.Type.VIDEO){
				syncId = mGoogleDriveService.upload(note);
			}else if (note.getType() == Note.Type.IDEA){
				syncId = mGoogleDriveService.upload(note);
			}
						
			if (syncId != null){
				note.setImgurId(syncId);
				note.setUploaded(true);
				note.setModified(false);
				updateNote(note);
			}
		}

		for (Note note : modifiedNotes){
			if (note.getType() == Note.Type.PHOTO){
				if (mGoogleDriveService.update(note)){
					note.setModified(false);
					updateNote(note);
				}
			}else if (note.getType() == Note.Type.VIDEO){
				if (mGoogleDriveService.update(note)){
					note.setModified(false);
					updateNote(note);
				}
			}
		}		
	}

	public void updateNote(Note pnote) {
		mDataManager.updateNote(pnote);		
	}

	public List<Note> getAllNotes() {
		return mDataManager.getNoteDao().getAllbyClause(null, null, null, null, "ID DESC");
	}

	public void close() {
		mDataManager.getDatabase().close();		
	}

	public void saveNote(Note note) {
		mDataManager.saveNote(note);		
	}

	public void deleteNote(long id) {
		mDataManager.deleteNote(id);
	}


}
