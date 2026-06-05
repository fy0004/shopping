package com.example.shoppingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final Context context;
    private List<Address> addresses = new ArrayList<>();
    private OnAddressClickListener clickListener;
    private OnAddressActionListener actionListener;

    public AddressAdapter(Context context) {
        this.context = context;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address addr = addresses.get(position);
        holder.tvReceiverInfo.setText(addr.getReceiverName() + "  " + addr.getPhone());
        holder.tvAddressFull.setText(addr.getFullAddress());
        holder.tvDefaultTag.setVisibility(addr.isDefault() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(addr);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(addr);
            return true;
        });
    }

    @Override
    public int getItemCount() { return addresses.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverInfo, tvAddressFull, tvDefaultTag;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverInfo = itemView.findViewById(R.id.tv_receiver_info);
            tvAddressFull = itemView.findViewById(R.id.tv_address_full);
            tvDefaultTag = itemView.findViewById(R.id.tv_default_tag);
        }
    }

    public interface OnAddressClickListener {
        void onClick(Address address);
    }

    public interface OnAddressActionListener {
        void onEdit(Address address);
        void onDelete(Address address);
    }

    public void setOnAddressClickListener(OnAddressClickListener listener) { this.clickListener = listener; }
    public void setOnAddressActionListener(OnAddressActionListener listener) { this.actionListener = listener; }
}
