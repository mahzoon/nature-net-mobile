package net.nature.client.activities;

import net.nature.client.OnLandmarkSelectionEventListener;
import net.nature.client.R;
import net.nature.client.R.id;
import net.nature.client.TouchMapImageView;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Landmark;
import net.nature.client.fragments.ToolbarFragment;
import net.nature.client.views.LandmarkView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class MainMapActivity extends FragmentActivity implements  OnLandmarkSelectionEventListener  {

	private LandmarkView mViewLandmark;
	private TouchMapImageView mImageViewMap;
	private RelativeLayout mLayoutMap;
	private ImageButton mButtonToggleMapSize;
	private ZoomControls mZoom;
	private GestureDetector mDetector;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_map);
		mImageViewMap = (TouchMapImageView) findViewById(R.id.map);
		mImageViewMap.setMaxZoom(4f);
		mImageViewMap.setLandmarkSelectionEventListener(this);

		mButtonToggleMapSize = (ImageButton) findViewById(R.id.buttonToggleMapSize);				

		mLayoutMap = (RelativeLayout) findViewById(R.id.layoutMap);

		mViewLandmark = (LandmarkView) findViewById(R.id.viewLandmark);
		mViewLandmark.setMainMapActivity(this);

		// register the listener for the click on the drawer bar to shrink/expand the map
		OnTouchListener simpleTouchListener = new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {								
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					toggleMapSize(v);					
					break;
				}
				return true;
			}
		};
		((RelativeLayout) findViewById(R.id.layoutDrawerBar)).setOnTouchListener(simpleTouchListener);		
		

		mZoom = (ZoomControls) findViewById(id.zoomControlsMap);
		mZoom.setOnZoomInClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mImageViewMap.increaseZoom();				
			}			
		});
		mZoom.setOnZoomOutClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mImageViewMap.decreaseZoom();				
			}

		});

		Landmark landmark = ApplicationData.getCurrentLandmark(getApplicationContext());
		onLandmarkSelection(landmark);

		// center on the first map landmark
		Rect r = landmark.getRect();
		mImageViewMap.setCenter(r.centerX(), r.centerY());

		expandMap();

		boolean showHelp = this.getIntent().getBooleanExtra("showHelp", false);
		if (showHelp){
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);//ApplicationContext());
			alert.setTitle("Self-guided Tour");
			alert.setMessage("Touch an icon on the map to start the self-guided tour.  The tour will lead you around the Hallam Lake Nature "+
					"Preserve. The trail is a .3 mile loop. Please take pictures and field notes about your observations!");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});
			alert.show();   
		}
	}
	

	@Override
	public void onLandmarkSelection(Landmark landmark) {		
		mImageViewMap.setSelectedLandmark(landmark.getId());	
		mViewLandmark.setLandmark(landmark.getId());

		// every time a user clicks on a landmark on the map, it is selected as the
		// current landmark
		ApplicationData.setCurrentLandmark(getApplicationContext(),landmark.getId());		

		// hack: don't center the map any more
		mImageViewMap.setCenter(-1, -1);
	}


	public void shrinkMap(){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageViewMap.getLayoutParams());
		params.height = 200;
		mImageViewMap.setLayoutParams(params);
		mButtonToggleMapSize.setImageResource(R.drawable.arrow_down);
		mZoom.setVisibility(View.INVISIBLE); // zoom is hidden when map is small
		findViewById(android.R.id.content).invalidate();
	}
	
	public void adjustMapHeightBy(int d){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageViewMap.getLayoutParams());		
		params.height = params.height + d;
		if (params.height <= 800 && params.height >= 200){
			mImageViewMap.setLayoutParams(params);
			findViewById(android.R.id.content).invalidate();
		}
	}

	public void expandMap(){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageViewMap.getLayoutParams());
		params.height = 800;
		mImageViewMap.setLayoutParams(params);
		mButtonToggleMapSize.setImageResource(R.drawable.arrow_up);
		mZoom.setVisibility(View.VISIBLE);		
		findViewById(android.R.id.content).invalidate();
	}


	public void toggleMapSize(View view){
		if (mImageViewMap.getHeight() > 400){
			shrinkMap();
		}else{
			expandMap();
		}
	}

	public void onCameraButtonClick(View view){
		ToolbarFragment toolbar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.toolbar);
		toolbar.invokePhotoCapture();		
	}
	
	public void onLandmarkViewSelectionChanged(Landmark landmark){
		mImageViewMap.setSelectedLandmark(landmark.getId());
		ApplicationData.setCurrentLandmark(getApplicationContext(),landmark.getId());
		Rect r = landmark.getRect();
		mImageViewMap.setCenter(r.centerX(), r.centerY());
	}
	
	
}
