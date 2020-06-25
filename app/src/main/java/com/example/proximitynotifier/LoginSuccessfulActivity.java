package com.example.proximitynotifier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginSuccessfulActivity extends AppCompatActivity {

    private String mobile;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_successful);
        mobile=getIntent().getStringExtra("Mobile");
        Toast.makeText(getApplicationContext(),mobile,Toast.LENGTH_LONG).show();
        //writeData();
    }
    private void writeData()
    {
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(mobile).child("Reminders");
        ReminderObject m=new ReminderObject("Title","22/07/2001","20:00","2.33","4.66","Motihari","No details");
        databaseReference.push().setValue(m);

    }
}
