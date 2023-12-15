package com.example.familychat.model;

import android.content.Context;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SignalRManager {
    private static final String HUB_URL = "http://familychat.somee.com/notificationHub";
    private static HubConnection hubConnection;

    private SignalRManager() {
        // private constructor to prevent instantiation
    }

    public static void initialize(Context context) {
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder.create(HUB_URL).build();

            // Additional configuration or setup if needed

            // Start the connection
            hubConnection.start().blockingAwait();

            // Handle connection events or send initial messages if necessary

            Toast.makeText(context, "SignalR connection initialized", Toast.LENGTH_SHORT).show();
        }
    }

    public static HubConnection getHubConnection() {
        return hubConnection;
    }

    public static void stopConnection() {
        if (hubConnection != null) {
            hubConnection.stop();
            hubConnection = null;
        }
    }
}
