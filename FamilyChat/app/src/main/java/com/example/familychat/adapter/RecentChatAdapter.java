package com.example.familychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.R;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatRooms;

import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ChatroomModelViewHolder>{
    private final Context context;
    private final List<ChatRooms> data;
    private final OnItemClickListener onItemClickListener;

    public RecentChatAdapter(Context context, List<ChatRooms> data,OnItemClickListener onItemClickListener1) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener1;
    }
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position) {
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

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

    @Override
    public int getItemCount() {
        return ChatManager.getAllChatRooms().size();
    }
    public void addMessage(Integer key, ChatRooms room) {
        ChatManager.addChatRooms(key,room);
        notifyItemInserted(ChatManager.getAllChatRooms().size() - 1);
    }
}
