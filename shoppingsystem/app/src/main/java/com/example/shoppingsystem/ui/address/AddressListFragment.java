package com.example.shoppingsystem.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.AddressAdapter;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.util.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddressListFragment extends Fragment {

    private RecyclerView rvAddresses;
    private FloatingActionButton fabAdd;
    private AddressAdapter adapter;
    private AddressViewModel viewModel;
    private SessionManager sessionManager;
    private boolean selectMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);

        sessionManager = SessionManager.getInstance(requireContext());
        long userId = sessionManager.getUserId();

        rvAddresses = view.findViewById(R.id.rv_addresses);
        fabAdd = view.findViewById(R.id.fab_add_address);

        // Check select mode (used from order confirm)
        if (getArguments() != null) {
            selectMode = getArguments().getBoolean("selectMode", false);
        }

        adapter = new AddressAdapter(requireContext());
        rvAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAddresses.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        viewModel.loadAddresses(userId);
        viewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
            adapter.setAddresses(addresses);
        });

        fabAdd.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("addressId", -1);
            Navigation.findNavController(v).navigate(R.id.action_addressList_to_addressEdit, args);
        });

        adapter.setOnAddressClickListener(address -> {
            if (selectMode) {
                // Return selected address to previous fragment
                Bundle result = new Bundle();
                result.putLong("selectedAddressId", address.getId());
                getParentFragmentManager().setFragmentResult("address_selected", result);
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        adapter.setOnAddressActionListener(new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onEdit(Address address) {
                Bundle args = new Bundle();
                args.putLong("addressId", address.getId());
                Navigation.findNavController(requireView()).navigate(R.id.action_addressList_to_addressEdit, args);
            }

            @Override
            public void onDelete(Address address) {
                viewModel.deleteAddress(address.getId());
            }
        });

        return view;
    }
}
