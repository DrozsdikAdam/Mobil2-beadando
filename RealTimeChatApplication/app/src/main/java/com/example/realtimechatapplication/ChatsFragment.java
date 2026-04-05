package com.example.realtimechatapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bottom Navigation kezelése
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_chats);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                showAddOptionsDialog();
                return true;
            } else if (itemId == R.id.nav_settings) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new fragment_profile())
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return itemId == R.id.nav_chats;
        });

        // RecyclerView inicializálása
        recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adatlista feltöltése
        chatList = new ArrayList<>();
        chatList.add(new ChatModel(getString(R.string.user_name_1), getString(R.string.message_1), getString(R.string.time_1), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_2), getString(R.string.message_2), getString(R.string.time_2), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_3), getString(R.string.message_3), getString(R.string.time_3), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_4), getString(R.string.message_4), getString(R.string.time_yesterday), R.drawable.ic_person));
        chatList.add(new ChatModel(getString(R.string.user_name_5), getString(R.string.message_5), getString(R.string.time_yesterday), R.drawable.ic_person));

        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
    }

    private void showAddOptionsDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_add_options, null);

        // Ismerős hozzáadása opció
        view.findViewById(R.id.optionAddContact).setOnClickListener(v -> {
            dialog.dismiss();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new fragment_add_contact())
                    .addToBackStack(null)
                    .commit();
        });

        // Csoport létrehozása opció
        view.findViewById(R.id.optionCreateGroup).setOnClickListener(v -> {
            dialog.dismiss();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CreateGroupFragment())
                    .addToBackStack(null)
                    .commit();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}