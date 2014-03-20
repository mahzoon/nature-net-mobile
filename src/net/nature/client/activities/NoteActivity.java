package net.nature.client.activities;

import java.io.File;
import java.util.List;

import net.nature.client.R;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import net.nature.client.fragments.EditNoteFragment;
import net.nature.client.fragments.ToolbarFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NoteActivity extends FragmentActivity {

	private Notebook mNotebook;	 
	private Note mCurrentNote;	
	private List<Note> mAllNotes;
	private ViewPager mPagerEditNote;
	private MyAdapter mPagerAdapter;
	private Button mButtonDelete;
	private TextView mTextFirstStep;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);		
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode);
		Log.v(getClass().getSimpleName(), "current: " + mPagerEditNote.getCurrentItem());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(getClass().getSimpleName(),"onCreate()");		
		setContentView(R.layout.activity_note);

		mNotebook = new Notebook(getBaseContext());
		mAllNotes = mNotebook.getAllNotes();
				
		mButtonDelete = (Button) findViewById(R.id.buttonDelete);
		mTextFirstStep = (TextView) findViewById(R.id.textFirstStepInstruction);
		
		if (mAllNotes.size() == 0){
			mButtonDelete.setVisibility(View.INVISIBLE);
			mTextFirstStep.setVisibility(View.VISIBLE);
		}else{
			mButtonDelete.setVisibility(View.VISIBLE);
			mTextFirstStep.setVisibility(View.INVISIBLE);			
		}

		mPagerAdapter = new MyAdapter(getSupportFragmentManager());		
		mPagerEditNote = (ViewPager)findViewById(R.id.pager);
		mPagerEditNote.setAdapter(mPagerAdapter);


		long noteId = getIntent().getLongExtra("noteId",-1);
		if (noteId != -1){
			onNoteSelected(noteId);			
		}else{
			mPagerEditNote.setCurrentItem(0);
		}
	}

	public void invokePlayVideo(View view){		
		if (mCurrentNote != null && mCurrentNote.getType() == Note.Type.VIDEO){
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);			 
			intent.setDataAndType(Uri.fromFile(new File(mCurrentNote.getPath())),"video/3gpp"); 
			startActivity(intent);
		}
	}	
	
	public void newNote(View view){
		ToolbarFragment toolbar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		toolbar.invokePhotoCapture();
	}

	public void delete(View view){
		new AlertDialog.Builder(this)
		.setTitle("Deleting a note")
		.setMessage("Are you sure about deleting this note?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteCurrentItem();
			}
		})
		.setNegativeButton("No", null)
		.show();
	}

	private void deleteCurrentItem(){
		int position = mPagerEditNote.getCurrentItem();		
		Note noteToDelete = mAllNotes.get(position);
		mNotebook.deleteNote(noteToDelete.getId());
		
		mAllNotes.remove(position);
		// http://stackoverflow.com/questions/8060904/add-delete-pages-to-viewpager-dynamically
		mPagerAdapter.notifyDataSetChanged();
		
		if (mAllNotes.size() == 0){
			mButtonDelete.setVisibility(View.INVISIBLE);
			mTextFirstStep.setVisibility(View.VISIBLE);
		}else{
			mButtonDelete.setVisibility(View.VISIBLE);
			mTextFirstStep.setVisibility(View.INVISIBLE);			
		}
	}

	public void onNoteSelected(long noteId) {		
		int position=-1;
		for (int i = 0 ; i < mAllNotes.size(); ++i){
			if (mAllNotes.get(i).getId() == noteId){
				position = i;
			}
		}			
		mPagerEditNote.setCurrentItem(position);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		mNotebook.close();
	}

	public class MyAdapter extends FragmentStatePagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return mAllNotes.size();
		}

		@Override
		public Fragment getItem(int position) {
			Note note = mAllNotes.get(position);
			return EditNoteFragment.newInstance(note.getId());
		}

		// http://stackoverflow.com/questions/10396321/remove-fragment-page-from-viewpager-in-android
		@Override
		public int getItemPosition(Object object){
			return PagerAdapter.POSITION_NONE;
		}
	}


}