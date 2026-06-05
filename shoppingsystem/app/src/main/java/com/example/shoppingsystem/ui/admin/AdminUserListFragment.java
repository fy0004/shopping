package com.example.shoppingsystem.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserListAdapter adapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        rvUsers = view.findViewById(R.id.rv_admin_users);
        adapter = new UserListAdapter(requireContext());
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUsers.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        viewModel.loadAllUsers();
        viewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> adapter.setUsers(users));

        adapter.setOnToggleListener(user -> {
            String newStatus = "ACTIVE".equals(user.getStatus()) ? "DISABLED" : "ACTIVE";
            viewModel.updateUserStatus(user.getId(), newStatus);
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> adapter.setUsers(users));
            }
        });

        return view;
    }

    private static class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.VH> {

        private final android.content.Context context;
        private List<User> users = new ArrayList<>();
        private OnToggleListener toggleListener;

        UserListAdapter(android.content.Context ctx) { this.context = ctx; }

        void setUsers(List<User> list) { this.users = list != null ? list : new ArrayList<>(); notifyDataSetChanged(); }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            User u = users.get(pos);
            holder.tvInfo.setText(u.getNickname() + " (" + u.getPhone() + ")");
            holder.tvStatus.setText("状态: " + ("ACTIVE".equals(u.getStatus()) ? "正常" : "已禁用"));
            holder.tvStatus.setTextColor("ACTIVE".equals(u.getStatus()) ? 0xFF4CAF50 : 0xFFFF0000);
            holder.btnToggle.setText("ACTIVE".equals(u.getStatus()) ? "禁用" : "启用");
            holder.btnToggle.setOnClickListener(v -> { if (toggleListener != null) toggleListener.onToggle(u); });
        }

        @Override
        public int getItemCount() { return users.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvInfo, tvStatus; Button btnToggle;
            VH(View v) {
                super(v);
                tvInfo = v.findViewById(R.id.tv_user_info);
                tvStatus = v.findViewById(R.id.tv_user_status);
                btnToggle = v.findViewById(R.id.btn_toggle_status);
            }
        }

        interface OnToggleListener { void onToggle(User user); }

        void setOnToggleListener(OnToggleListener l) { this.toggleListener = l; }
    }
}
