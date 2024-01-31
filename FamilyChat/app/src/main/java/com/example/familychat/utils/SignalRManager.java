package com.example.familychat.utils;

import static com.microsoft.signalr.HubConnectionState.CONNECTED;
import static com.microsoft.signalr.HubConnectionState.CONNECTING;
import static com.microsoft.signalr.HubConnectionState.DISCONNECTED;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.familychat.NotificationActivity;
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

    //region SETUP
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

    public synchronized static void initialize(Context context, UserContext user) {
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder.create(HUB_URL).build();
            hubConnection.start().blockingAwait();
            setupSignalR(user);
            Toast.makeText(context, "Online", Toast.LENGTH_SHORT).show();
        }
    }

    private synchronized static void setupSignalR(UserContext user) {
        try {
            hubConnection.send("SaveUserConnection", user.userId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.userId);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    //endregion

    //region Message Handle
    private void getNotification(){
        try {
            hubConnection.on("broadcastMessage", (message) -> {

                try {
                    ObjectMapper om = new ObjectMapper();
                    ChatMessage msg = new ChatMessage();
                    msg.messageText = message;
                    msg.chatId = 1;
                    msg.userId = 2;
                    postChatNotificationEvent("Iftekhar","Test Message",1);
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
                    postChatNotificationEvent(msg.userName,msg.messageText,msg.chatId);

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
    public static boolean SendMessageToClint(String message) {
        Integer tryed = 5;
        while (tryed>=0) {

            if (hubConnection != null && hubConnection.getConnectionState() == CONNECTED) {
                //hubConnection.send("SendNotificationToAll", message);
                hubConnection.send("SendNotificationToClient", message);
                return true;
            } else {

                if (hubConnection != null) {
                    try {
                        hubConnection.start().blockingAwait();
                        setupSignalR(MyInformation.data);
                        if (hubConnection.getConnectionState() == CONNECTED) {
                            return true;
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
            tryed--;
        }
        return false;
    }




    //endregion

    //region HUBCONNECTION
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
    //endregion

    //region EventBus
    private static void postChatMessageEvent(ChatMessage chatMessage) {
        try {
            EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static void postChatRoomEvent(ChatRooms chatRooms) {
        try {
            EventBus.getDefault().post(new ChatRoomEvent(chatRooms));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
    //endregion

    //region Notification
    private void postChatNotificationEvent(String title,String content,Integer chatId) {

        Integer c = TrackingActivity.trackingActivity.getChatId();
        if(c!=chatId) {
            makeNotification(title, content);
        }
    }
    public void makeNotification(String title,String content) {
        String chanelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), chanelID);
        builder.setSmallIcon(R.drawable.launcher_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("msg", content);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(chanelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID, "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(0, builder.build());
        }
    }
    //endregion

}
