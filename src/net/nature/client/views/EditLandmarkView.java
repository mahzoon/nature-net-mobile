package net.nature.client.views;

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

public class EditLandmarkView extends LinearLayout {


	private TextView mTextViewLandmark;
	private Note mCurrentNote;
	
	public EditLandmarkView(Context context){
		super(context);
		sharedConstructing(context);
	}

	public EditLandmarkView(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_edit_landmark, this, true);

		mTextViewLandmark = (TextView) findViewById(R.id.textViewLandmark);

		//Button button = (Button)  findViewById(R.id.buttonEditNote);
		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				edit(v);				
			}			
		});		
//		statesToUI();
	}

	public void modelToUI(){
		Landmark landmark = ApplicationData.getLandmark(getContext(),  mCurrentNote.getLandmarkId());
		String text = "";
		if (landmark == null){
			text = "????";
		}else{
			text = landmark.toString();
		}
		mTextViewLandmark.setText(text);

	}

	public void edit(View v){		
		final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		final TouchMapImageView img = new TouchMapImageView(getContext());
		img.setLayoutParams(new LayoutParams(200,200));
		img.setImageResource(R.drawable.newmap);
		img.setMaxZoom(4f);
		
		Landmark landmark = getLandmark();
		if (landmark != null){
			Rect r = landmark.getRect();
			img.setCenter(r.centerX(), r.centerY()); 
			img.setSelectedLandmark(landmark.getId());
		}
		

		alert.setTitle("Select a location");
		alert.setView(img);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int landmarkId = img.getSelectedLandmark();
				updateNoteLandmark(landmarkId);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();   
	}
	
	
	public Note getNote(){
		return mCurrentNote;
	}
	
	public void setNote(Note note){
		this.mCurrentNote = note;
		modelToUI();
	}
	
	public Landmark getLandmark(){
		Landmark landmark = ApplicationData.getLandmark(getContext(), mCurrentNote.getLandmarkId());
		return landmark;
	}
	
	private void updateNoteLandmark(int landmarkId){
		if (mCurrentNote != null){
			Notebook mNotebook = new Notebook(getContext());
			Note note = mNotebook.getNote(mCurrentNote.getId());
			note.setLandmarkId(landmarkId);
			note.setModified(true);
			mNotebook.updateNote(note);
			mNotebook.close();
			mCurrentNote = note;
			modelToUI();
			
			Landmark landmark = getLandmark();
			if (landmark != null){
				String text = "The location is set to " + landmark.toString();
				Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
