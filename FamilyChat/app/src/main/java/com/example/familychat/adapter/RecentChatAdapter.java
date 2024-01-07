package com.example.familychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.R;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.RecentChatViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatViewHolder>{
    private final Context context;
    private final List<ChatRooms> data;
    private final OnItemClickListener onItemClickListener;

    public RecentChatAdapter(Context context,OnItemClickListener onItemClickListener1) {
        this.context = context;
        this.data = new ArrayList<>();
        this.onItemClickListener = onItemClickListener1;
    }
    @NonNull
    @Override
    public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new RecentChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatViewHolder holder, int position) {
        ChatRooms chatRoom = data.get(position);
        holder.usernameText.setText(chatRoom.UserFriend.userName);
        holder.lastMessageTime.setText("10:00AM");
        holder.lastMessageText.setText("Testing");
        holder.profilePic.setImageResource(R.drawable.person_icon);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(chatRoom);
            }
        });

    }

    public interface OnItemClickListener {
        void onItemClick(ChatRooms chatRoom);
    }

    @Override
    public int getItemCount() {
        return ChatManager.getAllChatRooms().size();
    }
    public void addChatRoom(ChatRooms room) {
        data.add(room);
        notifyItemInserted(data.size() - 1);
    }
}
