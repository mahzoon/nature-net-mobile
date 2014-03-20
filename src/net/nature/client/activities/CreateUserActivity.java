package net.nature.client.activities;

import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class CreateUserActivity extends FragmentActivity {

	private User mCurrentUser;
	private EditText mEditUsername;
	private ImageView mImageViewAvatar;
	private String mAvatarName;
	private Button mStartButton;


	private String createRandomAvatarName(){
		String names[] = {
				"NN_BearGreen",
				"NN_BisonOrange",
				"NN_CaribouPurple",
				"NN_FrogRed",
				"NN_GatorRed",
				"NN_HarePurple",
				"NN_HorseOrange",
				"NN_SnakeGreen",
				"NN_SquirrelOrange",
				"NN_TortoisePurple"				
		};

		int i = (int) (Math.random() * names.length); 
		return names[i];
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		mEditUsername = ((EditText) findViewById(R.id.editUsername));


		mAvatarName = createRandomAvatarName();

		// display the avatar
		int id = this.getResources().getIdentifier(mAvatarName.toLowerCase(), "drawable", this.getPackageName());
		mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);		
		mImageViewAvatar.setImageResource(id);
		
		mStartButton = (Button) findViewById(R.id.buttonStart);
		mStartButton.setVisibility(View.INVISIBLE);
		
		mEditUsername.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// http://stackoverflow.com/questions/11301061/null-keyevent-and-actionid-0-in-oneditoraction-jelly-bean-nexus-7			
				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				} else if (actionId == EditorInfo.IME_ACTION_SEARCH || 
						actionId == EditorInfo.IME_ACTION_DONE ||
						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

					String username = mEditUsername.getText().toString();
					if (username.length()>0){
						mStartButton.setVisibility(View.VISIBLE);
					}else{
						mStartButton.setVisibility(View.INVISIBLE);
					}
				}				
				return false;		
			}
		});		
	}


	public void start(View view){
		String username = mEditUsername.getText().toString();
		if (username.length() == 0){
			String text = "Please enter your name to start";
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			toast.show();		
			return;
		}
		
		mCurrentUser = ApplicationData.createNewUser(getApplicationContext(), username, mAvatarName);
		if (mCurrentUser == null) return;
		ApplicationData.setCurrentUser(getApplicationContext(), mCurrentUser);
		ApplicationData.setCurrentLandmark(getApplicationContext(),1);

		Intent data = new Intent();
		setResult(RESULT_OK, data);
		finish();
	}
}
