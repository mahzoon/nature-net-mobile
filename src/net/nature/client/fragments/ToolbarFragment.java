package net.nature.client.fragments;

import java.io.File;

import net.nature.client.GoogleDriveSyncService;
import net.nature.client.R;
import net.nature.client.activities.ActivitiesActivity;
import net.nature.client.activities.GalleryActivity;
import net.nature.client.activities.MainMapActivity;
import net.nature.client.activities.NoteActivity;
import net.nature.client.activities.UserActivity;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ToolbarFragment extends Fragment{

	private static final int CAPTURE_IMAGE = 1;
	private static final int CAPTURE_VIDEO = 2;
	private static final int NOTE = 7;
	private static final int USER = 8;

	private Notebook mNotebook;
	private String mCurrentNoteFilePath;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}    

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		mNotebook.close();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode);

		switch (requestCode){

		case CAPTURE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {				
				Note newNote = mNotebook.createNewNoteFromFile(mCurrentNoteFilePath);
				galleryAddPic(mCurrentNoteFilePath);
				invokeNote(newNote.getId());
			}
			break;

		case CAPTURE_VIDEO:
			if (resultCode == Activity.RESULT_OK) {							
				Note newNote = mNotebook.createNewNoteFromFile(mCurrentNoteFilePath);
				galleryAddPic(mCurrentNoteFilePath);
				invokeNote(newNote.getId());
			}
			break;			

		}
	}


	private void invokeNote(long id) {	
		Intent intent = new Intent(getActivity(), NoteActivity.class);
		intent.putExtra("noteId", id);
		startActivity(intent);			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_toolbar, container, false);        		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

//		ImageButton b = (ImageButton) getActivity().findViewById(R.id.btnGallery);
//		b.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View view) {
//				invokeGallery();				
//			}        	
//		});

		ImageButton b = (ImageButton) getActivity().findViewById(R.id.btnCapture);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokePhotoCapture();
			}
		});   
		b = (ImageButton) getActivity().findViewById(R.id.btnVideo);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokeVideoCapture();
			}        	
		});  
		((ImageButton) getActivity().findViewById(R.id.btnNote)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokeNote();
			}        	
		});
		b = (ImageButton) getActivity().findViewById(R.id.btnActivities);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokeActivities();
			}
		});	
		b = (ImageButton) getActivity().findViewById(R.id.btnIdea);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokeIdea();
			}
		});		
		b = (ImageButton) getActivity().findViewById(R.id.btnUser);
		b.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				invokeUser();
			}
		});		


		mNotebook = new Notebook(getActivity().getBaseContext());
	}

	public void invokePhotoCapture(){
		File f = mNotebook.createNewPhotoNoteFile();
		mCurrentNoteFilePath = f.getAbsolutePath();
		Log.v(getClass().getSimpleName(), "Invoke photo capture: path = " + mCurrentNoteFilePath);

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);		
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
	}
	
	public void invokeActivities() {
		Intent intent = new Intent(getActivity(), ActivitiesActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}


	public void invokeMap() {
		Intent intent = new Intent(getActivity(), MainMapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void invokeUser() {
		Intent intent = new Intent(getActivity(), UserActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, USER);	
	}

	public void invokeVideoCapture(){
		File f = mNotebook.createNewVideoNoteFile();
		mCurrentNoteFilePath = f.getAbsolutePath();
		Log.v(getString(R.string.app_name), "invoke video pcature: path = " + mCurrentNoteFilePath);

		Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		startActivityForResult(intent, CAPTURE_VIDEO);
	}

	public void invokeGallery() {
		Intent intent = new Intent(getActivity(), GalleryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);			
	}

	public void invokeNote() {
		Intent intent = new Intent(getActivity(), NoteActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, NOTE);
	}

	public void invokeIdea() {
		final EditText input = new EditText(getActivity());
		new AlertDialog.Builder(getActivity())
		.setTitle("Submit Design Idea")
		.setMessage("Here you can submit your design idea about the NatureNet project:")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int whichButton) {
				 Editable ed = input.getText(); 
				 GoogleDriveSyncService gdss = new GoogleDriveSyncService(getActivity());
				 String r = gdss.addIdea(ed.toString(), ApplicationData.getCurrentUser(getActivity()));
				 if (r!=null)
				 {
					String text = "Thank you for your contribution. Your idea was submitted.";
					Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
					toast.show();
				 }
			 }
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			 }
		}).show();	
	}
	
	private void galleryAddPic(String path) {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}



}
