package com.example.patrik.androidchat;

import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sendButton;
    private EditText messageInput;
    private MessageAdapter messageAdapter;
    private ChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());

        ListView messagesView = (ListView) findViewById(R.id.message_list);
        messagesView.setAdapter(messageAdapter);

        messageInput = (EditText) findViewById(R.id.edit_message);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        new clientTask().execute("");

    }

    @Override
    public void onClick(View v) {
        sendMessage();
    }

    private void sendMessage(){
        String text = messageInput.getText().toString();

        if(!text.equals("")){
            Message message = new Message(text, "127.0.0.1", new Date().toString());
            chatClient.sendMessage(message);
            messageInput.setText("");
        }
    }

    private class clientTask extends AsyncTask<String, String, ChatClient> {
        protected ChatClient doInBackground(String... message) {
            try {
                chatClient = new ChatClient(new ChatClient.MessageListener(){
                    @Override
                    public void onMessageReceived(Message message){
                        publishProgress(message.text);
                    }
                }

                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            chatClient.startRead(messageAdapter);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            messageAdapter.addMessage(new Message(values[0], "0", null));
            messageAdapter.notifyDataSetChanged();
        }
    }
}
