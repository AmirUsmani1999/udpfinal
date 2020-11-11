package com.chinaappsremover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.chinaappsremover.ui.App;

public class Method extends AppCompatActivity {

    Button button;
    Button button2;
    Button button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
        button = (Button) findViewById(R.id.app);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
        button2 = (Button) findViewById(R.id.ocr);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity1();
            }
        });
        button3 = (Button) findViewById(R.id.Barcode);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity2();
            }
        });

    }

    public void openNewActivity() {
        Intent intent = new Intent(Method.this, App.class);
        startActivity(intent);
    }

    public void openNewActivity1() {
        Intent intent = new Intent(Method.this, ocr.class);
        startActivity(intent);
    }
    public void openNewActivity2() {
        Intent intent = new Intent(Method.this, HomeActivity.class);
        startActivity(intent);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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