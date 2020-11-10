package com.chinaappsremover;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.math.BigInteger;

public class Login extends AppCompatActivity {
    TextView register;
    EditText Phone, Password;
    Button loginButton;
    DatabaseReference reff;
    ImageView Eye;
    String phone, pass, S, Cipher, A, new_phone, realPhone = "Null", isFirst;
    int i, count = 1, b;
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.contact_us) {
            openUrl(getResources().getString(R.string.contact_us_url));
            return true;
        } else if (itemId == R.id.privacy_policy) {
            openUrl(getResources().getString(R.string.privacy_policy_url));
            return true;
        } else if (itemId != R.id.rate_us) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            rateus();
            return true;
        }
    }
    private void openUrl(String str) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    private void rateus() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/apps/details?id=" + getPackageName())));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences2 = getSharedPreferences(A,b);
        isFirst = preferences2.getString("isFirst","notFirst");

        //retrieving original and changed phone number from SharedPreferences
        //realPhone = preferences.getString("Phone","Null");
        //new_phone = preferences.getString("NewPhone","Null");
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.register);
        Phone = findViewById(R.id.phone);
        Password = findViewById(R.id.password);
        Eye = findViewById(R.id.eye);
        loginButton = findViewById(R.id.loginButton);
        FirebaseApp.initializeApp(this);

        Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count%2!=0) {
                    Password.setInputType(InputType.TYPE_CLASS_TEXT);
                    Password.setSelection(Password.getText().length());
                    Eye.setImageResource(R.drawable.closed_eye);
                }
                else{
                    Password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Password.setTypeface(Typeface.SANS_SERIF);
                    Password.setSelection(Password.getText().length());
                    Eye.setImageResource(R.drawable.open_eye);
                }
                count++;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                phone = Phone.getText().toString().trim();
                pass = Password.getText().toString().trim();
                if(phone.equals("")){
                    Phone.setError("Must be filled");
                    Phone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                }
                else if(pass.equals("")){
                    Password.setError("Must be filled");
                    Password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                }
                else{
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Logging in...");

                    pd.show();

                    BigInteger hash = BigInteger.valueOf((phone.charAt(0) - '0') + (phone.charAt(2) - '0') + (phone.charAt(4) - '0') + (phone.charAt(6) - '0') + (phone.charAt(8) - '0'));
                    StringBuilder sb = new StringBuilder();
                    char[] letters = pass.toCharArray();

                    for (char ch : letters) {
                        sb.append((byte) ch);
                    }
                    String a = sb.toString();
                    BigInteger temp = new BigInteger(a);
                    hash = temp.multiply(hash);
                    Cipher = String.valueOf(hash);
                    reff = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                String storedPass = dataSnapshot.child("Password").getValue().toString();
                                try {
                                    if (storedPass.equals(Cipher)) {
                                        SharedPreferences.Editor editor1 = getSharedPreferences(S, i).edit();
                                        editor1.putString("Status", "Yes");
                                        editor1.putString("Phone", phone);
                                        editor1.apply();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finishAffinity();
                                    }

                                    else {
                                        Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    if (storedPass.equals(Cipher)) {
                                        SharedPreferences.Editor editor1 = getSharedPreferences(S, i).edit();
                                        editor1.putString("Status", "Yes");
                                        editor1.putString("Phone", phone);
                                        editor1.apply();
                                        startActivity(new Intent(Login.this, Method.class));
                                        finishAffinity();
                                    }

                                    else {
                                        Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (Exception e) {
                                Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                            }
                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Login.this, "There is some error", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }
}
