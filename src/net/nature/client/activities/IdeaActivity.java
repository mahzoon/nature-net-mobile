package net.nature.client.activities;

import net.nature.client.R;
import net.nature.client.database.Note;
import net.nature.client.database.Notebook;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class IdeaActivity extends Activity {

	private Notebook mNotebook;
	private ImageView mImageViewScreenshot;
	private String mScreenshotPath;
	private EditText mEditTextIdea;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_idea);
		
		mNotebook = new Notebook(getApplicationContext());				
		mImageViewScreenshot = (ImageView) findViewById(R.id.imageViewSceenshot);
		mEditTextIdea = (EditText) findViewById(R.id.editTextIdea);
		mScreenshotPath = getIntent().getStringExtra("screenshotPath");
		if (mScreenshotPath != null){
			Bitmap image = BitmapFactory.decodeFile(mScreenshotPath);
			mImageViewScreenshot.setImageBitmap(image);
		}
	}
	
	
	public void saveAndFinish(View view){		
		Note note = mNotebook.createNewNodeFromFile(mScreenshotPath, Note.Type.IDEA);
		note.setComment(mEditTextIdea.getText().toString());
		mNotebook.updateNote(note);
		
		Context context = getApplicationContext();
		CharSequence text = "Your idea has been saved!";
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
		
		finish();
	}
	
	public void cancel(View view){
		finish();
	}


	@Override
	protected void onDestroy() {
		mNotebook.close();
		super.onDestroy();
	}
	
	
	
}


