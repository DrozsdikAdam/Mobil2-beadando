package com.example.realtimechatapplication;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText editTextGroupName;
    private RecyclerView recyclerViewFriends;
    private MaterialButton btnCreateGroup;
    private ImageButton btnBack;
    private FriendSelectAdapter adapter;
    private List<UserModel> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnBack = findViewById(R.id.imageButton);

        btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();

        btnCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Kérlek adj meg egy csoportnevet!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<UserModel> selectedFriends = new ArrayList<>();
            for (UserModel user : friendList) {
                if (user.isSelected()) {
                    selectedFriends.add(user);
                }
            }

            if (selectedFriends.isEmpty()) {
                Toast.makeText(this, "Legalább egy tagot válassz ki!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Itt jönne a logika a csoport létrehozásához (pl. Firebase)
            Toast.makeText(this, groupName + " létrehozva " + selectedFriends.size() + " taggal", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void setupRecyclerView() {
        friendList = new ArrayList<>();
        // Bővített teszt adatok a teszteléshez
        friendList.add(new UserModel("1", "Kovács János", ""));
        friendList.add(new UserModel("2", "Nagy Anna", ""));
        friendList.add(new UserModel("3", "Szabó Béla", ""));
        friendList.add(new UserModel("4", "Tóth Gergő", ""));
        friendList.add(new UserModel("5", "Horváth Eszter", ""));
        friendList.add(new UserModel("6", "Kiss László", ""));
        friendList.add(new UserModel("7", "Molnár Zsófia", ""));
        friendList.add(new UserModel("8", "Varga Dávid", ""));
        friendList.add(new UserModel("9", "Fekete Péter", ""));
        friendList.add(new UserModel("10", "Papp Krisztina", ""));

        adapter = new FriendSelectAdapter(friendList);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriends.setAdapter(adapter);
    }
}