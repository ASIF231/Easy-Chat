package xyz.asif23.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class verifyOTP extends AppCompatActivity {
    TextView changeNumber;
    EditText getOtp;
    android.widget.Button btnverify;
    String enteredOtp;

    FirebaseAuth auth;
    ProgressBar progAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        changeNumber = findViewById(R.id.changenumber);
        btnverify = findViewById(R.id.btnverify);
        getOtp = findViewById(R.id.rotp);
        progAuth = findViewById(R.id.progressverify);

        auth = FirebaseAuth.getInstance();
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(verifyOTP.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredOtp = getOtp.getText().toString();
                if (enteredOtp.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter Your OTP First", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progAuth.setVisibility(View.VISIBLE);
                    String codereceive = getIntent().getStringExtra("otp");

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codereceive,enteredOtp);

                    signingWithPhoneCredencial(credential);
                    finish();
                }

            }
        });

    }

    private void signingWithPhoneCredencial(PhoneAuthCredential credential)
    {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() )
                {
                    progAuth.setVisibility(View.VISIBLE);
                    Toast.makeText(verifyOTP.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(verifyOTP.this,setprofile.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        progAuth.setVisibility(View.INVISIBLE);
                        Toast.makeText(verifyOTP.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}