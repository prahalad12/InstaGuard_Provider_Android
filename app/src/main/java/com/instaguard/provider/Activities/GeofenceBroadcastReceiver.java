package com.instaguard.provider.Activities;

import static android.content.Context.MODE_PRIVATE;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_200;
import static com.instaguard.provider.Helpers.Utils.RESPONSECODE_201;
import static java.lang.System.out;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.instaguard.provider.Helpers.Retrofit.APIClient;
import com.instaguard.provider.Helpers.Retrofit.APIInterface;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.Models.BankModel;
import com.instaguard.provider.Models.GeofencePushNotificationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {

        // an Intent broadcast.
        //Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        out.println("~~~~`intent~~~~" + intent);
        //Utils.CUSTOM_INTENT_GEOFENCE

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent != null && geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        if (geofencingEvent != null) {
            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
            if (geofenceList != null) {
                for (Geofence geofence : geofenceList) {
                    Log.d(TAG, "onReceive: " + geofence.getRequestId());
                }
            }

            //Location location = geofencingEvent.getTriggeringLocation();
            int transitionType = geofencingEvent.getGeofenceTransition();

            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    //Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MainActivity.class);
                    callApi(context, "entry");
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    //Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MainActivity.class);
                    //callApi(context,Geofence.GEOFENCE_TRANSITION_DWELL);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    //Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                    //notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MainActivity.class);
                    callApi(context, "exit");
                    break;
            }
        }
    }

    private void callApi(Context context, String geofenceTransitionEnter) {

        String driverId = context.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).getString(Utils.SP_DRIVERID, null);
        String userAccessToken = context.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).getString(Utils.SP_DRIVER_ACCESSTOKEN, null);
        String strBookingId = context.getSharedPreferences(Utils.SP_MAP_TRIPSTATE, MODE_PRIVATE).getString(Utils.SP_CURRENT_BOOKING_ID, "");

        out.println("onReceive: ~~driverId~~" + driverId);
        out.println("onReceive: ~~strBookingId~~" + strBookingId);
        out.println("onReceive: ~~geofenceTransitionEnter~~" + geofenceTransitionEnter);

        if (driverId != null) {
            if (!strBookingId.isEmpty() && !strBookingId.equals("null")) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("driver_id", driverId);
                map.put("booking_id", strBookingId);
                map.put("current_status", geofenceTransitionEnter);

                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<GeofencePushNotificationModel> call2 = apiInterface.doSendPushNotifyGeofence(userAccessToken, map);
                call2.enqueue(new Callback<GeofencePushNotificationModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GeofencePushNotificationModel> call, @NonNull Response<GeofencePushNotificationModel> response) {
                        if (response.code() == RESPONSECODE_200 || response.code() == RESPONSECODE_201) {
                            if (response.body() != null && response.body().getMessage().equals("Push Notification sent successfully")) {
                                System.out.println("~~Push Notification sent successfully~~");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeofencePushNotificationModel> call, @NonNull Throwable t) {
                        call.cancel();
                    }
                });
            }
        }
    }

}
