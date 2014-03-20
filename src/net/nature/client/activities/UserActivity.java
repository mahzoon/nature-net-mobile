package net.nature.client.activities;

import net.nature.client.GoogleDriveSyncService;
import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.User;
import net.nature.client.fragments.ToolbarFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends FragmentActivity {

	public static final String PREFS_NAME = "NatureNetPrefs";

	private TextView mEditUsername;

	private ImageView mImageViewAvatar;
	private User mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		mEditUsername = ((TextView) findViewById(R.id.editUsername));		

		mCurrentUser = ApplicationData.getCurrentUser(getApplicationContext());
		String avatarName = mCurrentUser.getAvatarName();
		mEditUsername.setText(mCurrentUser.getName());

		int id = this.getResources().getIdentifier(avatarName.toLowerCase(), "drawable", this.getPackageName());
		mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);		
		mImageViewAvatar.setImageResource(id);
	}
	
	public void invokeNote(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		f.invokeNote();
	}
	
	public void invokeGallery(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		f.invokeGallery();
	}
	
	public void invokeActivities(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		f.invokeActivities();
	}
		
	public void invokeInfo(View view){
//		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
//		f.invokeGallery();
	}
	
	public void invokeCamera(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		f.invokePhotoCapture();
	}
	
	public void invokeMap(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		Intent intent = new Intent(this, MainMapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("showHelp",  true);
		startActivity(intent);
	}
	
	public void invokeIdea(View view){
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
		.setTitle("Submit Design Idea")
		.setMessage("Here you can submit your design idea about the NatureNet project:")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int whichButton) {
				 Editable ed = input.getText(); 
				 GoogleDriveSyncService gdss = new GoogleDriveSyncService(UserActivity.this);
				 String r = gdss.addIdea(ed.toString(), mCurrentUser);
				 if (r!=null)
				 {
					String text = "Thank you for your contribution. Your idea was submitted.";
					Toast toast = Toast.makeText(UserActivity.this, text, Toast.LENGTH_LONG);
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

}
