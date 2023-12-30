package com.example.familychat.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.familychat.R;
import com.example.familychat.model.ActiveUserDto;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.ChatMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.greenrobot.eventbus.EventBus;

public class SignalRManager extends Service {
    private static final String HUB_URL = "http://familychat.somee.com/notificationHub";
    private static HubConnection hubConnection;
    public static boolean serviceRunning = false;
    final String CHANNEL_ID = "default_channel";
    final int id = 2001;
    String msg;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentText("FamilyChat is running")
                    .setContentTitle(msg)
                    .setSmallIcon(R.drawable.launcher_icon)
                    .build();

            startForeground(id, notification);
            getNotification();
            if(intent.getExtras().get("status")=="stop") {
                stopForegroundService();
            }
        return super.onStartCommand(intent,flag,startId);
    }


    public static void initialize(Context context, UserContext user) {
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder.create(HUB_URL).build();
            hubConnection.start().blockingAwait();
            setupSignalR(user);
            Toast.makeText(context, "Online", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setupSignalR(UserContext user) {
        try {
            hubConnection.send("SaveUserConnection", user.userId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.userId);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    private static void postChatMessageEvent(ChatMessage chatMessage) {
        try {
            EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private void getNotification(){
        try {
            hubConnection.on("broadcastMessage", (message) -> {

                try {
                    ObjectMapper om = new ObjectMapper();
                    ChatMessage msg = om.readValue(message, ChatMessage.class);
                    ChatRooms chatRooms = ChatManager.getChatRooms(msg.chatId);
                    this.msg = message;
                    postChatMessageEvent(msg);
                    showNotification(msg.userName,msg.messageText);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }, String.class);
            hubConnection.on("ReceivedPersonalNotification", (message) -> {

                try {
                    ObjectMapper om = new ObjectMapper();
                    ChatMessage msg = om.readValue(message, ChatMessage.class);
                    this.msg = message;
                    postChatMessageEvent(msg);
                    showNotification(msg.userName,msg.messageText);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }, String.class);
            hubConnection.on("ActiveUser", (message) -> {
                try {
                    this.msg = message;
                    ObjectMapper om = new ObjectMapper();
                    ActiveUserDto u = om.readValue(message, ActiveUserDto.class);
                    ChatManager.getChatRooms(u.chatId).UserFriend.connectionId = u.connectionId;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }, String.class);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }


    public static HubConnection getHubConnection() {
        return hubConnection;
    }
    private void stopForegroundService() {
        stopForeground(true);
        stopSelf();
    }
    public static void stopConnection() {
        if (hubConnection != null) {
            hubConnection.stop();
            hubConnection = null;
        }
    }
}
