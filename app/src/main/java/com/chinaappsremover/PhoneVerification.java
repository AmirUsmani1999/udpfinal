package com.chinaappsremover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.math.BigInteger;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** This is the activity after filling registration form
 *  For phone verification
 *  Verification is done by sending an OTP on given number and verifying the OTP
 */

public class PhoneVerification extends AppCompatActivity implements View.OnClickListener {

    EditText etOtp;
    Button btResendOtp, btVerifyOtp;
    private FirebaseAuth mAuth;
    String name, password, phone, username, dob, Cipher, mVerificationId, S, encryptedUsername;
    int i;
    Intent intent;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        initFields();
        intent = getIntent();
        phone = intent.getStringExtra("phone");
        username = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        dob = intent.getStringExtra("dob");
        password = intent.getStringExtra("password");
        mResendToken = intent.getParcelableExtra("mResendToken");
        mVerificationId = intent.getStringExtra("verificationId");

        mAuth = FirebaseAuth.getInstance();
        encryptedUsername = encryptUsername(username).toString();
        BigInteger hash = BigInteger.valueOf((phone.charAt(0) - '0') + (phone.charAt(2) - '0') + (phone.charAt(4) - '0') + (phone.charAt(6) - '0') + (phone.charAt(8) - '0'));
        StringBuilder sb = new StringBuilder();
        char[] letters = password.toCharArray();
        for (char ch : letters) {
            sb.append((byte) ch);
        }
        String a = sb.toString();
        BigInteger i = new BigInteger(a);
        hash = i.multiply(hash);
        Cipher = String.valueOf(hash);
        Firebase.setAndroidContext(this);
        //btVerifyOtp.setBackgroundResource(R.drawable.button);
        //btResendOtp.setBackgroundResource(R.drawable.button);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                btResendOtp.setText("You can resend OTP after " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                btResendOtp.setText("Resend OTP");
            }

        }.start();
    }

    void initFields() {
        etOtp = findViewById(R.id.et_otp);
        btResendOtp = findViewById(R.id.bt_resend_otp);
        btVerifyOtp = findViewById(R.id.bt_verify_otp);
        btResendOtp.setOnClickListener(this);
        btVerifyOtp.setOnClickListener(this);
        btResendOtp.setEnabled(false);
        buttonEnable();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_resend_otp:
                initFireBaseCallbacks();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phone,
                        1,
                        TimeUnit.MINUTES,
                        this,
                        mCallbacks,
                        mResendToken);
                Toast.makeText(PhoneVerification.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                btResendOtp.setEnabled(false);
                buttonEnable();
                break;
            case R.id.bt_verify_otp:
                if (etOtp.getText().toString().equals("")) {
                    etOtp.setError("Must be filled");
                } else {
                    final ProgressDialog pd = new ProgressDialog(PhoneVerification.this);
                    pd.setMessage("Verifying...");
                    pd.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etOtp.getText().toString());
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferences.Editor editor1 = getSharedPreferences(S, i).edit();
                                        editor1.putString("Phone", phone);
                                        editor1.putString("NewPhone", phone);
                                        editor1.apply();
                                        Firebase reference = new Firebase("https://updfinal.firebaseio.com/users");
                                        reference.child(phone).child("Username").setValue(encryptedUsername);
                                        reference.child(phone).child("Password").setValue(Cipher);
                                        reference.child(phone).child("Name").setValue(name);
                                        reference.child(phone).child("DOB").setValue(dob);
                                        reference.child(phone).child("Phone").setValue(phone);

                                        //sendNotification("Registration Successful", "Welcome to Smart Chat");
                                        Toast.makeText(PhoneVerification.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor editor2 = getSharedPreferences(S, i).edit();
                                        editor2.putString("Status", "Yes");
                                        editor2.apply();
                                        Intent verificationIntent = new Intent(PhoneVerification.this, MainActivity.class);
                                        verificationIntent.putExtra("userphone", phone);
                                        startActivity(verificationIntent);
                                        finishAffinity();
                                    } else {
                                        Toast.makeText(PhoneVerification.this, "Verification Failed, Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                    pd.dismiss();
                                }
                            });
                }
                break;
        }
    }

    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

            }
        };
    }

    public void buttonEnable() {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(new Runnable() {

            @Override
            public void run() {

                PhoneVerification.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btResendOtp.setEnabled(true);
                    }
                });
            }
        }, 1, TimeUnit.MINUTES);
    }

    public String encryptUsername(String uname) {
        // Encryption logic
        return uname;
    }
}
