package net.nature.client.views;

import java.util.ArrayList;
import java.util.List;

import net.nature.client.R;
import net.nature.client.database.Landmark;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditCategoryView extends LinearLayout {


	private TextView mTextViewCategories;
	public EditCategoryView(Context context){
		super(context);
		sharedConstructing(context);
	}

	public EditCategoryView(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_edit_category, this, true);
		setOrientation(LinearLayout.HORIZONTAL);


		mTextViewCategories = (TextView) findViewById(R.id.textViewCategories);
		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				edit(v);				
			}
		});

		//		Button button = (Button) findViewById(R.id.buttonAddCategory);
		//		button.setOnClickListener(new OnClickListener(){
		//			@Override
		//			public void onClick(View v) {
		//				add(v);				
		//			}			
		//		});		
		//
		//		statesToUI();
	}


	final boolean[] states = new boolean[]{false, false, false, false};
	final String[] items = {"Animal","Plant","Landscape","Insect"};
	private Note mCurrentNote;
//	public String[] getSelectedCategories(){
//		List<String> ret = new ArrayList<String>();
//		for (int i = 0; i < items.length; ++i){			
//			if (states[i]){
//				ret.add(items[i]);				
//			}
//		}
//		return ret.toArray(new String[]{});
//	}	

	public void modelToUI(){
		if (mCurrentNote != null){
			String text = mCurrentNote.getCategories();
			if (text.length() == 0){
				text = "(Touch to select)";
			}
			mTextViewCategories.setText(text);
		}
	}

	public void setNote(Note note){
		this.mCurrentNote = note;
		setSelectedCategories(note.getCategories());
		modelToUI();
	}


	/**
	 * Set the states of the checkboxes based on a ',' separated
	 * string of category names
	 * 
	 * @param categoriesText
	 */
	public void setSelectedCategories(String categoriesText){
		String[] categoryNames = categoriesText.split(",");
		for (int i=0; i < items.length; ++i){
			states[i] = false;
			for (String categoryName : categoryNames){
				if (items[i].compareToIgnoreCase(categoryName) == 0){
					states[i] = true;
				}
			}
		}
	}

	public String getSelectedCategories(){
		String categoriesText = "";	
		for (int i = 0; i < states.length; ++i){
			if (states[i]){
				categoriesText = categoriesText + items[i] + ",";
			}
		}
		
		if (categoriesText.length() > 0){
			// remove the last ','
			categoriesText = categoriesText.subSequence(0, categoriesText.length()-1).toString();
		}
		return categoriesText;
	}

	public void updateNoteCategories(){
		if (mCurrentNote != null){
			String categoriesText = getSelectedCategories();

			Notebook mNotebook = new Notebook(getContext());
			Note note = mNotebook.getNote(mCurrentNote.getId());
			note.setCategories(categoriesText);
			note.setModified(true);
			mNotebook.updateNote(note);
			mNotebook.close();
			mCurrentNote = note;
			modelToUI();

			String msg = "";
			if (categoriesText.length() == 0){
				msg = "No category";
			}else{
				msg = "Categories are set to " + categoriesText;
			}
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	public void edit(View v){		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Which categories?");
		builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
			public void onClick(DialogInterface dialogInterface, int item, boolean state) {
			}
		});
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				SparseBooleanArray CheCked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
				for (int i = 0; i < states.length; ++i){
					states[i] = CheCked.get(i);
				}
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
