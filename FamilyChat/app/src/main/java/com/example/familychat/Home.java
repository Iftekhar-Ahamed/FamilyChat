package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.adapter.RecentChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.utils.ChatRoomEvent;
import com.example.familychat.utils.MyInformation;
import com.example.familychat.utils.SignalRManager;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.API;
import com.example.familychat.utils.ChatMessageEvent;
import com.example.familychat.model.ChatRoomCallback;
import com.example.familychat.model.ChatRoomDto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.microsoft.signalr.HubConnection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity  {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    UserContext user;
    HubConnection hubConnection;
    private static final String CHAT_FRAG_TAG = "chatRecentFrag";
    private static final String PROFILE_FRAG_TAG = "profileFrag";
    private  ChatRecentFragment chatRecentFrag ;

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
                        Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                        manageFragmentTransaction(CHAT_FRAG_TAG);
                    }
                    if (item.getItemId() == R.id.menu_profile) {
                        Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
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
        hubConnection = SignalRManager.getHubConnection();
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
        ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.notify();
    }
    private static void postChatRoomEvent(ChatRooms chatRooms) {
        try {
            EventBus.getDefault().post(new ChatRoomEvent(chatRooms));
        }catch (Exception e){
            System.out.println(e.toString());
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
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG)).commit();
                } else {

                    // If the fragment does not exist, add it to the fragment manager.
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, new ChatRecentFragment(), CHAT_FRAG_TAG).commit();
                    chatRecentFrag = (ChatRecentFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG);
                }
                if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG) != null) {
                    // If the other fragment is visible, hide it.
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG)).commit();
                }
                break;
            case PROFILE_FRAG_TAG:
                if (getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG) != null) {
                    // If the fragment exists, show it.
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAG_TAG)).commit();
                } else {
                    // If the fragment does not exist, add it to the fragment manager.
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, new ProfileFragment(), PROFILE_FRAG_TAG).commit();
                }
                if (getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG) != null) {
                    // If the other fragment is visible, hide it.
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(CHAT_FRAG_TAG)).commit();
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
}
