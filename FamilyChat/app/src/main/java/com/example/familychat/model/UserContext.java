package com.example.familychat.model;

import java.io.Serializable;

public class UserContext implements Serializable {
    public String name="";
    public String connectionId="";
    public Integer UserId=0;
    public  Boolean IsUser=false;
    public UserContext(UserContext user){
        name = user.name;
        connectionId = user.connectionId;
        UserId = user.UserId;
        IsUser = user.IsUser;
    }
    public UserContext(){
    }
}
