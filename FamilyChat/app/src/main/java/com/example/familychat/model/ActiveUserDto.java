package com.example.familychat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveUserDto {
    @JsonProperty("ConnectionId")
    public String connectionId;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("UserId")
    public int userId;
    @JsonProperty("ChatId")
    public int chatId;
    @JsonProperty("IsUser")
    public boolean isUser;
}
