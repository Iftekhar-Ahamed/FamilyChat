package com.example.familychat.utils;

public class TrackingActivity {
    private Integer chatId;
    public static TrackingActivity trackingActivity;
    public TrackingActivity(){
        chatId = 0;
        if(trackingActivity==null) {
            trackingActivity = this;
        }
    }
    public synchronized int getChatId() {
        return this.chatId;
    }
    public synchronized void setChatId(int newValue) {
        this.chatId = newValue;
    }
}
