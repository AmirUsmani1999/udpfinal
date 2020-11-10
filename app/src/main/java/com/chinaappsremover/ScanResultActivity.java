package com.chinaappsremover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanResultActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView countryImg;
    TextView countryText, countryName;
    String barCode;
    int barCodeInt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        countryImg = findViewById(R.id.country_img);
        countryText = findViewById(R.id.country_text);
        countryName = findViewById(R.id.country_name);
        String codeResult = getIntent().getStringExtra("result");
        if(codeResult != null)
        barCode = codeResult.substring(0,3);
        barCodeInt = Integer.parseInt(barCode);
        HelperClass helperClass = new HelperClass(countryImg,countryName,barCodeInt);
        helperClass.updateUi();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), barcode.class));
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
}
