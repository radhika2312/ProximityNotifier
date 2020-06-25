package com.example.proximitynotifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;


public class AddReminderActivity extends AppCompatActivity {

    private int position;
    private DatabaseReference databaseReference;
    private String mobile;
    private String key;
    private String title,details,place,date,time,latitude,longitude;
    private EditText title_textField,details_textField,place_textField;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ImageView imageView;
    private final int LAUNCH_MAP_ACTIVITY=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        loadPreferences();
        latitude="No";
        longitude="No";
        title_textField=findViewById(R.id.title_textField);
        details_textField=findViewById(R.id.details_textField);
        place_textField=findViewById(R.id.place_textField);
        imageView=findViewById(R.id.location_imageView);
        datePicker=findViewById(R.id.datePicker);
        timePicker=findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        position=getIntent().getIntExtra("Position",-1);
        //Toast.makeText(getApplicationContext(),position + " ",Toast.LENGTH_LONG).show();
        time=String.valueOf(timePicker.getCurrentHour()).toString() +":" + String.valueOf(timePicker.getCurrentMinute()).toString();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time=String.valueOf(hourOfDay).toString() + ":" + String.valueOf(minute).toString();
            }
        });
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(mobile).child("Reminders");
        getKey();
        setData();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Position",position);
                if(position!=-1)
                {
                    intent.putExtra("Longitude",longitude);
                    intent.putExtra("Latitude",latitude);
                }
                startActivityForResult(intent,LAUNCH_MAP_ACTIVITY);

            }
        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LAUNCH_MAP_ACTIVITY)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                assert data != null;
                latitude=data.getStringExtra("latitude");
                longitude=data.getStringExtra("longitude");
                //Toast.makeText(getApplicationContext(),latitude + " " + longitude,Toast.LENGTH_LONG).show();
            }
        }



    }

    private void setData()
    {
        if(position!=-1)
        {
           String title_rem,date_rem,time_rem,longitude_rem,latitude_rem,place_rem,details_rem;
           int hour,min,day,month,year;
           Intent  intent=getIntent();
           title_rem=intent.getStringExtra("Title");
           place_rem=intent.getStringExtra("Place");
           details_rem=intent.getStringExtra("Details");
           longitude=intent.getStringExtra("Longitude");
           latitude=intent.getStringExtra("Latitude");
           date_rem=intent.getStringExtra("Date");
           time_rem=intent.getStringExtra("Time");
           title_textField.setText(title_rem);
           place_textField.setText(place_rem);
           details_textField.setText(details_rem);
           StringTokenizer st=new StringTokenizer(time_rem,":");
           hour=Integer.parseInt(st.nextToken());
           min=0;
           if(st.hasMoreTokens())
           {
               min=Integer.parseInt(st.nextToken());
           }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hour);
                timePicker.setMinute(min);
            }
            StringTokenizer st2=new StringTokenizer(date_rem,"/");
            day=Integer.parseInt(st2.nextToken());
            month=Integer.parseInt(st2.nextToken())-1;
            year=Integer.parseInt(st2.nextToken());
            datePicker.updateDate(year,month,day);

        }

    }

    private void getKey() {
        if (position != -1) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int c = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (c == position) {
                            key = child.getKey();
                        }
                        c++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.okButton:
                writeData();
                break;
            case R.id.deleteButton:
            {

                if(position!=-1)
                {
                    deleteRow(key);
                }
                else
                {
                    returnToFront();
                }


            }

        }
        return super.onOptionsItemSelected(item);
    }
    private void writeData()
    {

        if(latitude.equals("No") || longitude.equals("No"))
        {
            Toast.makeText(getApplicationContext(),"Please select the location",Toast.LENGTH_LONG).show();
            return;
        }
        title=title_textField.getText().toString();
        details=details_textField.getText().toString();
        place=place_textField.getText().toString();
        date=datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() +1)+ "/" + datePicker.getYear();
        if(title.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"One of the fields is missing. Please check",Toast.LENGTH_LONG).show();
            title_textField.setError("Title cannot be empty");
            return;
        }
        if(details.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"One of the fields is missing. Please check",Toast.LENGTH_LONG).show();
            details_textField.setError("Details cannot be empty");
            return;
        }
        if(place.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"One of the fields is missing. Please check",Toast.LENGTH_LONG).show();
            place_textField.setError("Place cannot be empty");
            return;
        }

        ReminderObject m=new ReminderObject(title,date,time,longitude,latitude,place,details);
        if(position==-1) {
            databaseReference.push().setValue(m);
        }
        else
        {
            databaseReference.child(key).setValue(m);

        }
        Intent intent=new Intent(getApplicationContext(),FrontActivity.class);
        startActivity(intent);
        finish();

    }

    private void deleteRow(String key)
    {
        if(key.equals(""))
            return;
        FirebaseDatabase.getInstance().getReference().child("Users").child(mobile).child("Reminders").child(key)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            returnToFront();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"UnSuccessfully deleted",Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }
    private void returnToFront()
    {
        Toast.makeText(getApplicationContext(),"Reminder deleted successfully",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),FrontActivity.class);
        startActivity(intent);
        finish();
    }
}
