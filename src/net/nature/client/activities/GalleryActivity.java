package net.nature.client.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.nature.client.R;
import net.nature.client.R.id;
import net.nature.client.R.layout;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryActivity extends FragmentActivity {
	private static final int NOTE = 0;
	private List<DataHolder> list;
	private ImageAdapter imageAdapter;
	private Notebook mNotebook;

	/** Called when the activity is first created. */
	
	class DataHolder {
		Note note;
		Bitmap thumbnail;
	}
	
	
	private void loadPhotos(){
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID + " desc";

		// http://stackoverflow.com/questions/5039779/displaying-images-from-a-specific-folder-on-the-sdcard-using-a-gridview
		Cursor imagecursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				columns, 
				MediaStore.Images.Media.DATA + " like ? ",
				new String[] {"%NatureNet%"},
				orderBy);
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
		int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
		
		// http://developer.android.com/reference/android/database/Cursor.html#moveToFirst()
		if (!imagecursor.moveToFirst())
			return;
		
		do {			
			
			String path = imagecursor.getString(dataColumnIndex);	
			Note note = mNotebook.getNote(path);
			if (note == null)
				continue;
			
			int id = imagecursor.getInt(image_column_index);
			Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND, null);

			DataHolder dh = new DataHolder();
			dh.note = note;
			dh.thumbnail = thumbnail;
			list.add(dh);
			
		}while (imagecursor.moveToNext());
		
		imagecursor.close();
	}	
	
	private void loadVideos(){
		final String[] columns = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID };
		final String orderBy = MediaStore.Video.Media._ID + " desc";

		// http://stackoverflow.com/questions/5039779/displaying-images-from-a-specific-folder-on-the-sdcard-using-a-gridview
		Cursor imagecursor = getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				columns, 
				MediaStore.Video.Media.DATA + " like ? ",
				new String[] {"%NatureNet%"},
				orderBy);
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Video.Media._ID);
		int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DATA);
				
		// http://developer.android.com/reference/android/database/Cursor.html#moveToFirst()
		if (!imagecursor.moveToFirst())
			return;
		
		do {			
			
			String path = imagecursor.getString(dataColumnIndex);			
			int id = imagecursor.getInt(image_column_index);
			Note note = mNotebook.getNote(path);
			if (note == null)
				continue;
			
			Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Video.Thumbnails.MINI_KIND, null);
			
			DataHolder dh = new DataHolder();
			dh.note = note;
			dh.thumbnail = thumbnail;
			list.add(dh);
			
		}while (imagecursor.moveToNext());
		
		imagecursor.close();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		mNotebook = new Notebook(getBaseContext());
		list = new ArrayList<DataHolder>();
		loadPhotos();
		loadVideos();
		
		// sort them in descending id
		Collections.sort(list, new Comparator<DataHolder>(){
			@Override
			public int compare(DataHolder d1, DataHolder d2) {
				// TODO Auto-generated method stub
				return (Long.valueOf(d2.note.getId())).compareTo(Long.valueOf(d1.note.getId()));
			}			
		});
		
		if (list.size() >= 21){
			list = list.subList(0, 20);			
		}

		GridView imagegrid = (GridView) findViewById(R.id.phoneImageGrid);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);		
		
		imagegrid.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Log.v(GalleryActivity.class.getSimpleName(), "id = " + id);
				finishSelectImage(id);
			}			
		});
	}

	private void finishSelectImage(long noteId){
		Intent intent = new Intent(this, NoteActivity.class);
		intent.putExtra("noteId", noteId);
		//startActivityForResult(intent, START);
		startActivityForResult(intent, NOTE);
		
//		Intent returnIntent = new Intent("RESULT_ACTION");
//		returnIntent.putExtra("noteId", noteId);
//		setResult(RESULT_OK,returnIntent);
//		finish();
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return list.get(position).note.getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.gallery_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.iconUploaded = (ImageView) convertView.findViewById(R.id.iconUploaded);
				holder.iconPlay = (ImageView) convertView.findViewById(R.id.iconPlay);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
						
			final DataHolder dh = list.get(position);			
			holder.imageview.setId(position);
			holder.imageview.setImageBitmap(dh.thumbnail);
			
			convertView.setLayoutParams(new GridView.LayoutParams(350,350));

			Note note = dh.note;
			if (note.isUploaded())
				holder.iconUploaded.setVisibility(View.VISIBLE);
			else
				holder.iconUploaded.setVisibility(View.INVISIBLE);
			
			if (note.getType() == Note.Type.VIDEO)
				holder.iconPlay.setVisibility(View.VISIBLE);
			else
				holder.iconPlay.setVisibility(View.INVISIBLE);
			
			holder.id = position;
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		ImageView iconUploaded;
		ImageView iconPlay;
		int id;
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();		
		mNotebook.close();
	}
	
}