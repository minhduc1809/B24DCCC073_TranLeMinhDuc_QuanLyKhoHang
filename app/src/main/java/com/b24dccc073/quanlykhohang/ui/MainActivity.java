package com.b24dccc073.quanlykhohang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.b24dccc073.quanlykhohang.R;
import com.b24dccc073.quanlykhohang.adapter.ProductAdapter;
import com.b24dccc073.quanlykhohang.db.DatabaseHelper;
import com.b24dccc073.quanlykhohang.model.Product;
import com.b24dccc073.quanlykhohang.util.CurrencyFormatter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private TextView tvEmptyState;
    private TextView tvTapHint;
    private RecyclerView rvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTapHint = findViewById(R.id.tvTapHint);
        rvProducts = findViewById(R.id.rvProducts);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWarehouseData();
    }

    private void loadWarehouseData() {
        List<Product> products = dbHelper.getAllProducts();
        long totalValue = dbHelper.getTotalValue();
        int lowStockCount = dbHelper.countLowStock();

        tvTotalProducts.setText(String.valueOf(products.size()));
        tvTotalValue.setText(CurrencyFormatter.formatVnd(totalValue));
        tvLowStockCount.setText(String.valueOf(lowStockCount));

        adapter.setProducts(products);

        boolean isEmpty = products.isEmpty();
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvTapHint.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
