package net.nature.client.activities;

import net.nature.client.activities.ConsentFormActivity;
import net.nature.client.R;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Notebook;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class RootActivity extends Activity {

	private static final int SYNC_INTERVAL = 30000;

	public static final int START = 2;
	public static final int OVERVIEW = 3;
	public static final int CONSENT = 4;
	
	private LocationListener mLocationListener;
	private Location mCurrentBestLocation;
	private SyncTask mSyncTask;
	private Notebook mNotebook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root);

		//startLocationService();
		mNotebook = new Notebook(getBaseContext());

		mSyncTask = new SyncTask();
		mSyncTask.execute();
		
		PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
		final String eulaKey = "consentform_" + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        //if(hasBeenShown == false)
        //{
        //	Intent intent = new Intent(this, ConsentFormActivity.class);
        //	startActivityForResult(intent, CONSENT);
        //}
        //else
        //{
        	Intent intent = new Intent(this, OverviewActivity.class);
    		startActivityForResult(intent, OVERVIEW);
        //}
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
	
	class SyncTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			try {
				while (!isCancelled()){	        	
					mNotebook.sync();
					Thread.sleep(SYNC_INTERVAL);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}       
	}  

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(getClass().getSimpleName(), "onActivityResult: requestCode = " + requestCode + ", resultCode= " + resultCode);

		switch (requestCode){
		case OVERVIEW:
			if (resultCode == Activity.RESULT_OK){			
				ApplicationData.setCurrentLandmark(getApplicationContext(), 1);
				Intent intent = new Intent(this, UserActivity.class);
				startActivityForResult(intent, START);
			}else{
				// if the user presses BACK in the overview screen
				Log.v(getClass().getSimpleName(), "finishing root activity");				
				// finish the root task, which quits the app
				finish();
			}
			break;		
		case START:
			if (resultCode == Activity.RESULT_CANCELED){
				// if the user presses BACK, we show the overview screen again
				Intent intent = new Intent(this, OverviewActivity.class);
				startActivityForResult(intent, OVERVIEW);
			}
			break;

		case CONSENT:
			if (resultCode == Activity.RESULT_OK){			
				Intent intent = new Intent(this, OverviewActivity.class);
				startActivityForResult(intent, OVERVIEW);
			}else{
				// if the user presses BACK in the overview screen
				Log.v(getClass().getSimpleName(), "finishing root activity");				
				// finish the root task, which quits the app
				finish();
			}
			break;
		default:
			Log.v(getClass().getSimpleName(), "finishing root activity");
			finish();
			break;
		}
	}

	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.v(getClass().getSimpleName(), "onDestroy()");
		stopLocationService();		
		if (mSyncTask != null){
			mSyncTask.cancel(true);			
		}
		mNotebook.close();
	}

	private void startLocationService(){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {

				// Called when a new location is found by the network location provider.
				if (location != null){
					if (isBetterLocation(location, mCurrentBestLocation)){
						mCurrentBestLocation = location;

						SharedPreferences settings = getSharedPreferences("NatureNetPrefs", 0);
						SharedPreferences.Editor editor = settings.edit();							
						editor.putString("long", Double.toString(location.getLongitude()));
						editor.putString("lat",Double.toString(location.getLatitude()));						
						editor.commit();
					}
				}
				Log.d("Location", "currnet best location is: " + 
						"long=" + mCurrentBestLocation.getLongitude() + ", " +
						"lat=" + mCurrentBestLocation.getLatitude());
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.d("Location","provider ["  + provider + "]'s status is changed to " + status);
			}

			public void onProviderEnabled(String provider) {
				Log.d("Location","provider ["  + provider + "] is enabled");

			}

			public void onProviderDisabled(String provider) {
				Log.d("Location","provider ["  + provider + "] is disabled");				
			}
		};

		// Register the listener with the Location Manager to receive location updates
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		Log.d("Location", "location service is started");

		//locationManager.get
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation != null){
			mCurrentBestLocation = lastKnownLocation;
			Log.v("Location", "last known location is: " + lastKnownLocation.toString());
		}else
			Log.v("Location", "last known location is: " + "N/A");
	}

	private void stopLocationService(){
		if (mLocationListener != null){
			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(mLocationListener);
			Log.d("Location", "location service is stopped");
		}
	}


	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
