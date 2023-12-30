package com.example.familychat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familychat.model.ChatManager;
import com.example.familychat.utils.MyInformation;
import com.example.familychat.model.SaveLogInInfoDto;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.API;
import com.example.familychat.utils.FileOperation;
import com.example.familychat.model.MessageHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

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
        ChatManager.clearAllChatRooms();
        setInProgress(false);

        if(readUserLogInInfo()){
            Intent home = new Intent(LogIn.this, Home.class);
            startActivity(home);
            finish();
        }


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
                                try {
                                    Intent home = new Intent(LogIn.this, Home.class);
                                    MyInformation.initialize(user, msg.token);
                                    ObjectMapper om = new ObjectMapper();
                                    String info = om.writeValueAsString(new SaveLogInInfoDto(user, msg.token));
                                    saveUserLogInInfo(info);
                                    startActivity(home);
                                    finish();
                                }catch (Exception e){
                                    System.out.println(e);
                                }
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
    private void saveUserLogInInfo(String content){
        File path = getApplicationContext().getFilesDir();
        FileOperation.writeIntoFile(path,"UserLogInInfo.txt",content);
    }
    private boolean readUserLogInInfo(){
        File path = getApplicationContext().getFilesDir();
        String res = FileOperation.readFromFile(path,"UserLogInInfo.txt");
        if(res == "NotFound"){
            return  false;
        }
        try {
            ObjectMapper om = new ObjectMapper();
            SaveLogInInfoDto dto = om.readValue(res,SaveLogInInfoDto.class);
            MyInformation.initialize(dto.data, dto.token);
            return true;
        }catch (Exception e){
            return  false;
        }
    }
}
