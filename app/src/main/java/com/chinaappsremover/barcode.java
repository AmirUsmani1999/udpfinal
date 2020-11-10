package com.chinaappsremover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Objects;

public class barcode extends AppCompatActivity {
    Button scanBtn, searchBtn;
    EditText barCodeInput;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        scanBtn = findViewById(R.id.scan_btn);
        barCodeInput = findViewById(R.id.bar_code_input);
        searchBtn = findViewById(R.id.search_btn);
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},1);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                String codeInput = barCodeInput.getText().toString();
                if(codeInput.trim().equals("")){
                    Toast.makeText(barcode.this, "Enter a valid Bar Code", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (codeInput.length() == 3 || codeInput.length() == 8 || codeInput.length() == 13) {
                        Intent intent = new Intent(getApplicationContext(), ScanResultActivity.class);
                        intent.putExtra("result", codeInput);
                        startActivity(intent);
                    } else {
                        Toast.makeText(barcode.this, "Enter a valid Bar Code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).
                getRootView().getWindowToken(),0);
    }
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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}
