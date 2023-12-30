package com.example.familychat.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout leftChatLayout;
    public LinearLayout rightChatLayout;
    public TextView leftChatTextView;
    public TextView rightChatTextView;

    public ChatViewHolder(View itemView) {
        super(itemView);
        leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
        rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
        leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
        rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
    }
}
