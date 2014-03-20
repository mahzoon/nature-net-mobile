package net.nature.client.fragments;

import net.nature.client.R;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
//import net.nature.client.views.EditCategoryView;
import net.nature.client.views.EditLandmarkView2;
import net.nature.client.views.EditNoteTextView;
import net.nature.client.views.EditParkActivityView;
import net.nature.client.views.TouchImageView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditNoteFragment extends Fragment  {
	private ImageView mImageViewPlay;
	private TouchImageView mTouchImageViewNotePhoto;
	
	private EditNoteTextView mEditNoteText;
	//private EditLandmarkView mEditLandmark;
	private EditLandmarkView2 mEditLandmark;
	//private EditCategoryView mEditCategory;
	private EditParkActivityView mEditParkActivity;
	
	private Notebook mNotebook;
	private Note mCurrentNote;
	private Bitmap bitmap;
	private TextView mTextViewUsername;


	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_edit_note, container, false);
		//mEditCategory = (EditCategoryView) view.findViewById(R.id.editCategory);
		mEditLandmark = (EditLandmarkView2) view.findViewById(R.id.editLandmark);
		mEditNoteText = (EditNoteTextView) view.findViewById(R.id.editNoteText);
		mEditParkActivity = (EditParkActivityView) view.findViewById(R.id.editParkActivity);
		
		mTextViewUsername = (TextView) view.findViewById(R.id.textViewUsername);		
		mImageViewPlay = (ImageView) view.findViewById(R.id.imageViewPlay);
		mTouchImageViewNotePhoto = (TouchImageView) view.findViewById(R.id.touchImageViewNotePhoto);			
		
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();	
		mNotebook.close();
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		long id = getArguments().getLong("id", -1);
		mNotebook = new Notebook(getActivity().getApplicationContext());
		mCurrentNote = mNotebook.getNote(id);
		
		mEditLandmark.setNote(mCurrentNote);
		mEditNoteText.setNote(mCurrentNote);		
		//mEditCategory.setNote(mCurrentNote);
		mEditParkActivity.setNote(mCurrentNote);
		mTextViewUsername.setText(mCurrentNote.getUsername());
		
		loadNoteImageInViewer(mCurrentNote);
		//syncNoteToUI();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode);
		super.onActivityResult(requestCode, resultCode, data);		
	}


	public Bitmap decodeImageFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeFile(filePath, o2);
	}

	private void loadNoteImageInViewer(Note note){
		if (note != null && mTouchImageViewNotePhoto != null){
			String filePath = note.getPath();
			if (note.getType() == Note.Type.PHOTO){
				bitmap = decodeImageFile(filePath);
			}else if (note.getType() == Note.Type.VIDEO){
				bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
						MediaStore.Images.Thumbnails.MINI_KIND);
			}
			mTouchImageViewNotePhoto.setImageBitmap(bitmap);
			switch (mCurrentNote.getType()){
			case Note.Type.VIDEO:
				mImageViewPlay.setVisibility(View.VISIBLE);
				break;

			case Note.Type.PHOTO:
				mImageViewPlay.setVisibility(View.INVISIBLE);
				break;

			}
		}
	}

	public static Fragment newInstance(long id) {
		EditNoteFragment f = new EditNoteFragment();
		Bundle args = new Bundle();
		args.putLong("id", id);
		f.setArguments(args);
		return f;
	}


}
