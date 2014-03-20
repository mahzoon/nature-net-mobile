package net.nature.client.activities;

import net.nature.client.GoogleDriveSyncService;
import net.nature.client.R;
import net.nature.client.TouchMapImageView;
import net.nature.client.activities.SelectUserDialog.OnUserSelectedListener;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Landmark;
import net.nature.client.database.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.AsyncTask;

public class OverviewActivity extends FragmentActivity {

	private static final int CREATE = 1;
	private static final int SELECT = 2;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
		android.os.StrictMode.setThreadPolicy(policy); 
	}	

	public void startCreateUser(View view){
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivityForResult(intent, CREATE);
	}

	public void onUserSelected(User user){
		ApplicationData.setCurrentUser(getApplicationContext(), user);		
		ApplicationData.setCurrentLandmark(getApplicationContext(), 1);
		Intent data = new Intent();
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	public void startSelectUser(View view){
		
		pd = new ProgressDialog(OverviewActivity.this);
		//pd.setTitle("Please Wait...");
		pd.setMessage("Downloading List of Users...");
		pd.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				GoogleDriveSyncService gdss = new GoogleDriveSyncService(OverviewActivity.this);
				gdss.updateUserTable();
				pd.dismiss();
				
				OverviewActivity.this.runOnUiThread(new Runnable() {
					  public void run() {
						  SelectUserDialog dialog = new SelectUserDialog(OverviewActivity.this);
							dialog.setOnUserSelectedListener(new OnUserSelectedListener(){
								@Override
								public void onUserSelected(User user) {
									OverviewActivity.this.onUserSelected(user);
								}
							});
							dialog.show();
					  }
					});
			}
		}).start();
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode + ", resultCode= " + resultCode);

		if (resultCode == RESULT_OK ){
			Log.v(getClass().getSimpleName(), "onActivityResult: finished");
			setResult(RESULT_OK, data);
			finish();
		}else if (resultCode == RESULT_CANCELED){
			Log.v(getClass().getSimpleName(), "onActivityResult: canceled");
		}
	} 

}
