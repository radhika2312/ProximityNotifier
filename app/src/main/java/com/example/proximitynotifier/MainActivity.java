package com.example.proximitynotifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private Button loginButton;
    private Button signUpButton;
    private TextInputLayout textInputLayout1,textInputLayout2;
    private EditText mobNumber,password;
    private DatabaseReference databaseReference;
    private String mobileNumber,pass,loggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPreferences();
        if(!loggedIn.equals("no"))
        {
            Intent intent=new Intent(getApplicationContext(), FrontActivity.class);
            intent.putExtra("Mobile",loggedIn);
            startActivity(intent);
            finish();
        }
        loginButton=findViewById(R.id.login);
        signUpButton=findViewById(R.id.signUp);
        mobNumber=findViewById(R.id.mobNumber);
        password=findViewById(R.id.password);
        textInputLayout1=findViewById(R.id.name_text_input1);
        Objects.requireNonNull(textInputLayout1.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0)
                {
                    textInputLayout1.setError("Mobile Number cannot be empty");
                }
                else if(s.length()!=10)
                {
                    textInputLayout1.setError("Enter valid mobile number");
                }
                else
                {
                    textInputLayout1.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textInputLayout2=findViewById(R.id.name_text_input2);
        Objects.requireNonNull(textInputLayout2.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0)
                {
                    textInputLayout2.setError("Enter valid password");
                }
                else
                {
                    textInputLayout2.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();

            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login()
    {
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String mob=mobNumber.getText().toString();
                String pas=password.getText().toString();
                MessageDigest digest=null;
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                assert digest != null;
                byte[] pasd = digest.digest(pas.getBytes(StandardCharsets.UTF_8));
                pas= Arrays.toString(pasd);
                if(dataSnapshot.child(mob).exists())
                {
                    if(!mob.isEmpty())
                    {
                        User user=dataSnapshot.child(mob).getValue(User.class);
                        assert user != null;
                        if(user.getPwd().equals(pas))
                        {
                            clearUser();
                            saveUser();
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(),FrontActivity.class);
                            intent.putExtra("Mobile",mob);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Username",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"User Not Found ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_LONG).show();

            }
        });

    }

    private void startLogin() {
        mobileNumber=mobNumber.getText().toString();
        pass=password.getText().toString();
        /*if(mobileNumber.length()!=10)
        {
            textInputLayout1.setError("Enter valid mobile number");
            mobNumber.requestFocus();
            return;
        }*/
        if(pass.length()==0)
        {
            textInputLayout2.setError("Enter valid password");
            password.requestFocus();
            return;
        }
        login();
    }



    private void loadPreferences()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("usersave", Context.MODE_PRIVATE);
        loggedIn=sharedPreferences.getString("User","no");
        if(loggedIn.equals("") || loggedIn.isEmpty() || loggedIn.equals("no"))
        {
            loggedIn="no";
        }
    }
    private void clearUser()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("usersave",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
    private void saveUser()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("usersave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("User",mobileNumber);
        editor.apply();
    }
}
