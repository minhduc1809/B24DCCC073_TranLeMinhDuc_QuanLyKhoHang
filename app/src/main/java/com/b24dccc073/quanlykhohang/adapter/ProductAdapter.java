package com.b24dccc073.quanlykhohang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.b24dccc073.quanlykhohang.R;
import com.b24dccc073.quanlykhohang.model.Product;
import com.b24dccc073.quanlykhohang.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        items.clear();
        if (products != null) {
            items.addAll(products);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = items.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvCategory;
        private final TextView tvLowStock;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvLowStock = itemView.findViewById(R.id.tvItemLowStock);
        }

        void bind(Product product, OnProductClickListener listener) {
            tvName.setText(product.getName());
            tvPrice.setText("Giá bán: " + CurrencyFormatter.formatVnd(product.getPrice()));
            tvQuantity.setText("SL: " + product.getQuantity());
            tvCategory.setText(product.getCategory());

            boolean lowStock = product.isLowStock();
            tvLowStock.setVisibility(lowStock ? View.VISIBLE : View.GONE);
            int qtyColor = ContextCompat.getColor(
                    itemView.getContext(),
                    lowStock ? R.color.danger : R.color.text_primary
            );
            tvQuantity.setTextColor(qtyColor);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
