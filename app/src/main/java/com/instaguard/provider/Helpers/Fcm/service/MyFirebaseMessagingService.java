package com.instaguard.provider.Helpers.Fcm.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.instaguard.provider.Activities.LaunchActivity;
import com.instaguard.provider.Activities.MainActivity;
import com.instaguard.provider.BuildConfig;
import com.instaguard.provider.Helpers.Fcm.Util.NotificationUtils;
import com.instaguard.provider.Helpers.SoundPoolManager;
import com.instaguard.provider.Helpers.Utils;
import com.instaguard.provider.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twilio.voice.CallInvite;
import com.twilio.voice.CancelledCallInvite;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final int THUMBNAIL_SIZE = 450;

    private NotificationUtils notificationUtils;
    private static int count = 0;
    String strDataLis, strImage, strTitle, strMessage;
    int strstatus;

    private static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    private static final String CALL_SID_KEY = "CALL_SID";
    private static final String VOICE_CHANNEL = "default";
    String strTwilios;
    private NotificationManager notificationManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);
        Log.e(TAG, "onNewToken: " + s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences.Editor getState = getApplicationContext().getSharedPreferences(Utils.GLOBAL_STORED_ID, MODE_PRIVATE).edit();
        getState.putString(Utils.SP_DEVICE_ID, token);
        getState.apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, "onMessageReceived: ~~~~~~~~~~~~~~~~" + remoteMessage.getData().toString());
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null)
            getImage(remoteMessage);

    }


    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            sendNotification(bitmap, "image");
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.e(TAG, "onBitmapFailed: ~~~~~~~~~~" + e.toString());
        }


        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendNotification(Bitmap bitmap, String key) {
        try {
            if (bitmap == null) {

                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent;
                try {
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "101";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant")
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

                    //Configure Notification Channel
                    notificationChannel.setDescription("Notifications");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(strTitle)
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setContentText(strMessage)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_EVENT);

                notificationManager.notify(1, notificationBuilder.build());


            } else {

                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
                style.bigPicture(bitmap);


                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent;
                try {
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                //pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "101";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant")
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

                    //Configure Notification Channel
                    notificationChannel.setDescription("Notifications");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);

                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(strTitle)
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setContentText(strMessage)
                        .setContentIntent(pendingIntent)

                        .setStyle(style)
                        .setLargeIcon(bitmap)

                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_MAX);


                notificationManager.notify(1, notificationBuilder.build());

            }
        } catch (/*IllegalArgumentException |*/ Exception e) {
            e.printStackTrace();
        }
    }

    private void getImage(final RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        if (BuildConfig.DEBUG) Log.e(TAG, "getImage: ~~~~~~~~~~~~~~~~" + data);

        //String strData = data.get("{a_data=my_data}");
        //String strDatas = data.get("{a_data=pn_city, body=123, image=, title=Test}");

        strDataLis = data.get("a_data");
        strTwilios = data.get("twi_account_sid");
        System.out.println("str data==>" + strDataLis + "--" + strTwilios);

        if (data.get("status")!=null && !String.valueOf(data.get("status")).equals("")&& !String.valueOf(data.get("status")).equals("null")) {
            strstatus = Integer.parseInt(Objects.requireNonNull(data.get("status")));
            /*SharedPreferences.Editor getState = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
            getState.putInt(Utils.Notification_Unread, strstatus);
            getState.apply();*/
            System.out.println("notification Checking " + strstatus);
        }

        if (strDataLis != null) {

            if (strDataLis.equals("pn_city")) {

                strImage = data.get("image");
                strTitle = data.get("title");
                strMessage = data.get("body");

                if (strImage != null && !strImage.equals("") && !strImage.isEmpty()) {
//                    Log.e(TAG, "getImage: ~~~~~~~~~~if~~~~~~~~~~~");
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Get image from data Notification
                            Picasso.get()
                                    .load(strImage)
                                    .into(target);
                        }
                    });
                } else {
                    Bitmap bitmap = null;
                    sendNotification(bitmap, "noimage");

                }
            } else {

                strTitle = remoteMessage.getNotification().getTitle();
                strMessage = remoteMessage.getNotification().getBody();

                if (BuildConfig.DEBUG) Log.e(TAG, "getNotification: ~~~~~~~~~~~~~~~~" + strTitle);
                if (BuildConfig.DEBUG) Log.e(TAG, "getNotification: ~~~~~~~~~~~~~~~~" + strMessage);

                Bitmap bitmap = null;
                sendNotification(bitmap, "noimage");
            }
        } else if (strTwilios != null) {
            //{twi_account_sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx,
            // twi_to=client:Hyride_driver113, twi_answer_timeout=-1,
            // twi_message_type=twilio.voice.call,
            // twi_call_sid=CAxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx,
            // twi_message_id=RUxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx, twi_from=client:Hyride_user6}
            Log.e(TAG, "NavigationDrawerData Payload: " + remoteMessage.getData().toString());
            try {

                Map<String, String> datas = remoteMessage.getData();
                final int notificationId = (int) System.currentTimeMillis();

                boolean valid = Voice.handleMessage(remoteMessage.getData(), new MessageListener() {
                    @Override
                    public void onCallInvite(@NonNull CallInvite callInvite) {
                        final int notificationId = (int) System.currentTimeMillis();
                        MyFirebaseMessagingService.this.notify(callInvite, notificationId);
                        MyFirebaseMessagingService.this.sendCallInviteToActivity(callInvite, notificationId);
                    }

                    @Override
                    public void onCancelledCallInvite(@NonNull CancelledCallInvite cancelledCallInvite) {
                        MyFirebaseMessagingService.this.cancelNotification(cancelledCallInvite);
                        MyFirebaseMessagingService.this.sendCancelledCallInviteToActivity(
                                cancelledCallInvite);
                    }
                });
                if (!valid) {
                    Log.e(TAG, "The message was not a valid Twilio Voice SDK payload: " +
                            remoteMessage.getData());
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        } else {

            strTitle = remoteMessage.getNotification().getTitle();
            strMessage = remoteMessage.getNotification().getBody();

            if (BuildConfig.DEBUG) Log.e(TAG, "getNotification: ~~~~~~~~~~~~~~~~" + strTitle);
            if (BuildConfig.DEBUG) Log.e(TAG, "getNotification: ~~~~~~~~~~~~~~~~" + strMessage);

            Bitmap bitmap = null;
            sendNotification(bitmap, "noimage");
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    //For Android Version Orio and greater than orio.
    private void notify(CallInvite callInvite, int notificationId) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_INCOMING_CALL);
        intent.putExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(MainActivity.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /*@SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);*/

        PendingIntent pendingIntent;
        try {
            pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                            | PendingIntent.FLAG_IMMUTABLE);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            pendingIntent =
                    PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }

        /*
         * Pass the notification id and call sid to use as an identifier to cancel the
         * notification later
         */
        Bundle extras = new Bundle();
        extras.putInt(NOTIFICATION_ID_KEY, notificationId);
        extras.putString(CALL_SID_KEY, callInvite.getCallSid());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel callInviteChannel = new NotificationChannel(VOICE_CHANNEL,
                    "Primary Voice Channel", NotificationManager.IMPORTANCE_HIGH);
            callInviteChannel.setLightColor(Color.GREEN);
            callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(callInviteChannel);

            Notification notification =
                    buildNotification(getString(R.string.vimo_customer) + " " + getString(R.string.is_calling),
                            pendingIntent,
                            extras);
            notificationManager.notify(notificationId, notification);
        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.vimo_customer) + " " + getString(R.string.is_calling))
                            /*.setContentText(callInvite.getFrom() + " is calling.")*/
                            .setAutoCancel(true)
                            .setExtras(extras)
                            .setContentIntent(pendingIntent)
                            .setGroup("vimo_app_notification")
                            .setColor(Color.rgb(214, 10, 37));

            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    private void cancelNotification(CancelledCallInvite cancelledCallInvite) {
        SoundPoolManager.getInstance((this)).stopRinging();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            /*
             * If the incoming call was cancelled then remove the notification by matching
             * it with the call sid from the list of notifications in the notification drawer.
             */
            StatusBarNotification[] activeNotifications =
                    notificationManager.getActiveNotifications();
            for (StatusBarNotification statusBarNotification : activeNotifications) {
                Notification notification = statusBarNotification.getNotification();
                Bundle extras = notification.extras;
                String notificationCallSid = extras.getString(CALL_SID_KEY);

                if (cancelledCallInvite.getCallSid().equals(notificationCallSid)) {
                    notificationManager.cancel(extras.getInt(NOTIFICATION_ID_KEY));
                }
            }
        } else {
            /*
             * Prior to Android M the notification manager did not provide a list of
             * active notifications so we lazily clear all the notifications when
             * receiving a CancelledCallInvite.
             *
             * In order to properly cancel a notification using
             * NotificationManager.cancel(notificationId) we should store the call sid &
             * notification id of any incoming calls using shared preferences or some other form
             * of persistent storage.
             */
            notificationManager.cancelAll();
        }
    }

    /*
     * Send the CallInvite to the VoiceActivity. Start the activity if it is not running already.
     */
    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_INCOMING_CALL);
        intent.putExtra(MainActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(MainActivity.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    /*
     * Send the CancelledCallInvite to the VoiceActivity
     */
    private void sendCancelledCallInviteToActivity(CancelledCallInvite cancelledCallInvite) {
        Intent intent = new Intent(MainActivity.ACTION_CANCEL_CALL);
        intent.putExtra(MainActivity.CANCELLED_CALL_INVITE, cancelledCallInvite);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Build a notification.
     *
     * @param text          the text of the notification
     * @param pendingIntent the body, pending intent for the notification
     * @param extras        extras passed with the notification
     * @return the builder
     */
    @TargetApi(Build.VERSION_CODES.O)
    public Notification buildNotification(String text, PendingIntent pendingIntent, Bundle extras) {
        return new Notification.Builder(getApplicationContext(), VOICE_CHANNEL)
                .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setExtras(extras)
                .setAutoCancel(true)
                .build();
    }
}