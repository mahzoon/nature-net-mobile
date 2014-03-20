package net.nature.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager {


	private Context context;
	private SQLiteDatabase database;
	private NoteDao noteDao;
	private UserDao userDao;
	//private PlatformDao platformDao;

	public DataManager(Context context){
		setContext(context);
		SQLiteOpenHelper openHelper = new OpenHelper(context, "NATURENETDATABASE", null, 8);
		setDatabase(openHelper.getWritableDatabase());

		//            this.platformDao = new PlatformDao(new PlatformTableDefinition(), database);
		this.setNoteDao(new NoteDao(new NoteTableDefinition(), getDatabase()));
		this.setUserDao(new UserDao(new UserTableDefinition(), getDatabase()));
	}

	public void close(){
		getDatabase().close();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	
	
	public long saveNote(Note note){
		long result = 0;     
		try {
			getDatabase().beginTransaction();
			result = getNoteDao().save(note);
			getDatabase().setTransactionSuccessful();
			
			Log.d(getClass().getSimpleName(), "database saved : " + note);			
		} catch (Exception e) {
			e.printStackTrace();
		}               
		getDatabase().endTransaction();
		return result;
	}
	
	public long saveUser(User user){
		long result = 0;     
		try {
			getDatabase().beginTransaction();
			result = getUserDao().save(user);
			getDatabase().setTransactionSuccessful();
			
			user.setId(result);
			getDatabase().endTransaction();
			Log.d(getClass().getSimpleName(), "Save: " + user);		

		} catch (Exception e) {
			e.printStackTrace();
		}               
		return result;
	}

	public void deleteNote(long id) {
		Log.d(getClass().getSimpleName(), "Delete:" + id);
		try {
			getDatabase().beginTransaction();
			getNoteDao().delete(id);
			getDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}               
		getDatabase().endTransaction();
	}	

	public void updateNote(Note note){
		Log.d("DataManager", "update note:" + note);
		try {
			getDatabase().beginTransaction();
			getNoteDao().update(note, note.getId());
			getDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}               
		getDatabase().endTransaction();
	}
	
	public User getUserByName(String name){
		return getUserDao().getByClause(" NAME ='"+name+"'", new String[]{"1"});
	}
	
	public void updateUser(User user) {
		Log.d("DataManager", "database updated:" + user);
		try {
			getDatabase().beginTransaction();
			getUserDao().update(user, user.getId());
			getDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}               
		getDatabase().endTransaction();		
	}


	public int getNumPhotos(){
		return getNoteDao().getAll().size();
	}

	public Note getNote(Long id){           
		return getNoteDao().get(id);
	}

	public NoteDao getNoteDao() {
		return noteDao;
	}

	public void setNoteDao(NoteDao noteDao) {
		this.noteDao = noteDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void emptyUserTable()
	{
		try {
			getDatabase().beginTransaction();
			getUserDao().delete();
			getDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}               
		getDatabase().endTransaction();	
	}
}

class OpenHelper extends SQLiteOpenHelper {



	public OpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			NoteTableDefinition noteSTable = new NoteTableDefinition();
			noteSTable.onCreate(db);

			UserTableDefinition userTable = new UserTableDefinition();
			userTable.onCreate(db);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			NoteTableDefinition noteSTable = new NoteTableDefinition();
			noteSTable.onUpgrade(db, oldVersion, newVersion);

			UserTableDefinition userTable = new UserTableDefinition();
			userTable.onUpgrade(db, oldVersion, newVersion);

		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

