package com.example.realtimechatapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView inicializálása
        recyclerView = findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adatlista létrehozása és feltöltése tesztadatokkal
        chatList = new ArrayList<>();
        chatList.add(new ChatModel(getString(R.string.user_name_1), getString(R.string.message_1), getString(R.string.time_1), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_2), getString(R.string.message_2), getString(R.string.time_2), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_3), getString(R.string.message_3), getString(R.string.time_3), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_4), getString(R.string.message_4), getString(R.string.time_yesterday), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_5), getString(R.string.message_5), getString(R.string.time_yesterday), R.drawable.ic_person));

        // Adapter beállítása
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
    }
}