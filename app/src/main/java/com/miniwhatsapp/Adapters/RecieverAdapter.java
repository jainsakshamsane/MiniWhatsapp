package com.miniwhatsapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miniwhatsapp.Models.MessageModel;
import com.miniwhatsapp.R;

import java.util.List;

public class RecieverAdapter extends RecyclerView.Adapter<RecieverAdapter.ViewHolder> {

    private List<MessageModel> messages;

    public RecieverAdapter(List<MessageModel> messages) {
        this.messages = messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapper_reciever, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        holder.messageText.setText(message.getMessageText());
        // Set other message properties as needed
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
        }
    }
}
