package net.nature.client.activities;

import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.ParkActivity;
import net.nature.client.fragments.ToolbarFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class ActivitiesActivity extends FragmentActivity {

	public static final String PREFS_NAME = "NatureNetPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activities);
	}
	
	
	private void invokeActivityHelper(View view, final int activityId){
		ParkActivity parkActivity = ApplicationData.getParkActivity(getApplicationContext(), activityId);
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(parkActivity.getName());
		alert.setMessage(parkActivity.getDescription());
		alert.setPositiveButton("Take a Picture", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				ApplicationData.setCurrentParkActivity(getApplicationContext(), activityId);
				ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
				f.invokePhotoCapture();
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});		
		alert.show();   
	}
	
	public void invokeActivity1(View view){
		invokeActivityHelper(view, 1);
	}
		
	public void invokeActivity2(View view){
		invokeActivityHelper(view, 2);
	}
	
	public void invokeActivity3(View view){
		invokeActivityHelper(view, 3);
	}
	
	public void invokeActivity4(View view){
		invokeActivityHelper(view, 4);
	}
	
	public void invokeActivity5(View view){
		invokeActivityHelper(view, 5);
	}
	
	public void invokeActivity6(View view){
		invokeActivityHelper(view, 6);
	}
	
	public void invokeTour(View view){
		ToolbarFragment f = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		Intent intent = new Intent(this, MainMapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("showHelp",  true);
		startActivity(intent);
	}

}
