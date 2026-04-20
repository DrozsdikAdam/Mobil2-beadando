package com.example.realtimechatapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimechatapplication.R;
import com.example.realtimechatapplication.models.UserModel;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class FriendSelectAdapter extends RecyclerView.Adapter<FriendSelectAdapter.FriendViewHolder> {

    private List<UserModel> friendList;

    public FriendSelectAdapter(List<UserModel> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        UserModel user = friendList.get(position);
        holder.tvFriendName.setText(user.getUserName());

        if (user.isSelected()) {
            holder.btnSelectFriend.setImageResource(R.drawable.ic_close);
            holder.btnSelectFriend.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.TextBlue));
        } else {
            holder.btnSelectFriend.setImageResource(R.drawable.ic_add);
            holder.btnSelectFriend.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.TextBlue));
        }

        holder.itemView.setOnClickListener(v -> {
            user.setSelected(!user.isSelected());
            notifyItemChanged(position);
        });

        holder.btnSelectFriend.setOnClickListener(v -> {
            user.setSelected(!user.isSelected());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgFriendProfile;
        TextView tvFriendName;
        ImageButton btnSelectFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFriendProfile = itemView.findViewById(R.id.imgFriendProfile);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            btnSelectFriend = itemView.findViewById(R.id.btnSelectFriend);
        }
    }
}