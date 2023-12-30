package com.example.familychat.model;

public class SaveLogInInfoDto {
    public  UserContext data;
    public  String token;
    public SaveLogInInfoDto(){

    }
    public SaveLogInInfoDto(UserContext user,String tk){
        this.data = user;
        token = tk;
    }
}
