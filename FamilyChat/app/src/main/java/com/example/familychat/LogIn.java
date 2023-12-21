package com.example.familychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.familychat.utils.ChatRoomDto;
import com.example.familychat.utils.MessageHelper;
import com.example.familychat.model.MyInformation;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.API;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LogIn extends AppCompatActivity {
    String message;
    String token;
    EditText usernameInput,passwordInput;
    ProgressBar progressBar;
    Button logInBtn,forgetPassBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInput = findViewById(R.id.login_username);
        passwordInput = findViewById(R.id.login_password);
        logInBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        forgetPassBtn = findViewById(R.id.login_forget_password_btn);
        setInProgress(false);

        logInBtn.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                setInProgress(true);
                API<MessageHelper> api = new API<MessageHelper>(v.getContext());
                String url = "FamilyChat/UserLogIn" + "?UserName=" + usernameInput.getText().toString() + "&Password=" + passwordInput.getText().toString();
                api.fetchData(url, MessageHelper.class,null, new API.UserCallback<MessageHelper>() {
                    @Override
                    public void onUserReceived(MessageHelper msg) {
                        Toast.makeText(LogIn.this, msg.message, Toast.LENGTH_SHORT).show();

                        API<UserContext> userData = new API<UserContext>(v.getContext());
                        String userDataUrl = "FamilyChat/GetUserById" + "?UserId=" + msg.userId;
                        userData.fetchData(userDataUrl,UserContext.class,msg.token,new API.UserCallback<UserContext>(){
                            @Override
                            public void onUserReceived(UserContext user) {
                                Intent home = new Intent(LogIn.this, Home.class);
                                MyInformation.initialize(user,msg.token);
                                startActivity(home);
                                finish();
                            }
                            @Override
                            public void onUserError(String errorMessage) {
                                Toast.makeText(LogIn.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onUserError(String errorMessage) {
                        Toast.makeText(LogIn.this, "Wrong User Name and Password", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }
                });
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            logInBtn.setVisibility(View.GONE);
            forgetPassBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            logInBtn.setVisibility(View.VISIBLE);
            forgetPassBtn.setVisibility(View.VISIBLE);
        }
    }

    public void buttonColorChange(View v,Button colorButton){
        int color = Color.parseColor("#e74c3c");
        colorButton.setBackgroundColor(color);

        // Delay to reset color after 500 milliseconds (0.5 seconds)
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                int originalColor = Color.parseColor("#3498db");
                colorButton.setBackgroundColor(originalColor);
            }
        }, 500);
    }

}
