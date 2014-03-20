package net.nature.client.activities;

import net.nature.client.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;

public class ConsentFormActivity extends Activity {
	
	//private String EULA_PREFIX = "eula_";
	//private Activity mActivity; 
	private String eulaKey;
	
	//public ConsentFormActivity(Activity context) {
	//	mActivity = context; 
	//}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consentform);
		android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
		android.os.StrictMode.setThreadPolicy(policy);
		
		PackageInfo versionInfo = getPackageInfo();
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
		eulaKey = "consentform_" + versionInfo.versionCode;
	}
	
	private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
             pi = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi; 
    }

	public void accepted(View view)
	{
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(eulaKey, true);
		editor.commit();
		//finish();
		
		Intent data = new Intent();
		setResult(Activity.RESULT_OK, data);
		finish();
	}
	
	public void declined(View view)
	{
		Intent data = new Intent();
		setResult(Activity.RESULT_CANCELED, data);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode + ", resultCode= " + resultCode);

		setResult(resultCode, data);
		finish();
	} 
	
//     public void show() {
//        PackageInfo versionInfo = getPackageInfo();
//
//        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
//		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
//        if(hasBeenShown == false){
//
//        	// Show the Eula
//            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
//            
//            //Includes the updates as well so users know what changed. 
//            String message = "The EULA Message";
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
//                    .setTitle(title)
//                    .setMessage(message)
//                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            // Mark this version as read.
//                            SharedPreferences.Editor editor = prefs.edit();
//                            editor.putBoolean(eulaKey, true);
//                            editor.commit();
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// Close the activity as they have declined the EULA
//							mActivity.finish(); 
//						}
//                    	
//                    });
//            builder.create().show();
//        }
//    }
}
