package saneforce.sanzen.activity.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanzen.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
    }
}