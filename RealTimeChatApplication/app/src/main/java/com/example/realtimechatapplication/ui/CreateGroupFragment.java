package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.adapters.FriendSelectAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupFragment extends Fragment {

    private EditText editTextGroupName;
    private RecyclerView recyclerViewFriends;
    private MaterialButton btnCreateGroup;
    private ImageButton btnBack;
    private FriendSelectAdapter adapter;
    private List<UserModel> friendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextGroupName = view.findViewById(R.id.editTextGroupName);
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        btnCreateGroup = view.findViewById(R.id.btnCreateGroup);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        setupRecyclerView();

        btnCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(getContext(), "Kérlek adj meg egy csoportnevet!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<UserModel> selectedFriends = new ArrayList<>();
            for (UserModel user : friendList) {
                if (user.isSelected()) {
                    selectedFriends.add(user);
                }
            }

            if (selectedFriends.isEmpty()) {
                Toast.makeText(getContext(), "Legalább egy tagot válassz ki!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Itt jönne a logika a csoport létrehozásához (pl. Firebase)
            Toast.makeText(getContext(), groupName + " létrehozva " + selectedFriends.size() + " taggal", Toast.LENGTH_LONG).show();
            
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        friendList = new ArrayList<>();
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
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFriends.setAdapter(adapter);
    }
}