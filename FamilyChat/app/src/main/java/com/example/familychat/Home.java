package com.example.familychat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.utils.MyInformation;
import com.example.familychat.utils.NotificationEvent;
import com.example.familychat.utils.SignalRManager;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.ChatMessageEvent;
import com.example.familychat.utils.TrackingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Home extends AppCompatActivity  {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    UserContext user;
    private static final String CHAT_FRAG_TAG = "chatRecentFrag";
    private static final String PROFILE_FRAG_TAG = "profileFrag";
    private  ChatRecentFragment chatRecentFrag ;
    public Home(){
        TrackingActivity ob = new TrackingActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            user = MyInformation.data;
            setContentView(R.layout.activity_home);
            Switch switchService = findViewById(R.id.switchService);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            searchButton = findViewById(R.id.main_search_btn);
            manageFragmentTransaction(CHAT_FRAG_TAG);
            if(SignalRManager.serviceRunning==false) {
                switchService.setChecked(false);
            }else {
                switchService.setChecked(true);
            }

            //region Button

            switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        startBroadcastService();
                    } else {
                        stopBroadcastService();
                    }
                }
            });
            searchButton.setOnClickListener((v) -> {
                Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();

            });
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.menu_chat) {
                        //Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                        manageFragmentTransaction(CHAT_FRAG_TAG);
                    }
                    if (item.getItemId() == R.id.menu_profile) {
                        //Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
                        manageFragmentTransaction(PROFILE_FRAG_TAG);
                    }
                    return true;
                }
            });

            //endregion

        }catch (Exception e){
            System.out.println(e);
        }

        EventBus.getDefault().register(this);
    }

    //region ON/OFF BroadcastService
    public void stopBroadcastService(){
        SignalRManager.serviceRunning=false;
        SignalRManager.stopConnection();
        Intent intent = new Intent(Home.this, SignalRManager.class);
        intent.putExtra("status","stop");
        stopService(intent);
    }
    public void startBroadcastService(){
        SignalRManager.serviceRunning=true;
        SignalRManager.initialize(Home.this,user);
        Intent intent = new Intent(Home.this, SignalRManager.class);
        intent.putExtra("status","start");
        startForegroundService(intent);
    }
    //endregion


    //region EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageEvent(ChatMessageEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.addMessage(chatMessage);
        ChatManager.recentChatAdapter.updateLastMessageText(ChatManager.getIndexForKey(chatMessage.chatId));
        if(ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.messages.size()>20) {
            ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.removeMessage(0);
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatNotificationEvent(NotificationEvent event) {
        Integer c = TrackingActivity.trackingActivity.getChatId();
        if(c!=event.chatId) {
            makeNotification(event.title, event.content);
        }
    }

    //endregion

    //region ManageFragmentTransaction
    public void manageFragmentTransaction(String selectedFrag) {
        switch (selectedFrag) {
            case CHAT_FRAG_TAG:
                if (getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG) != null) {
                    // If the fragment exists, show it.
                    chatRecentFrag = (ChatRecentFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_left,   // enter from left
                                    R.anim.exit_right,  // exit to right
                                    R.anim.fade_in,               // pop enter
                                    R.anim.fade_out               // pop exit
                            )
                            .show(getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG)).commit();
                } else {

                    // If the fragment does not exist, add it to the fragment manager.
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_right,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.exit_left  // popExit
                            ).add(R.id.main_frame_layout, new ChatRecentFragment(), CHAT_FRAG_TAG).commit();
                    chatRecentFrag = (ChatRecentFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG);
                }
                if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG) != null) {
                    // If the other fragment is visible, hide it.
                    getSupportFragmentManager()
                            .beginTransaction()
                            .hide(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG)).commit();
                }
                break;
            case PROFILE_FRAG_TAG:
                if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG) != null) {
                    // If the fragment exists, show it.
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_right,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.exit_left  // popExit
                            )
                            .show(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG)).commit();
                } else {
                    // If the fragment does not exist, add it to the fragment manager.
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(
                                    R.anim.enter_right,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.exit_left  // popExit
                            ).add(R.id.main_frame_layout, new ProfileFragment(), PROFILE_FRAG_TAG).commit();
                }
                if (getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG) != null) {
                    // If the other fragment is visible, hide it.
                    getSupportFragmentManager()
                            .beginTransaction()
                            .hide(getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG)).commit();
                }
                break;
        }
    }
    //endregion

    //region DefultStateApp
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        // Unregister EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    //endregion


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

}
