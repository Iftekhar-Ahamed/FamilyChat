package com.example.familychat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class ChatActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private boolean chatFragmentAdded = false;
    private TextView otherUsername;
    private ImageView imageView;
    private HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        imageView = findViewById(R.id.profile_pic_image_view);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        // Setup SignalR
        setupSignalR();

        if (savedInstanceState != null) {
            chatFragmentAdded = savedInstanceState.getBoolean("chatFragmentAdded", true);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment(hubConnection))
                    .commit();
            chatFragmentAdded = true;
        }
    }
    private void setupSignalR() {
        String url = "http://familychat.somee.com/notificationHub";

        try {
            hubConnection = HubConnectionBuilder
                    .create(url)
                    .build();
            hubConnection.start().blockingAwait();

            hubConnection.on("broadcastMessage", (message) -> {
                // Handle the received message
                runOnUiThread(() -> {
                    // Notify ChatFragment about the new message
                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (chatFragment != null) {
                        chatFragment.onNewMessage(message);
                    }
                });
            }, String.class);
            new ChatActivity.HubConnectionTask().execute(hubConnection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    class HubConnectionTask extends AsyncTask<HubConnection, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(HubConnection... hubConnections) {
            HubConnection hubConnection = hubConnections[0];
            hubConnection.start().blockingAwait();
            return null;
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("chatFragmentAdded", chatFragmentAdded);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        chatFragmentAdded = savedInstanceState.getBoolean("chatFragmentAdded", true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
