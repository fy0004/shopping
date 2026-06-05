package com.example.shoppingsystem.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.model.CartWithProduct;
import com.example.shoppingsystem.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private List<CartWithProduct> items = new ArrayList<>();

    private OnItemChangeListener changeListener;

    public CartAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<CartWithProduct> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartWithProduct item = items.get(position);

        holder.cbSelected.setOnCheckedChangeListener(null);
        holder.cbSelected.setChecked(item.isSelected());
        holder.cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (changeListener != null) changeListener.onToggleSelected(item.getId(), isChecked);
        });

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(PriceFormatter.format(item.getProductPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        String imgUrl = item.getFirstImage();
        Glide.with(context).load(imgUrl).into(holder.ivImage);

        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getQuantity() - 1;
            if (qty >= 1) {
                item.setQuantity(qty);
                holder.tvQuantity.setText(String.valueOf(qty));
                if (changeListener != null) changeListener.onQuantityChanged(item.getId(), qty);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getQuantity() + 1;
            item.setQuantity(qty);
            holder.tvQuantity.setText(String.valueOf(qty));
            if (changeListener != null) changeListener.onQuantityChanged(item.getId(), qty);
        });

        holder.itemView.setOnClickListener(v -> {
            holder.cbSelected.setChecked(!holder.cbSelected.isChecked());
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        Button btnMinus, btnPlus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelected = itemView.findViewById(R.id.cb_item);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }

    public interface OnItemChangeListener {
        void onToggleSelected(long itemId, boolean selected);
        void onQuantityChanged(long itemId, int quantity);
    }

    public void setOnItemChangeListener(OnItemChangeListener listener) { this.changeListener = listener; }

    /**
     * Returns current items for checking selected state.
     */
    public List<CartWithProduct> getCurrentItems() {
        return items;
    }
}
