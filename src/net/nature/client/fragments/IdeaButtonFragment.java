package net.nature.client.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import net.nature.client.R;
import net.nature.client.activities.IdeaActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class IdeaButtonFragment extends Fragment{

	private Button mButtonIdea;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}    

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_idea_button, container, false);        		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mButtonIdea = (Button) getActivity().findViewById(R.id.buttonIdea);
		mButtonIdea.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String screenshotPath = saveScreenshot();
				Intent intent = new Intent(getActivity(), IdeaActivity.class);
				intent.putExtra("screenshotPath", screenshotPath);
				startActivity(intent);
			}
		});
	}
	
	public void invokeIdea(View v){
		String screenshotPath = saveScreenshot();
		Intent intent = new Intent(getActivity(), IdeaActivity.class);
		intent.putExtra("screenshotPath", screenshotPath);
		startActivity(intent);
	}

	public String saveScreenshot(){
		File screenshotFileFolder = new File(
				Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES
						), 
						"ideas"
				);
		if(!screenshotFileFolder.exists())
			screenshotFileFolder.mkdirs();


		// generate the screenshot of the current content view
		View v1 =  getActivity().findViewById(android.R.id.content).getRootView();
		Bitmap bitmap;
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);

		File screenshotFile = new File(screenshotFileFolder, "" + (new Date()).getTime() + ".png");

		Log.v(getClass().getSimpleName(), "screenshot file = " + screenshotFile.getAbsolutePath());
		
		try {
			FileOutputStream fOut;			
			fOut = new FileOutputStream(screenshotFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);			
			fOut.flush();
			fOut.close();

			Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			//			File f = new File(path);
			Uri contentUri = Uri.fromFile(screenshotFile);
			mediaScanIntent.setData(contentUri);
			getActivity().sendBroadcast(mediaScanIntent);			

			return screenshotFile.getAbsolutePath();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
