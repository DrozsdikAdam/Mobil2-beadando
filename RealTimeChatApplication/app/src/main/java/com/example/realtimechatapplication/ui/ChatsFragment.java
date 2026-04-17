package com.example.realtimechatapplication.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.realtimechatapplication.MainViewModel;
import com.example.realtimechatapplication.adapters.ChatAdapter;
import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.ChatModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel inicializálása
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
        });

        // Navigáció beállítása
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.chatsFragment, R.id.loginFragment)
                .build();
        
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                showAddOptionsDialog();
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
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
        NavController navController = Navigation.findNavController(requireView());

        view.findViewById(R.id.optionAddContact).setOnClickListener(v -> {
            dialog.dismiss();
            navController.navigate(R.id.addContactFragment);
        });

        view.findViewById(R.id.optionCreateGroup).setOnClickListener(v -> {
            dialog.dismiss();
            navController.navigate(R.id.createGroupFragment);
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
