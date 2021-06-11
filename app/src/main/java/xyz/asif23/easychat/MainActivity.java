package xyz.asif23.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;
import android.content.*;

public class MainActivity extends AppCompatActivity {
    EditText editphone;
    android.widget.Button sendotp;
    CountryCodePicker ccode;
    String countrycode;
    String Phon;

    FirebaseAuth auth;
    ProgressBar prog;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks call;
    String codesend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ccode = findViewById(R.id.contrycode);
        sendotp = findViewById(R.id.btnsend);
        editphone = findViewById(R.id.phone);
        prog = findViewById(R.id.progmainactv);

        auth=FirebaseAuth.getInstance();

        countrycode = ccode.getSelectedCountryCodeWithPlus();
        ccode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = ccode.getSelectedCountryCodeWithPlus();
            }
        });

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number;

                number = editphone.getText().toString();
                if(number.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if(number.length() <10)
                {
                    Toast.makeText(MainActivity.this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    prog.setVisibility(View.VISIBLE);
                    sendotp.setVisibility(View.INVISIBLE);
                    Phon = countrycode+editphone.getText();

                    Toast.makeText(MainActivity.this, "OTP Send Successfully", Toast.LENGTH_SHORT).show();
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(Phon).setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(call).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });
        call = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
                // automatic fetch otp
            }

            @Override
            public void onVerificationFailed(@NonNull  FirebaseException e) {
                prog.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(MainActivity.this, "OTP is Sent Successfully", Toast.LENGTH_SHORT).show();
                prog.setVisibility(View.INVISIBLE);
                sendotp.setVisibility(View.VISIBLE);
                codesend = s;

                Intent intent = new Intent(MainActivity.this,verifyOTP.class);
                intent.putExtra("otp",codesend);
                startActivity(intent);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Intent intent = new Intent(MainActivity.this,chatActivity.class);
            startActivity(intent);
        }
        }
    }
