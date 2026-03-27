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
        chatList.add(new ChatModel("Teszt Elek", "Szia! Mi újság?", "12:00", R.drawable.ic_person));
        chatList.add(new ChatModel("Kovács Anna", "Mikor találkozunk?", "11:45", R.drawable.ic_person));
        chatList.add(new ChatModel("Nagy Gábor", "Elküldtem a fájlt.", "09:30", R.drawable.ic_person));
        chatList.add(new ChatModel("Szabó Bence", "Köszi a segítséget!", "Tegnap", R.drawable.ic_person));
        chatList.add(new ChatModel("Horváth Dóra", "Szia, mizu?", "Tegnap", R.drawable.ic_person));

        // Adapter beállítása
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
    }
}