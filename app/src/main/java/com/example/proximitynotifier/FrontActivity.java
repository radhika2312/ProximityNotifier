package com.example.proximitynotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FrontActivity extends AppCompatActivity {

    private FloatingActionButton addButton;
    private DatabaseReference databaseReference;
    private String mobile;
    private RecyclerView recyclerView;
    private ArrayList<ReminderObject> reminderObjects;
    private int REQUEST_CODE=12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        loadPreferences();
        reminderObjects=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerView);
        //tempWrite();
        addButton=findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AddReminderActivity.class);
                intent.putExtra("Position",-1);
                startActivity(intent);
            }
        });
        readData();
        startAlarm();
        //Toast.makeText(getApplicationContext(),mobile,Toast.LENGTH_LONG).show();
    }

    private void tempWrite()
    {
        ReminderObject m=new ReminderObject("Title","22/07/2001","20:00","2.33","4.66","Motihari","No Details");

        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(mobile).child("Reminders");
        for(int i=0;i<10;++i)
        {
            databaseReference.push().setValue(m);


        }


    }
    private void loadPreferences()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("usersave", Context.MODE_PRIVATE);
        mobile=sharedPreferences.getString("User","no");
        if(mobile.equals("") || mobile.isEmpty() || mobile.equals("no"))
        {
            mobile="no";
        }
    }
    private void readData()
    {
        if(mobile==null || mobile.equals("no"))
        {
            return;
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(mobile).child("Reminders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot user:snapshot.getChildren())
                {
                    HashMap<String,String > mp=(HashMap<String,String >)user.getValue();
                    assert mp != null;
                    ReminderObject m=new ReminderObject(mp.get("title_rem"),mp.get("date_rem"),mp.get("time_rem"),mp.get("longitude_rem"),mp.get("latitude_rem"),mp.get("place_rem"),mp.get("details_rem"));
                    reminderObjects.add(m);
                }
                MyAdapter myAdapter=new MyAdapter(getApplicationContext(),reminderObjects);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void startAlarm()
    {

        Intent intent=new Intent(getApplicationContext(),MyBroadcastReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),REQUEST_CODE,intent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long time=10*60*1000;
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),time,pendingIntent);
    }
}
