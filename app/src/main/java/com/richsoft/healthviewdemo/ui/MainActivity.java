package com.richsoft.healthviewdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.richsoft.healthviewdemo.R;
import com.richsoft.healthviewdemo.view.HealthView;

public class MainActivity extends AppCompatActivity {
    private HealthView mHealthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHealthView = (HealthView) findViewById(R.id.healthView);

        mHealthView.start(new int[]{1000, 2000, 3345, 4456, 7788, 8877, 12050}, 7890, 7);

        mHealthView.setOnLookClickListener(new HealthView.OnLookClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HelloCj/HealthViewDemo"));
                intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                startActivity(intent);

            }
        });
    }
}
