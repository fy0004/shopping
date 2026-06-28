package com.example.shoppingsystem.ui.address;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AddressEditFragment extends Fragment {

    private EditText etReceiverName, etPhone, etDetail, etRegion;
    private SwitchMaterial switchDefault;
    private Button btnSave, btnDelete;
    private AddressViewModel viewModel;
    private SessionManager sessionManager;
    private long addressId = -1;

    private final TreeMap<String, Map<String, List<String>>> regionData = new TreeMap<>();
    private String savedProvince = "", savedCity = "", savedDistrict = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_edit, container, false);

        sessionManager = SessionManager.getInstance(requireContext());
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        etReceiverName = view.findViewById(R.id.et_receiver_name);
        etPhone = view.findViewById(R.id.et_phone);
        etRegion = view.findViewById(R.id.et_region);
        etDetail = view.findViewById(R.id.et_detail);
        switchDefault = view.findViewById(R.id.switch_default);
        btnSave = view.findViewById(R.id.btn_save_address);
        btnDelete = view.findViewById(R.id.btn_delete_address);

        // 从 assets 加载省市区数据
        loadRegionData();

        // 点击弹出三级联动选择
        etRegion.setOnClickListener(v -> showRegionPicker());

        if (getArguments() != null) {
            addressId = getArguments().getLong("addressId", -1);
        }

        if (addressId > 0) {
            btnDelete.setVisibility(View.VISIBLE);
            viewModel.loadAddresses(sessionManager.getUserId());
            viewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
                if (addresses != null) {
                    for (Address addr : addresses) {
                        if (addr.getId() == addressId) {
                            etReceiverName.setText(addr.getReceiverName());
                            etPhone.setText(addr.getPhone());
                            savedProvince = addr.getProvince();
                            savedCity = addr.getCity();
                            savedDistrict = addr.getDistrict();
                            etRegion.setText(savedProvince + " " + savedCity + " " + savedDistrict);
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
            address.setProvince(savedProvince);
            address.setCity(savedCity);
            address.setDistrict(savedDistrict);
            address.setDetail(etDetail.getText().toString().trim());
            address.setDefault(switchDefault.isChecked());

            if (TextUtils.isEmpty(address.getReceiverName()) || TextUtils.isEmpty(address.getPhone())) {
                Toast.makeText(requireContext(), "请填写姓名和手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.saveAddress(address);
        });

        btnDelete.setOnClickListener(v -> {
            if (addressId > 0) viewModel.deleteAddress(addressId);
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

    private void loadRegionData() {
        try {
            InputStream is = requireContext().getAssets().open("regions.json");
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            JSONObject json = new JSONObject(new String(buf, "UTF-8"));
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String province = it.next();
                JSONObject citiesJson = json.getJSONObject(province);
                TreeMap<String, List<String>> citiesMap = new TreeMap<>();
                for (Iterator<String> cit = citiesJson.keys(); cit.hasNext(); ) {
                    String city = cit.next();
                    List<String> districts = new ArrayList<>();
                    for (int i = 0; i < citiesJson.getJSONArray(city).length(); i++) {
                        districts.add(citiesJson.getJSONArray(city).getString(i));
                    }
                    citiesMap.put(city, districts);
                }
                regionData.put(province, citiesMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegionPicker() {
        if (regionData.isEmpty()) return;

        String[] provinces = regionData.keySet().toArray(new String[0]);
        new AlertDialog.Builder(requireContext())
                .setTitle("选择省份")
                .setItems(provinces, (dialog, which) -> {
                    String province = provinces[which];
                    Map<String, List<String>> cities = regionData.get(province);
                    if (cities == null) return;
                    String[] cityNames = cities.keySet().toArray(new String[0]);
                    new AlertDialog.Builder(requireContext())
                            .setTitle("选择城市")
                            .setItems(cityNames, (dialog2, which2) -> {
                                String city = cityNames[which2];
                                List<String> districts = cities.get(city);
                                if (districts == null) return;
                                String[] districtNames = districts.toArray(new String[0]);
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("选择区/县")
                                        .setItems(districtNames, (dialog3, which3) -> {
                                            String district = districtNames[which3];
                                            savedProvince = province;
                                            savedCity = city;
                                            savedDistrict = district;
                                            etRegion.setText(province + " " + city + " " + district);
                                        }).show();
                            }).show();
                }).show();
    }
}
