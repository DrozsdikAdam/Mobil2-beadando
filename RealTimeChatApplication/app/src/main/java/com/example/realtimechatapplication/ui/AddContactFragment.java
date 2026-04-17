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

public class AddContactFragment extends Fragment {

    private EditText searchContact;
    private RecyclerView recyclerViewContacts;
    private MaterialButton btnAddContact;
    private ImageButton btnBack;
    private FriendSelectAdapter adapter;
    private List<UserModel> contactList;

    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchContact = view.findViewById(R.id.searchContact);
        recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts);
        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        setupRecyclerView();

        btnAddContact.setOnClickListener(v -> {
            List<UserModel> selectedContacts = new ArrayList<>();
            for (UserModel user : contactList) {
                if (user.isSelected()) {
                    selectedContacts.add(user);
                }
            }

            if (selectedContacts.isEmpty()) {
                Toast.makeText(getContext(), "Kérlek válassz ki legalább egy ismerőst!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), selectedContacts.size() + " ismerős hozzáadva", Toast.LENGTH_LONG).show();
            
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        contactList = new ArrayList<>();
        contactList.add(new UserModel("11", "Hajdú Péter", ""));
        contactList.add(new UserModel("12", "Balogh Edit", ""));
        contactList.add(new UserModel("13", "Kelemen Ottó", ""));
        contactList.add(new UserModel("14", "Vincze Gábor", ""));
        contactList.add(new UserModel("15", "Sándor Beáta", ""));

        adapter = new FriendSelectAdapter(contactList);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewContacts.setAdapter(adapter);
    }
}