package net.nature.client.views;

import net.nature.client.R;
import net.nature.client.activities.MainMapActivity;
import net.nature.client.database.ApplicationData;
import net.nature.client.database.Landmark;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class LandmarkView extends LinearLayout {

	private Landmark mCurrentLandmark;
	private TextView mTextLandmarkName;
	private TextView mTextDescription;
	private ScrollView mScrollView;
	private LinearLayout mLayoutCameraButtons;
	
	private MainMapActivity activity;
	
	public LandmarkView(Context context){
		super(context);
		sharedConstructing(context);
	}

	public LandmarkView(Context context, AttributeSet attrs){
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_landmark, this, true);
		mTextLandmarkName = (TextView) findViewById(R.id.textLandmarkName);		
		mTextDescription = (TextView) findViewById(R.id.textLandmarkDescription);
		mScrollView = (ScrollView) findViewById(R.id.scrollDescription);		
		mLayoutCameraButtons = (LinearLayout) findViewById(R.id.layoutCameraButtons);
	}

	public void setLandmark(int landmarkId){
		mCurrentLandmark = ApplicationData.getLandmark(getContext(), landmarkId);
		modelToUI();
	}
	
	public void setLandmark(Landmark landmark){
		mCurrentLandmark = landmark;
		modelToUI();
	}

	public void modelToUI(){
		if (mCurrentLandmark != null){			
			int i = this.getResources().getIdentifier("landmark_" + mCurrentLandmark.getId(), "drawable", getContext().getPackageName());			
			mTextDescription.setCompoundDrawablesWithIntrinsicBounds(0, i, 0, 0);
			mTextLandmarkName.setText(mCurrentLandmark.toString());			
			mTextDescription.setText(mCurrentLandmark.getDescription());
			
			mLayoutCameraButtons.removeAllViews();
			for (String target : mCurrentLandmark.getTargets()){
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				Button button = new Button(getContext());
				button.setText("Take a picture of " + target);
				button.setTextSize(24);
				button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_camera, 0, 0,  0);
				button.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (activity != null)
							activity.onCameraButtonClick(v);						
					}					
				});
				mLayoutCameraButtons.addView(button, params);	
				
			}
			
			// after switching to another landmark, scroll to the top
			mScrollView.fullScroll(ScrollView.FOCUS_UP);
						
		}
	}
	
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev ) {
		
		if (gestureDetector == null){
			gestureDetector = new GestureDetector(getContext(), new GestureListener());
		}
		
		// TouchEvent dispatcher.
		if( gestureDetector != null ) {
			if( gestureDetector.onTouchEvent( ev ) )
				// If the gestureDetector handles the event, a swipe has been
				// executed and no more needs to be done.
				return true;
		}
		return super.dispatchTouchEvent( ev );
	}


	private GestureDetector gestureDetector;
	public class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.v(getClass().getSimpleName(), "onFling()");
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							Log.v(getClass().getSimpleName(), "SwipeRight");
							onSwipeRight();
						} else {
							Log.v(getClass().getSimpleName(), "SwipeLeft");
							onSwipeLeft();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}
	}
	
	public void onSwipeRight() {
		if (mCurrentLandmark != null){
			Landmark landmark = ApplicationData.getPreviousLandmark(getContext(), mCurrentLandmark.getId());
			setLandmark(landmark);
			activity.onLandmarkViewSelectionChanged(landmark);
		}
	}

	public void onSwipeLeft() {
		if (mCurrentLandmark != null){
			Landmark landmark = ApplicationData.getNextLandmark(getContext(), mCurrentLandmark.getId());
			setLandmark(landmark);
			activity.onLandmarkViewSelectionChanged(landmark);
		}
	}

	public MainMapActivity getMainMapActivity() {
		return activity;
	}

	public void setMainMapActivity(MainMapActivity activity) {
		this.activity = activity;
	}
		
}



