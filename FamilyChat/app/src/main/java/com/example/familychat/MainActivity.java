package com.example.familychat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public class PullRequest {
        public String Url;
        public Integer PullRequestId;
        public String Avatar;
        public String Login;
        public String Title;
    }

    ArrayList<PullRequest> pullRequests = new ArrayList<PullRequest>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = "http://familychat.somee.com/notificationHub";

        try {
            HubConnection hubConnection = HubConnectionBuilder
                    .create(url)
                    .build();
            hubConnection.start().blockingAwait();

            hubConnection.send("SendNotificationToAll", "Nahian");

            hubConnection.on("broadcastMessage", (message) -> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("New Message: " + message);
                    }
                });
            }, String.class);

            new HubConnectionTask().execute(hubConnection);
        }catch (Exception e){
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

    public class PullRequestAdapter extends ArrayAdapter<PullRequest> {
        public PullRequestAdapter(Context context, ArrayList<PullRequest> pullRequests){
            super(context, 0, pullRequests);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PullRequest pr = getItem(position);
            if(convertView == null) {
            }
            return convertView;
        }
    }
}