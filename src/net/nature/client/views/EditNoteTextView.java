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
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class EditNoteTextView extends LinearLayout {

	private TextView mTextViewNoteText;
	private Note mCurrentNote = null;

	public EditNoteTextView(Context context){
		super(context);
		sharedConstructing(context);
	}

	public EditNoteTextView(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}


	private void sharedConstructing(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_edit_note_text, this, true);
		
		setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				edit();
			}
			
		});
		mTextViewNoteText = (TextView) findViewById(R.id.textViewNoteText);
//		mEditTextNote.setOnEditorActionListener(new OnEditorActionListener(){
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				// http://stackoverflow.com/questions/11301061/null-keyevent-and-actionid-0-in-oneditoraction-jelly-bean-nexus-7			
//				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
//					return false;
//				} else if (actionId == EditorInfo.IME_ACTION_SEARCH || 
//						actionId == EditorInfo.IME_ACTION_DONE ||
//						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//					updateNoteText();
//				}				
//				return false;		
//			}			
//		});
	}
	
	
	private void updateNoteText(String text){
		if (mCurrentNote != null){
			Notebook mNotebook = new Notebook(getContext());
			Note note = mNotebook.getNote(mCurrentNote.getId());
			note.setComment(text);
			note.setModified(true);
			mNotebook.updateNote(note);
			mNotebook.close();
			mCurrentNote = note;
		
			modelToUI();			
			
			String msg = "The note is saved";
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void modelToUI(){
		if (mCurrentNote != null){
			if (mCurrentNote.getComment().length() == 0){
				mTextViewNoteText.setText("(Touch to enter a note)");
			}else{
				mTextViewNoteText.setText(mCurrentNote.getComment());
			}
		}
	}
	
	public void setNote(Note note){
		this.mCurrentNote = note;
		modelToUI();
	}
	
	public void edit(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		final EditText txt = new EditText(getContext());
		txt.setText(mCurrentNote.getComment());
		txt.setHint("Touch here to type a note");
		txt.setMinLines(5);
		txt.setGravity(Gravity.TOP);

		alert.setTitle("Type a note");
		alert.setView(txt);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				updateNoteText(txt.getText().toString());
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();   
	}

}
