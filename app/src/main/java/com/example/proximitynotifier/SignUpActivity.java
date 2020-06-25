package com.example.proximitynotifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {


    private Button getOTPButton,signUpButton;
    private EditText nameField,ageField,mobNumField,password1Field,password2Field,otpField;
    private TextInputLayout nameLayout,ageLayout,mobNumLayout,password1Layout,password2Layout,otpLayout;
    private FirebaseAuth mAuth;
    private String otpSentBySystem;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField=findViewById(R.id.editText);
        mobNumField=findViewById(R.id.editText2);
        ageField=findViewById(R.id.editText3);
        password1Field=findViewById(R.id.editText4);
        password2Field=findViewById(R.id.editText5);
        otpField=findViewById(R.id.editText7);
        nameLayout=findViewById(R.id.name_text_input1);
        mobNumLayout=findViewById(R.id.name_text_input2);
        ageLayout=findViewById(R.id.name_text_input3);
        password1Layout=findViewById(R.id.name_text_input4);
        password2Layout=findViewById(R.id.name_text_input5);
        otpLayout=findViewById(R.id.name_text_input6);
        getOTPButton=findViewById(R.id.button5);
        signUpButton=findViewById(R.id.button);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();


        getOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String m=mobNumField.getText().toString();
                if(m.length()!=10)
                {
                    mobNumLayout.setError("Enter valid phone number");
                    mobNumField.requestFocus();
                }
                else
                {
                    sendVerificationCode();
                }

            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                nameField.setText(e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                otpSentBySystem=s;
                //Toast.makeText(getApplicationContext(),otpSentBySystem,Toast.LENGTH_LONG).show();
            }
        };

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n,m,a,p1,p2,o;
                n=nameField.getText().toString();
                m=mobNumField.getText().toString();
                a=ageField.getText().toString();
                p1=password1Field.getText().toString();
                p2=password2Field.getText().toString();
                o=otpField.getText().toString();
                if(n.isEmpty())
                {
                    nameLayout.setError("Name cannot be empty");
                    nameField.requestFocus();
                    return;
                }
                if(m.length()!=10)
                {
                    mobNumLayout.setError("Invalid Mobile Number");
                    mobNumField.requestFocus();
                    return;
                }
                if(a.isEmpty())
                {
                    ageLayout.setError("Invalid age");
                    ageField.requestFocus();
                    return;
                }
                if(p1.isEmpty() || p1.length()<6)
                {
                    password1Layout.setError("Password should be of at least length 6 ");
                    password1Field.requestFocus();
                    return;
                }
                if(p2.isEmpty() || p2.length()<6 || !p1.equals(p2))
                {
                    password2Layout.setError("Enter the same password here");
                    password2Field.requestFocus();

                }
                else
                {
                    verifyOTPandSignUp();

                }

            }
        });


    }


    private void verifyUser(PhoneAuthCredential phoneAuthCredential)
    {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                            String n,m,p1,p2;
                            n=nameField.getText().toString();
                            m=mobNumField.getText().toString();
                            p1=password1Field.getText().toString();
                            MessageDigest digest=null;
                            try {
                                digest=MessageDigest.getInstance("SHA-256");
                            }
                            catch (NoSuchAlgorithmException e)
                            {
                                e.printStackTrace();
                            }
                            assert digest!=null;
                            byte[] pp=digest.digest(p1.getBytes(StandardCharsets.UTF_8));
                            p1= Arrays.toString(pp);
                            final User user=new User(n,m,p1);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(user.getMobile()).exists())
                                    {
                                        Toast.makeText(getApplicationContext(),"User already exists with this mobile number",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else
                                    {
                                        databaseReference.child(user.getMobile()).setValue(user);
                                        Toast.makeText(getApplicationContext(),"SignUp successful",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),"Process cancelled",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                        }

                    }
                });

        mAuth.signOut();
        mAuth.signOut();
    }

    private void verifyOTPandSignUp()
    {
        String codeByUser=otpField.getText().toString();
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(otpSentBySystem,codeByUser);
        verifyUser(phoneAuthCredential);
    }



    private void sendVerificationCode()
    {
        String m=mobNumField.getText().toString();
        m="+91" + m;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(m,60, TimeUnit.SECONDS,SignUpActivity.this,mCallbacks);


    }


}
