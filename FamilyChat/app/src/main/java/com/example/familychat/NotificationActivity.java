package com.example.familychat;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_notification);
        textView = findViewById(R.id.notificationTextView);
        String data = getIntent().getStringExtra(  "msg");
        textView.setText(data);
    }

}
