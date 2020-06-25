package com.example.proximitynotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private DatabaseReference databaseReference;
    private String mobile;
    private Context context;
    private ArrayList<ReminderObject> reminderObjects;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private final static String default_notification_channel_id = "default" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        loadPreferences();
        mediaPlayer=MediaPlayer.create(context,R.raw.bella_ciao);
        currentLocation=null;
        reminderObjects=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(mobile).child("Reminders");
        getLocation();
        //Toast.makeText(context,mobile,Toast.LENGTH_LONG).show();
        readData();
    }
    private void loadPreferences()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("usersave", Context.MODE_PRIVATE);
        mobile=sharedPreferences.getString("User","no");
        if(mobile.equals("") || mobile.isEmpty() || mobile.equals("no"))
        {
            mobile="no";
        }
    }
    private void readData()
    {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int c=0;
                for(DataSnapshot user:snapshot.getChildren()) {
                    HashMap<String, String> mp = (HashMap<String, String>) user.getValue();
                    String ankit=mp.get("longitude_rem");
                    assert ankit != null;
                    double lon=Double.parseDouble(ankit);
                    String rishu=mp.get("latitude_rem");
                    assert rishu != null;
                    double lat=Double.parseDouble(rishu);
                    double dis=distanceBetweenCoordinates(currentLocation.getLatitude(),currentLocation.getLongitude(),lat,lon);
                    assert mp != null;
                    String date_rem=mp.get("date_rem");
                    String time_rem=mp.get("time_rem");
                    date_rem=date_rem + " " + time_rem;
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm") ;
                    long cur=System.currentTimeMillis();
                    try {
                        Date date=sdf.parse(date_rem);
                        assert date != null;
                        long time=date.getTime();
                        long dif=time-cur;
                        /*if(dif<0)
                        {
                            String key=user.getKey();
                            assert key != null;
                            deleteRow(key);
                        }*/
                        long cmp=11*60*1000;
                        long comp=60*60*1000;
                        if((dif<cmp && dif>0) || (dis<100 && dif < comp && dif>0))
                        {
                            showNotification(mp.get("title_rem"),mp.get("details_rem"),c);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    c++;

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getLocation()
    {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if(location!=null)
                    {
                        currentLocation=location;
                    }

                });
    }


    private void deleteRow(String key)
    {
        if(key.equals(""))
            return;
        FirebaseDatabase.getInstance().getReference().child("Users").child(mobile).child("Reminders").child(key)
                .removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                       Toast.makeText(context,"Reminder deleted as its time was up",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context,"UnSuccessfully deleted",Toast.LENGTH_LONG).show();

                    }

                });

    }
    private double distanceBetweenCoordinates(double lat1, double long1, double lat2, double long2) {

        double _eQuatorialEarthRadius = 6378.1370D;
        double _d2r = (Math.PI / 180D);
        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double d = _eQuatorialEarthRadius * c;

        return (d*1000);
    }



    private void showNotification(String title,String details,int position) {
        mediaPlayer.start();
        Notification notification=getNotification(title,details,position);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel) ;
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        assert notificationManager != null;
        notificationManager.notify(1 , notification) ;
    }

    private Notification getNotification (String title,String content,int position) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, default_notification_channel_id ) ;
        Intent intent=new Intent(context,AddReminderActivity.class);
        intent.putExtra("Position",position);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle( title ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground) ;
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }



}

