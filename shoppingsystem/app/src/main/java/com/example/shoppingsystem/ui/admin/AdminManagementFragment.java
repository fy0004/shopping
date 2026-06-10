package com.example.shoppingsystem.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminManagementFragment extends Fragment {

    private RecyclerView rvAdmins;
    private FloatingActionButton fabAdd;
    private AdminAdapter adminAdapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_management, container, false);

        rvAdmins = view.findViewById(R.id.rv_admins);
        fabAdd = view.findViewById(R.id.fab_add_admin);

        adminAdapter = new AdminAdapter();
        rvAdmins.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAdmins.setAdapter(adminAdapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        loadAdmins();

        fabAdd.setOnClickListener(v -> showEditDialog(null));

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) loadAdmins();
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadAdmins() {
        viewModel.loadAdminList();
        viewModel.getAdminList().observe(getViewLifecycleOwner(), admins -> adminAdapter.setAdmins(admins));
    }

    // ===== Adapter =====

    private class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.VH> {
        private List<User> admins = new ArrayList<>();

        void setAdmins(List<User> list) { this.admins = list != null ? list : new ArrayList<>(); notifyDataSetChanged(); }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(16, 8, 16, 8);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tv = new TextView(requireContext());
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tv.setLayoutParams(tp);
            tv.setTextSize(14);
            tv.setPadding(0, 8, 0, 8);

            Button btnDel = new Button(requireContext());
            btnDel.setText("删除");
            btnDel.setTextSize(11);
            btnDel.setMinWidth(0);
            btnDel.setPadding(8, 0, 8, 0);

            row.addView(tv);
            row.addView(btnDel);

            VH vh = new VH(row);
            vh.tv = tv;
            vh.btnDel = btnDel;
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            User u = admins.get(pos);
            holder.tv.setText(u.getNickname() + "  (" + u.getPhone() + ")");
            holder.tv.setOnClickListener(vi -> showEditDialog(u));
            holder.btnDel.setOnClickListener(vi -> new AlertDialog.Builder(requireContext())
                    .setTitle("删除管理员")
                    .setMessage("确定要删除" + u.getNickname() + "吗？")
                    .setPositiveButton("删除", (d, w) -> viewModel.deleteAdmin(u.getId()))
                    .setNegativeButton("取消", null).show());
        }

        @Override public int getItemCount() { return admins.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tv;
            Button btnDel;
            VH(View v) { super(v); }
        }
    }

    // ===== Dialog =====

    private void showEditDialog(@Nullable User admin) {
        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        b.setTitle(admin == null ? "新增管理员" : "编辑管理员");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 0);

        EditText etPhone = new EditText(requireContext());
        etPhone.setHint("手机号");
        if (admin != null) etPhone.setText(admin.getPhone());
        layout.addView(etPhone);

        EditText etNickname = new EditText(requireContext());
        etNickname.setHint("昵称");
        if (admin != null) etNickname.setText(admin.getNickname());
        layout.addView(etNickname);

        EditText etPwd = new EditText(requireContext());
        etPwd.setHint(admin == null ? "密码(至少6位)" : "新密码(留空不改)");
        layout.addView(etPwd);

        b.setView(layout);

        b.setPositiveButton("保存", (d, w) -> {
            String phone = etPhone.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            if (phone.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(requireContext(), "手机号和昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (admin == null) {
                if (pwd.length() < 6) { Toast.makeText(requireContext(), "密码至少6位", Toast.LENGTH_SHORT).show(); return; }
                viewModel.createAdmin(phone, pwd, nickname);
            } else {
                viewModel.updateAdmin(admin.getId(), nickname, pwd.isEmpty() ? null : pwd);
            }
        });
        b.setNegativeButton("取消", null);
        b.show();
    }
}
