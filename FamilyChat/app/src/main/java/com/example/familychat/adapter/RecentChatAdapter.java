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
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.utils.ChatViewHolder;

import java.util.List;
import java.util.Map;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ChatroomModelViewHolder>{
    private Context context;
    private Map<Integer,ChatRooms> data;

    public RecentChatAdapter(Context context, Map<Integer,ChatRooms> data) {
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatroomModelViewHolder holder, int position) {
        for (Map.Entry<Integer, ChatRooms> entry :data.entrySet()) {
            Integer key = entry.getKey();
            ChatRooms value = entry.getValue();
            holder.usernameText.setText(value.UserFriend.userName);
        }
    }
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
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
