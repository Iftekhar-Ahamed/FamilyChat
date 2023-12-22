package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.MyInformation;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.API;
import com.example.familychat.utils.ChatRoomCallback;
import com.example.familychat.utils.ChatRoomDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.microsoft.signalr.HubConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            searchButton = findViewById(R.id.main_search_btn);

            SignalRManager.initialize(this);
            hubConnection = SignalRManager.getHubConnection();


            setupSignalR();
            setupChatRooms();


            searchButton.setOnClickListener((v) -> {
                Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();

            });


            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.menu_chat) {
                        Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                    }
                    if (item.getItemId() == R.id.menu_profile) {
                        Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
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
                callback.onChatRoomReceived(room);
            }

            @Override
            public void onUserError(String errorMessage) {
                callback.onChatRoomError(errorMessage);
            }
        });
    }



    private void setupSignalR() {
        try {
            hubConnection.send("SaveUserConnection", user.userId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.userId);
            hubConnection.on("broadcastMessage", (message) -> {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                        ObjectMapper om = new ObjectMapper();
                        ChatMessage msg = om.readValue(message, ChatMessage.class);
                        if(chatRooms.chatFragment!=null){
                            chatRooms.chatFragment.onNewMessage(msg);
                        }
                    }catch (Exception e){
                        Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }, String.class);

            hubConnection.on("ActiveUser", (message) -> {
                runOnUiThread(() -> {
                    try {
                        ObjectMapper om = new ObjectMapper();
                        UserContext user = om.readValue(message, UserContext.class);
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }, String.class);
        } catch (Exception e) {
            Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void onChatRoomsLoaded() {
        loadChatFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
