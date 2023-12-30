package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
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
    ChatRooms chatRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            user = MyInformation.data;
            setContentView(R.layout.activity_home);
            Switch switchService = findViewById(R.id.switchService);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            searchButton = findViewById(R.id.main_search_btn);


            if(SignalRManager.serviceRunning==false) {
                switchService.setChecked(false);
            }else {
                switchService.setChecked(true);
            }

            switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        startBroadcastService();
                    } else {
                        stopBroadcastService();
                    }
                }
            });

            setupChatRooms();


            searchButton.setOnClickListener((v) -> {
                Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();

            });


            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.menu_chat) {
                        Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                        setupChatRooms();
                    }
                    if (item.getItemId() == R.id.menu_profile) {
                        Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
                        loadProfileFragment();
                    }
                    return true;
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
        EventBus.getDefault().register(this);
    }


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
        setupChatRooms();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageEvent(ChatMessageEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.addMessage(chatMessage);
        ChatManager.getChatRooms(chatMessage.chatId).chatAdapter.notify();
    }

    @Override
    protected void onDestroy() {
        // Unregister EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void loadChatFragment() {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame_layout, new ChatRecentFragment());
            transaction.commit();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private void loadProfileFragment() {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame_layout, new ProfileFragment());
            transaction.commit();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    private void setupChatRooms() {
        API<ChatRoomDto> apiConnectionList = new API<ChatRoomDto>(this);
        String connectionListUrl = "FamilyChat/GetAllConnectionByUserId" + "?id=" + MyInformation.data.userId;

        apiConnectionList.fetchDataList(connectionListUrl, ChatRoomDto.class, MyInformation.token, new API.UserCallback<List<ChatRoomDto>>() {
            @Override
            public void onUserReceived(List<ChatRoomDto> data) {
                processChatRooms(data, 0);
            }

            @Override
            public void onUserError(String errorMessage) {
                Toast.makeText(Home.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processChatRooms(List<ChatRoomDto> data, int index) {
        if (index < data.size()) {
            ChatRoomDto item = data.get(index);
            getChatRoom(item.chatFriendId, new ChatRoomCallback() {
                @Override
                public void onChatRoomReceived(ChatRooms chatRooms) {
                    chatRooms.chatId = item.chatId;
                    ChatManager.addChatRooms(item.chatId, chatRooms);
                    processChatRooms(data, index + 1); // Process the next item
                }
                @Override
                public void onChatRoomError(String errorMessage) {
                    Toast.makeText(Home.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            onChatRoomsLoaded(); // All chat rooms processed, call the callback
        }
    }

    public void getChatRoom(Integer chatFriendId, ChatRoomCallback callback) {
        ChatRooms room = new ChatRooms();
        API<UserContext> userData = new API<UserContext>(this);
        String userDataUrl = "FamilyChat/GetUserById" + "?UserId=" + chatFriendId;
        userData.fetchData(userDataUrl, UserContext.class, MyInformation.token, new API.UserCallback<UserContext>() {
            @Override
            public void onUserReceived(UserContext user) {
                room.UserFriend = user;
                room.chatAdapter = new ChatAdapter(Home.this,new ArrayList<ChatMessage>());
                callback.onChatRoomReceived(room);
            }

            @Override
            public void onUserError(String errorMessage) {
                callback.onChatRoomError(errorMessage);
            }
        });
    }





    public void onChatRoomsLoaded() {
        loadChatFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
