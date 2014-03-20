package net.nature.client.views;

import java.util.ArrayList;
import java.util.List;

import net.nature.client.R;
import net.nature.client.TouchMapImageView;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Landmark;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditLandmarkView2 extends LinearLayout {

	private TextView mTextViewLandmark;
	private List<Landmark> landmarks;
	private String[] items;
	
	public EditLandmarkView2(Context context){
		super(context);
		sharedConstructing(context);
	}

	public EditLandmarkView2(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_edit_landmark, this, true);

		mTextViewLandmark = (TextView) findViewById(R.id.textViewLandmark);

		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				edit(v);				
			}			
		});
		
		landmarks = ApplicationData.getAllLandmarks(context);
		
		items = new String[landmarks.size()];
		for (int i = 0 ; i < landmarks.size(); ++i){
			items[i] = landmarks.get(i).getName();
		}
	}

	private Note mCurrentNote;
	public void modelToUI(){
	if (mCurrentNote != null){
			String text = "";
			int landmarkId = mCurrentNote.getLandmarkId();
			if (landmarkId != -1){
				text = ApplicationData.getLandmark(getContext(), landmarkId).getName();
			}else{			
				text = "(Touch to select)";
			}
			mTextViewLandmark.setText(text);
		}
	}
	
	public Note getNote(){
		return mCurrentNote;
	}
	
	public void setNote(Note note){
		this.mCurrentNote = note;
		modelToUI();
	}

	private void updateNoteLandmark(){
		if (mCurrentNote != null){
			Notebook mNotebook = new Notebook(getContext());
			Note note = mNotebook.getNote(mCurrentNote.getId());
			note.setLandmarkId(mCurrentNote.getLandmarkId());
			note.setModified(true);
			mNotebook.updateNote(note);
			mNotebook.close();
			mCurrentNote = note;
			modelToUI();
			
			//Landmark landmark = getLandmark();
			String msg = "";
			if (mCurrentNote.getActivityId() == -1){
				msg = "No location is selected";
			}else{
				msg = "The location is set to " + getLandmark().getName();
			}
			//if (landmark != null){
				//msg = "The location is set to " + landmark.toString();
				Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
			//}
		}
	}
	
	public void edit(View v){		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Which Location?");
		
		// find the index of the location 
		int choice = -1;
		for (int i = 0 ; i < landmarks.size(); ++i){
			if (landmarks.get(i).getId() == mCurrentNote.getLandmarkId()){
				choice = i;
			}
		}
		
		builder.setSingleChoiceItems(items, choice, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				long itemId = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
				if (itemId != -1){
					//Log.d(, msg)
					mCurrentNote.setLandmarkId(landmarks.get((int)itemId).getId());
				}
//				SparseBooleanArray CheCked = ((AlertDialog) dialog).getListView().getSelectedItemId();
//				for (int i = 0; i < CheCked.size(); ++i){
//					if (CheCked.get(i)){
//						mCurrentNote.setLandmarkId(landmarks.get(i).getId());
//					}
//				}
				updateNoteLandmark();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	
	
	public Landmark getLandmark(){
		Landmark landmark = ApplicationData.getLandmark(getContext(), mCurrentNote.getLandmarkId());
		return landmark;
	}
}
