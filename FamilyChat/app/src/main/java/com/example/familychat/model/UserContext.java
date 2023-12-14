package com.example.familychat.model;

public class UserContext {
    public String name="";
    public String connectionId="";
    public Integer Id=0;
    public  Boolean IsUser=false;
    public UserContext(UserContext user){
        name = user.name;
        connectionId = user.connectionId;
        Id = user.Id;
        IsUser = user.IsUser;
    }
    public UserContext(){

    }
}
