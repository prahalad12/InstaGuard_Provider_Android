package com.instaguard.provider.Helpers.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WidgetService extends Service  {
	public static final String EXTRA_CUTOUT_SAFE_AREA = "WidgetService_cutout_safe_area";
	private WindowManager windowManager;
	private ImageView widgetHead;
	WindowManager.LayoutParams params;

	private GestureDetector gestureDetector;


	String driverId;

	DatabaseReference driverDistanceDatabaseRef;
	ValueEventListener driverDistanceValueEventListener;
	double distance;


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
		driverId = prefs.getString(Utils.SP_DRIVERID, null);

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		gestureDetector = new GestureDetector(this, new SingleTapConfirm());
		widgetHead = new ImageView(this);

		widgetHead.setImageResource(R.mipmap.ic_launcher_round);

		//https://stackoverflow.com/questions/46208897/android-permission-denied-for-window-type-2038-using-type-application-overlay
		//https://stackoverflow.com/questions/4481226/creating-a-system-overlay-window-always-on-top
		int LAYOUT_FLAG;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

			params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					LAYOUT_FLAG,
					WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

           /* params = new WindowManager.LayoutParams(
                	WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
                            */


			params.gravity = Gravity.TOP | Gravity.START;
			params.x = 0;
			params.y = 100;

			//this code is for dragging the chat head
			widgetHead.setOnTouchListener(new View.OnTouchListener() {

				private static final String TAG = "WidgetService";
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (gestureDetector.onTouchEvent(event)) {
						Log.e(TAG, "onTouch: ------>"+event );
						// single tap
						stopService(new Intent(WidgetService.this, WidgetService.class));
						callDistanceCheck();
						return true;
					} else {
						// your code for move and drag
						switch (event.getAction()) {

							case MotionEvent.ACTION_DOWN:
								initialX = params.x;
								initialY = params.y;
								initialTouchX = event.getRawX();
								initialTouchY = event.getRawY();
								return true;
							case MotionEvent.ACTION_UP:
								return true;

							case MotionEvent.ACTION_MOVE:
								params.x = initialX
										+ (int) (event.getRawX() - initialTouchX);
								params.y = initialY
										+ (int) (event.getRawY() - initialTouchY);
								windowManager.updateViewLayout(widgetHead, params);
								return true;
						}
					}

					return false;
				}
			});

			windowManager.addView(widgetHead, params);

		} else {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;

			params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					LAYOUT_FLAG,
					WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

           /* params = new WindowManager.LayoutParams(
                	WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
                            */


			params.gravity = Gravity.TOP | Gravity.START;
			params.x = 0;
			params.y = 100;

			//this code is for dragging the chat head
			widgetHead.setOnTouchListener(new View.OnTouchListener() {

				private static final String TAG = "WidgetService";
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (gestureDetector.onTouchEvent(event)) {
						Log.e(TAG, "onTouch: ------>"+event );
						// single tap
						stopService(new Intent(WidgetService.this, WidgetService.class));
						callDistanceCheck();
						return true;
					} else {
						// your code for move and drag
						switch (event.getAction()) {

							case MotionEvent.ACTION_DOWN:
								initialX = params.x;
								initialY = params.y;
								initialTouchX = event.getRawX();
								initialTouchY = event.getRawY();
								return true;
							case MotionEvent.ACTION_UP:
								return true;

							case MotionEvent.ACTION_MOVE:
								params.x = initialX
										+ (int) (event.getRawX() - initialTouchX);
								params.y = initialY
										+ (int) (event.getRawY() - initialTouchY);
								windowManager.updateViewLayout(widgetHead, params);
								return true;
						}
					}

					return false;
				}
			});

			windowManager.addView(widgetHead, params);
		}




	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (widgetHead != null)
			windowManager.removeView(widgetHead);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private static class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return true;
		}
	}

	private void callDistanceCheck() {

		if (driverId != null) {
			if (driverDistanceDatabaseRef != null) {
				driverDistanceDatabaseRef.removeEventListener(driverDistanceValueEventListener);
			}
			driverDistanceDatabaseRef = FirebaseDatabase.getInstance().getReference().child("providers").child(driverId);
			driverDistanceDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@SuppressLint("LongLogTag")
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					System.out.println("Inside listoner Dis---***01***state->>>");
					if (dataSnapshot.child("distance").getValue() != null) {
						System.out.println("Inside listoner Dis---1->>>");
						distance = Double.parseDouble(String.valueOf(dataSnapshot.child("distance").getValue()));
						// Log.e("DRIVERId~~distance~~~.", String.valueOf(distance));
						Intent intent = new Intent(WidgetService.this, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("distance", String.valueOf(distance));
						startActivity(intent);
					} else {
						System.out.println("Inside listoner Dis---0->>>");
                      /*  DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("drivers_status").child(driverId).child("distance").setValue("0");*/
						// Log.e("DRIVERId~~distance~~~.", String.valueOf(distance));
						Intent intent = new Intent(WidgetService.this, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("distance", "0");
						startActivity(intent);
					}
				}

				@SuppressLint("LongLogTag")
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.e("getDriver~widget~~~Error~", String.valueOf(databaseError));

					Intent intent = new Intent(WidgetService.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				}
			});

		} else {
			Intent intent = new Intent(WidgetService.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

}