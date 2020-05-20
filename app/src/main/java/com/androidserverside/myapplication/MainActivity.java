package com.androidserverside.myapplication;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {
    NotificationManager manager;
    final int NOTIF_ID = 1;
    final String Restaurants_Fragment = "Restaurants_Fragment";
    final String SPORTNEWS_FRAGMENT = "SportNews_Fragment";
    Toolbar toolbar;
    android.support.v7.app.ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Spinner TimeOptionsSpinner;
    boolean SportNotificationIsEnable = false;
    boolean ResturantNotificationIsEnable = false;
    int NotificationFrequency = 1;
    AlarmManager alarmManager;

    String sportChannel = null;
    String resturantChannel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("NotifParameters", Context.MODE_PRIVATE);
        SportNotificationIsEnable = sharedPref.getBoolean("isSportIsOn",false);
        ResturantNotificationIsEnable = sharedPref.getBoolean("isResturantIsOn",false);

        drawerLayout = findViewById(R.id.main_drawlayout);
        navigationView = findViewById(R.id.Navigation_View);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        sportChannel = null;
        resturantChannel = null;
        if(Build.VERSION.SDK_INT >= 26){
            int Importance = manager.IMPORTANCE_HIGH;

            sportChannel ="sport_notifications";
            CharSequence SportChannelName ="Sport Channel";
            NotificationChannel SportNotificationChannel = new NotificationChannel(sportChannel, SportChannelName, Importance);
            SportNotificationChannel.enableLights(true);
            SportNotificationChannel.setLightColor(Color.BLUE);
            SportNotificationChannel.enableVibration(true);

            resturantChannel ="resturant_notifications";
            CharSequence ResturantChannelName ="Resturant Channel";
            NotificationChannel ResturantNotificationChannel = new NotificationChannel(resturantChannel, ResturantChannelName, Importance);
            ResturantNotificationChannel.enableLights(true);
            ResturantNotificationChannel.setLightColor(Color.BLUE);
            ResturantNotificationChannel.enableVibration(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.menu_notification) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.notification_setting_dialog);
                    TimeOptionsSpinner = dialog.findViewById(R.id.time_spinner);
                    final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.time_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    TimeOptionsSpinner.setAdapter(adapter);
                    Button NotificationSaveBtn = dialog.findViewById(R.id.NotificationSettings_SaveBtn);
                    final Switch SportNotificationsSwitch = dialog.findViewById(R.id.Sport_Switch);
                    final Switch ResturantsNotificationsSwitch = dialog.findViewById(R.id.Resturant_Switch);

                    SharedPreferences sharedPref = getSharedPreferences("NotifParameters", Context.MODE_PRIVATE);
                    SportNotificationsSwitch.setChecked(sharedPref.getBoolean("isSportIsOn",false));
                    SportNotificationsSwitch.setShowText(sharedPref.getBoolean("isSportIsOn",false));
                    ResturantsNotificationsSwitch.setChecked(sharedPref.getBoolean("isResturantIsOn",false));
                    ResturantsNotificationsSwitch.setShowText(sharedPref.getBoolean("isResturantIsOn",false));

                    switch(sharedPref.getInt("Frequency",1)){
                        case 1:
                            TimeOptionsSpinner.setSelection(0);
                            break;
                        case 15:
                            TimeOptionsSpinner.setSelection(1);
                            break;
                        case 30:
                            TimeOptionsSpinner.setSelection(2);
                            break;
                        case 45:
                            TimeOptionsSpinner.setSelection(3);
                            break;
                        case 60:
                            TimeOptionsSpinner.setSelection(4);
                            break;
                    }

                    NotificationSaveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SharedPreferences sharedPref = getSharedPreferences("NotifParameters", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("isSportIsOn", SportNotificationIsEnable);
                            editor.putBoolean("isResturantIsOn", ResturantNotificationIsEnable);
                            editor.putInt("Frequency", NotificationFrequency);
                            editor.commit();
                            if(SportNotificationIsEnable || ResturantNotificationIsEnable){
                                Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
                                intent.putExtra("SportNotificationIsEnable",SportNotificationIsEnable);
                                intent.putExtra("ResturantNotificationIsEnable",ResturantNotificationIsEnable);
                                intent.putExtra("NotificationFrequency",NotificationFrequency);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                alarmManager.cancel(pendingIntent);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + NotificationFrequency * 1000 *20, pendingIntent);
                            }else{
                                Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                alarmManager.cancel(pendingIntent);
                            }
                        }
                    });


                    TimeOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            NotificationFrequency = Integer.parseInt(TimeOptionsSpinner.getSelectedItem().toString());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    SportNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                SportNotificationIsEnable = true;
                            } else {
                                SportNotificationIsEnable = false;
                            }
                        }
                    });
                    ResturantsNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ResturantNotificationIsEnable = true;
                            } else {
                                ResturantNotificationIsEnable = false;
                            }
                        }
                    });
                    dialog.show();
                }
                return      false;
            }
        });
        if(SportNotificationIsEnable || ResturantNotificationIsEnable){
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("SportNotificationIsEnable",SportNotificationIsEnable);
            intent.putExtra("ResturantNotificationIsEnable",ResturantNotificationIsEnable);
            intent.putExtra("NotificationFrequency",NotificationFrequency);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + NotificationFrequency * 1000 * 20, pendingIntent);
            }
        }else{
            Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.sportNews_fragment, new SportsFragment(), SPORTNEWS_FRAGMENT);
        fragmentTransaction.add(R.id.Resturant_Fragment, new RestaurantFragment(), Restaurants_Fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(Gravity.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        SharedPreferences sharedPref = getSharedPreferences("NotifParameters", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isSportIsOn", SportNotificationIsEnable);
        editor.putBoolean("isResturantIsOn", ResturantNotificationIsEnable);
        editor.putInt("Frequency", NotificationFrequency);
        editor.commit();
        super.onPause();
    }


}
