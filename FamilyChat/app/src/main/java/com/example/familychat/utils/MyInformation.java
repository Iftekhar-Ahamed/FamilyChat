package com.example.familychat.utils;

import com.example.familychat.model.UserContext;

public class MyInformation {
    public static UserContext data;
    public static String token;
    private MyInformation(){}
    public static void  initialize(UserContext user,String tk){
        data = user;
        token = tk;
    }
}
