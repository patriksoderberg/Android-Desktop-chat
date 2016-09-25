package com.example.patrik.androidchat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Patrik on 2016-08-08.
 */
public class MessageAdapter extends BaseAdapter {

    Context messageContext;
    ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages){
        messageContext = context;
        this.messages = messages;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = messageInflater.inflate(R.layout.message_row, null);

        MessageViewHolder holder = new MessageViewHolder();
        holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
        holder.messageView = (TextView) convertView.findViewById(R.id.message_text);

        convertView.setTag(holder);

        Message message = (Message) getItem(position);
        holder.messageView.setText(message.text);
        holder.senderView.setText(message.ip);

        return convertView;
    }

    public void addMessage(Message message){
        messages.add(message);
        notifyDataSetChanged();
    }

    private static class MessageViewHolder{
        public TextView senderView;
        public TextView messageView;
    }
}
