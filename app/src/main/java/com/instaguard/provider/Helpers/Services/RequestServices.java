package com.instaguard.provider.Helpers.Services;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.Helpers.Utils;

@SuppressLint("Registered")
public class RequestServices extends Service {
    public static final String MY_SERVICE=".adapter.RequestServices";

    DatabaseReference requstStatus;

    String driverId;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        driverId = prefs.getString(Utils.SP_DRIVERID, null);

//        Log.e("RequestServices~driverId~~~",driverId);

        listenListStatus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.print("my service is destroyed");
    }

    private void listenListStatus(){

        if(driverId!=null){

            requstStatus = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PROVIDER_HEADER).child(driverId).child(Utils.FIREBASE_TRIP_HEADER).child(Utils.FIREBASE_SERVICE_STATUS);

            ValueEventListener handler = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String Status = dataSnapshot.getValue().toString();

                        if (Status.matches("1")) {

                            System.out.println("IS MAPSHOWING?===>" + Utils.MAP_ISSHOWING);
                            System.out.println("Driver ID===>" +driverId);

                            if (!Utils.MAP_ISSHOWING) {

                                if(driverId!=null){

                                    System.out.println("<===== activity started =====> "+driverId);
                                    Intent intent = new Intent(RequestServices.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            requstStatus.addValueEventListener(handler);
        }
    }
}