package com.example.shoppingsystem.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.util.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddressEditFragment extends Fragment {

    private EditText etReceiverName, etPhone, etProvince, etCity, etDistrict, etDetail;
    private SwitchMaterial switchDefault;
    private Button btnSave, btnDelete;
    private AddressViewModel viewModel;
    private SessionManager sessionManager;
    private long addressId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_edit, container, false);

        sessionManager = SessionManager.getInstance(requireContext());

        etReceiverName = view.findViewById(R.id.et_receiver_name);
        etPhone = view.findViewById(R.id.et_phone);
        etProvince = view.findViewById(R.id.et_province);
        etCity = view.findViewById(R.id.et_city);
        etDistrict = view.findViewById(R.id.et_district);
        etDetail = view.findViewById(R.id.et_detail);
        switchDefault = view.findViewById(R.id.switch_default);
        btnSave = view.findViewById(R.id.btn_save_address);
        btnDelete = view.findViewById(R.id.btn_delete_address);

        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        if (getArguments() != null) {
            addressId = getArguments().getLong("addressId", -1);
        }

        if (addressId > 0) {
            // Edit mode: 从地址列表中查找
            btnDelete.setVisibility(View.VISIBLE);
            viewModel.loadAddresses(sessionManager.getUserId());
            viewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
                if (addresses != null) {
                    for (Address addr : addresses) {
                        if (addr.getId() == addressId) {
                            etReceiverName.setText(addr.getReceiverName());
                            etPhone.setText(addr.getPhone());
                            etProvince.setText(addr.getProvince());
                            etCity.setText(addr.getCity());
                            etDistrict.setText(addr.getDistrict());
                            etDetail.setText(addr.getDetail());
                            switchDefault.setChecked(addr.isDefault());
                            break;
                        }
                    }
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            Address address = new Address();
            address.setId(addressId > 0 ? addressId : 0);
            address.setUserId(sessionManager.getUserId());
            address.setReceiverName(etReceiverName.getText().toString().trim());
            address.setPhone(etPhone.getText().toString().trim());
            address.setProvince(etProvince.getText().toString().trim());
            address.setCity(etCity.getText().toString().trim());
            address.setDistrict(etDistrict.getText().toString().trim());
            address.setDetail(etDetail.getText().toString().trim());
            address.setDefault(switchDefault.isChecked());
            viewModel.saveAddress(address);
        });

        btnDelete.setOnClickListener(v -> {
            if (addressId > 0) {
                viewModel.deleteAddress(addressId);
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
