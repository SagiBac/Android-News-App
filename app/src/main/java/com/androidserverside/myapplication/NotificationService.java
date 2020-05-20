package com.androidserverside.myapplication;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends Service {
    private GoogleAPIService googleAPIService;
    private AlarmManager alarmManager;
    private NotificationManager notificationManager;
    private LocationManager locationManager;
    private RequestQueue queue;

    final int NOTIFICATION_ID = 1;
    //final int LOCATION_PERMISSION_CODE = 1;
    final String TAG = "SPORT - ERROR";
    final String SPORTNEWS_LINK = "https://newsapi.org/v2/top-headlines?country=il&category=sports&apiKey=ae6f7ce5700c4b3196f3aa546c68f74c";
    private int Frequency;
    private double latitude,longitude;
    private String FullApiURL;
    private String SportchannelId = null;
    private String ResturantchannelId = null;
    private boolean SportNotificationIsOn,ResturantNotificationIsOn;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        googleAPIService = new GoogleAPIService();
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        SharedPreferences sharedPref = getSharedPreferences("NotifParameters", Context.MODE_PRIVATE);
        SportNotificationIsOn = sharedPref.getBoolean("isSportIsOn",false);
        ResturantNotificationIsOn = sharedPref.getBoolean("isResturantIsOn",false);
        Frequency = sharedPref.getInt("Frequency",1);
        final NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        SportchannelId = null;
        ResturantchannelId = null;
        if (Build.VERSION.SDK_INT >= 26) {
            if (SportNotificationIsOn) {
                SportchannelId = "SportChannel";
                CharSequence SportchannelName = "Sport channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel SportnotificationChannel = new NotificationChannel(SportchannelId, SportchannelName, importance);
                SportnotificationChannel.enableLights(true);
                SportnotificationChannel.setLightColor(Color.RED);
                SportnotificationChannel.enableVibration(true);
                SportnotificationChannel.setVibrationPattern(new long[]{
                        100,
                        200
                });
                notificationManager.createNotificationChannel(SportnotificationChannel);
            }

            if (ResturantNotificationIsOn) {
                ResturantchannelId = "ResturantChannel";
                CharSequence ResturantchannelName = "Resturant Channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel ResturantNotificationChannel = new NotificationChannel(ResturantchannelId, ResturantchannelName, importance);
                ResturantNotificationChannel.enableLights(true);
                ResturantNotificationChannel.setLightColor(Color.RED);
                ResturantNotificationChannel.enableVibration(true);
                ResturantNotificationChannel.setVibrationPattern(new long[]{
                        100,
                        200
                });
                notificationManager.createNotificationChannel(ResturantNotificationChannel);
            }
        }

        if(ResturantNotificationIsOn){

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    AskForResturantsAfterGettingLocation(NotificationService.this,intent,Frequency);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // do something
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // notify user "GPS or Network provider" not available
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 500, locationListener);

            AskForResturantsAfterGettingLocation(this,intent,Frequency);
        }


        if (SportNotificationIsOn) {
            queue = Volley.newRequestQueue(this.getApplicationContext());
            final StringRequest request = new StringRequest(SPORTNEWS_LINK, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject rootObject = new JSONObject(response);
                        JSONArray results = rootObject.getJSONArray("articles");
                        JSONObject currentObject = results.getJSONObject(0);
                        final String title = currentObject.getString("title");
                        final String urlToArticle = currentObject.getString("url");
                        final String description = currentObject.getString("description");
                        final String urlToImage = currentObject.getString("urlToImage");
                        final Sports currentSports = new Sports();
                        currentSports.setTitle(title);
                        currentSports.setDescription(description);
                        currentSports.setUrlToArticle(urlToArticle);



                        Intent activityIntent = new Intent(NotificationService.this, MainActivity.class);
                        PendingIntent pendingIntentActivity = PendingIntent.getActivity(NotificationService.this, 0, activityIntent, 0);

                        /**/
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, SportchannelId)
                                .setSmallIcon(R.drawable.news_icon)
                                .setContentText(description)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .setBigContentTitle(title))
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setContentIntent(pendingIntentActivity)
                                .setAutoCancel(true);

                        /**/

                        Notification notification = builder.build();

                        notification.defaults = Notification.DEFAULT_ALL;
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;


                        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        notificationManager.notify(NOTIFICATION_ID, notification);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Frequency * 1000 * 60, pendingIntent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "IDAN - ERROR");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError) {
                        Log.e(TAG, "Update Location Request timed out.");
                    } else if (error instanceof NoConnectionError) {
                        Log.e(TAG, "Update Location no connection.");
                    } else if (error instanceof AuthFailureError) {
                        Log.e(TAG, "Auth failure");
                    } else if (error instanceof ServerError) {
                        Log.e(TAG, "Server Error");
                    } else if (error instanceof NetworkError) {
                        Log.e(TAG, "Network Error");
                    } else if (error instanceof ParseError) {
                        Log.e(TAG, "Parse Error");
                    }
                }
            });
            queue.add(request);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    void AskForResturantsAfterGettingLocation(final Context context , final Intent intent , final int ReaptingTime){
        String LocationCall = "?location=" + latitude + "," + longitude;
        String Radius = "&radius=1500";
        String Type = "&type=restaurant";
        String APIKEY = "&key=" + googleAPIService.getGOOGLE_API_KEY();
        FullApiURL = googleAPIService.getAPI_ROOT() + LocationCall + Radius + Type + APIKEY;

        queue = Volley.newRequestQueue(context.getApplicationContext());
        final StringRequest request = new StringRequest(FullApiURL, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject rootObject = new JSONObject(response);

                    JSONLocationDetails jsonLocationDetails = new JSONLocationDetails(rootObject);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent activityIntent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntentActivity = PendingIntent.getActivity(context, 0, activityIntent, 0);

                    /**/
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ResturantchannelId)
                            .setSmallIcon(R.drawable.news_icon)
                            .setContentText("the rating of the resturant is: " + jsonLocationDetails.getM_Rate())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(jsonLocationDetails.getM_Name()))
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pendingIntentActivity)
                            .setAutoCancel(true);

                    /**/

                    Notification notification = builder.build();

                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationManager.notify(NOTIFICATION_ID, notification);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ReaptingTime * 1000 * 60, pendingIntent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.e(TAG,"Update Location Request timed out.");
                }else if (error instanceof NoConnectionError){
                    Log.e(TAG,"Update Location no connection.");
                } else if (error instanceof AuthFailureError) {
                    Log.e(TAG,"Auth failure");
                } else if (error instanceof ServerError) {
                    Log.e(TAG,"Server Error");
                } else if (error instanceof NetworkError) {
                    Log.e(TAG,"Network Error");
                } else if (error instanceof ParseError) {
                    Log.e(TAG,"Parse Error");
                }
            }
        });
        queue.add(request);
    }



}
