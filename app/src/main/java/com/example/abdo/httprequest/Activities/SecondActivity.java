package com.example.abdo.httprequest.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.abdo.httprequest.R;

public class SecondActivity extends AppCompatActivity {

    TextView nameTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent=getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            String name = intent.getStringExtra(Intent.EXTRA_TEXT);
            nameTV = findViewById(R.id.name_tv);
            nameTV.setText(name);
        }
    }
}
