package net.nature.client.views;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {


	Matrix matrix;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;


	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	private int mSelectedLandmarkId;
	ScaleGestureDetector mScaleDetector;

	Context context;

	public TouchImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		m = new float[9];
		setScaleType(ScaleType.MATRIX);		
		setImageMatrix(matrix);

		setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!touchable)
					return true;

				mScaleDetector.onTouchEvent(event);
				PointF curr = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					last.set(curr);
					start.set(last);
					mode = DRAG;
					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						float deltaX = curr.x - last.x;
						float deltaY = curr.y - last.y;
						float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);
						float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);
						matrix.postTranslate(fixTransX, fixTransY);
						fixTrans();
						last.set(curr.x, curr.y);
					}
					break;

				case MotionEvent.ACTION_UP:
					mode = NONE;
					int xDiff = (int) Math.abs(curr.x - start.x);
					int yDiff = (int) Math.abs(curr.y - start.y);
					if (xDiff < CLICK && yDiff < CLICK){
						performClick();
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				}

				setImageMatrix(matrix);
				invalidate();
				return true; // indicate event was handled
			}

		});
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	public void increaseZoom(){
		applyScale(1.25f);
		invalidate();
	}

	public void decreaseZoom(){
		applyScale(0.75f);
		invalidate();
	}

	private void applyScale(float scaleFactor){
		float origScale = saveScale;
		saveScale *= scaleFactor;

		if (saveScale > maxScale) {
			saveScale = maxScale;
			scaleFactor = maxScale / origScale;
		} else if (saveScale < minScale) {
			saveScale = minScale;
			scaleFactor = minScale / origScale;
		}

		matrix.postScale(scaleFactor, scaleFactor, viewWidth / 2, viewHeight / 2);
		fixTrans();
		setImageMatrix(matrix);
		invalidate();
	}


	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (!touchable)
				return true;

			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}

			if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
			else
				matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());

			fixTrans();
			return true;
		}
	}

	void fixTrans() {
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
		float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

		if (fixTransX != 0 || fixTransY != 0)
			matrix.postTranslate(fixTransX, fixTransY);
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}


	private boolean touchable = true;
	public void setTouchable(boolean touchable){
		this.touchable = touchable;
	}

	public boolean isTouchable(){
		return touchable;
	}

	private int centerx=-1;
	private int centery=-1;
	public void setCenter(int x, int y){
		Log.v(getClass().getSimpleName(), "set center at " + x + "," + y);
		centerx = x;
		centery = y;	
		centerHelper(x,y,viewWidth,viewHeight);
	}
	
	private void centerHelper(int x, int y, int viewWidth, int viewHeight){
		
		if (centerx < 0 || centery < 0 || viewWidth == 0 || viewHeight == 0)
			return;

		float scale;

		Drawable drawable = getDrawable();
		if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
			return;
		int bmWidth = drawable.getIntrinsicWidth();
		int bmHeight = drawable.getIntrinsicHeight();

		//Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

		float scaleX = (float) viewWidth / (float) bmWidth;
		float scaleY = (float) viewHeight / (float) bmHeight;
		scale = Math.min(scaleX, scaleY);
		matrix.setScale(scale, scale);

		int dx = (int) -(1f*centerx*scale) + viewWidth/2;
		int dy = (int) -(1f*centery*scale) + viewHeight/2;

		matrix.postTranslate(dx,dy);

		origWidth = viewWidth;
		origHeight = viewHeight;


		matrix.postScale(1/scale, 1/scale, viewWidth/2,  viewHeight/2);

		saveScale = 1/scale;

		setImageMatrix(matrix);
		invalidate();		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		//
		// Rescales image on rotation
		//
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1){
						
			//Fit to screen.
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			//Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);

			// Center the image
			float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
			float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);

			origWidth = viewWidth - 2 * redundantXSpace;
			origHeight = viewHeight - 2 * redundantYSpace;

			setImageMatrix(matrix);
			invalidate();
			
		}
	}

}
