package net.nature.client.views;

import java.util.ArrayList;
import java.util.List;

import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Landmark;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import net.nature.client.database.ParkActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditParkActivityView extends LinearLayout {


	private TextView mTextViewParkActivity;
	private List<ParkActivity> activities;
	private String[] items;
	public EditParkActivityView(Context context){
		super(context);
		sharedConstructing(context);
	}

	public EditParkActivityView(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_edit_activity, this, true);
		setOrientation(LinearLayout.HORIZONTAL);


		mTextViewParkActivity = (TextView) findViewById(R.id.textViewCategories);
		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				edit(v);				
			}
		});
				
		activities = ApplicationData.getAllActivities(context);
		
		items = new String[activities.size()];
		for (int i = 0 ; i < activities.size(); ++i){
			items[i] = activities.get(i).getName();
		}
	}


	private Note mCurrentNote;
	public void modelToUI(){
		if (mCurrentNote != null){
			String text = mCurrentNote.getCategories();
			int activityId = mCurrentNote.getActivityId();
			if (activityId != -1){
				text = ApplicationData.getParkActivity(getContext(), activityId).getName();
			}else{			
				text = "(Touch to select)";
			}
			mTextViewParkActivity.setText(text);
		}
	}

	public void setNote(Note note){
		this.mCurrentNote = note;		
		modelToUI();
	}


	/**
	 * Set the states of the checkboxes based on a ',' separated
	 * string of category names
	 * 
	 * @param categoriesText
	 */
//	public void setSelectedCategories(String categoriesText){
////		String[] categoryNames = categoriesText.split(",");
////		for (int i=0; i < items.length; ++i){
////			states[i] = false;
////			for (String categoryName : categoryNames){
////				if (items[i].compareToIgnoreCase(categoryName) == 0){
////					states[i] = true;
////				}
////			}
////		}
//	}

//	public String getSelectedCategories(){
//		String categoriesText = "";	
//		for (int i = 0; i < states.length; ++i){
//			if (states[i]){
//				categoriesText = categoriesText + items[i] + ",";
//			}
//		}
//		
//		if (categoriesText.length() > 0){
//			// remove the last ','
//			categoriesText = categoriesText.subSequence(0, categoriesText.length()-1).toString();
//		}
//		return categoriesText;
//	}

	public void updateNoteCategories(){
		if (mCurrentNote != null){
			//String categoriesText = getSelectedCategories();

			Notebook mNotebook = new Notebook(getContext());
			Note note = mNotebook.getNote(mCurrentNote.getId());			
			note.setActivityId(mCurrentNote.getActivityId());
			note.setModified(true);
			mNotebook.updateNote(note);
			mNotebook.close();
			mCurrentNote = note;
			modelToUI();

			String msg = "";
			if (mCurrentNote.getActivityId() == -1){
				msg = "No activity is selected";
			}else{
				msg = "Activity is updated";
			}
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	public void edit(View v){		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Which activity?");
		
		// find the index of the activity 
		int choice = -1;
		for (int i = 0 ; i < activities.size(); ++i){
			if (activities.get(i).getId() == mCurrentNote.getActivityId()){
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
					mCurrentNote.setActivityId(activities.get((int)itemId).getId());
				}
//				SparseBooleanArray CheCked = ((AlertDialog) dialog).getListView().getSelectedItemId();
//				for (int i = 0; i < CheCked.size(); ++i){
//					if (CheCked.get(i)){
//						mCurrentNote.setActivityId(activities.get(i).getId());
//					}
//				}
				updateNoteCategories();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

}
