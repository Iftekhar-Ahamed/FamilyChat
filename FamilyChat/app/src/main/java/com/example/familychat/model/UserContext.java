package com.example.familychat.model;

import java.io.Serializable;

public class UserContext implements Serializable {
    public String userName="";
    public String connectionId="";
    public String passWord="";
    public Integer userId=0;
    public  Boolean isUser=false;
    public UserContext(UserContext user){
        userName = user.userName;
        connectionId = user.connectionId;
        userId = user.userId;
        isUser = user.isUser;
    }
    public UserContext(){
    }
}
