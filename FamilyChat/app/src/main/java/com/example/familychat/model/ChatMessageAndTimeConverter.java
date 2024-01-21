package com.example.familychat.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessageAndTimeConverter {
    static String patternTime = "hh:mm a";
    static String patternDate = "dd MMM";

    ChatMessageAndTimeConverter() {
    }

    public static String convertedTimeForLastChatTime(String dt) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            DateTimeFormatter outputFormatterTime = DateTimeFormatter.ofPattern(patternTime);
            DateTimeFormatter outputFormatterDate = DateTimeFormatter.ofPattern(patternDate);

            LocalDate today = LocalDate.now();
            LocalDate messageDate = dateTime.toLocalDate();

            if (today.equals(messageDate)) {
                // Today, format as time
                String formattedTime = dateTime.format(outputFormatterTime);
                return formattedTime;
            } else {
                // Not today, format as date
                String formattedDate = dateTime.format(outputFormatterDate);
                return formattedDate;
            }
        }catch (Exception e){
            return "*_*";
        }
    }

    public static String convertMessageIntoShortMessage(String msg){
        String converted = "";
        if(msg.length()>10){
            converted = msg.substring(0,10)+"....";
        }else {
            converted = msg;
        }
        return converted;
    }
}
