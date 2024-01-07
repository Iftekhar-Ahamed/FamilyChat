package com.example.familychat.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.R;

public class RecentChatViewHolder extends RecyclerView.ViewHolder {
    public TextView usernameText;
    public TextView lastMessageText;
    public TextView lastMessageTime;
    public ImageView profilePic;
    public RecentChatViewHolder(View itemView){
        super(itemView);
        usernameText = itemView.findViewById(R.id.user_name_text);
        lastMessageText = itemView.findViewById(R.id.last_message_text);
        lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
        profilePic = itemView.findViewById(R.id.profile_pic_image_view);
    }
}
