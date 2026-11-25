package com.b24dccc073.quanlykhohang.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.b24dccc073.quanlykhohang.R;
import com.b24dccc073.quanlykhohang.db.DatabaseHelper;
import com.b24dccc073.quanlykhohang.model.Product;
import com.b24dccc073.quanlykhohang.util.CurrencyFormatter;

public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product currentProduct;

    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailStatus;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailLowStock;
    private TextView tvDetailStockValue;
    private TextView tvDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);

        ImageButton btnHeaderBack = findViewById(R.id.btnHeaderBack);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailLowStock = findViewById(R.id.tvDetailLowStock);
        tvDetailStockValue = findViewById(R.id.tvDetailStockValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        Button btnImportOne = findViewById(R.id.btnImportOne);
        Button btnSellOne = findViewById(R.id.btnSellOne);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        btnHeaderBack.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnImportOne.setOnClickListener(v -> handleImportOne());
        btnSellOne.setOnClickListener(v -> handleSellOne());
        btnDeleteProduct.setOnClickListener(v -> confirmDelete());

        loadProduct();
    }

    private void loadProduct() {
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText(currentProduct.getCategory());
        tvDetailStatus.setVisibility(currentProduct.isActive() ? View.GONE : View.VISIBLE);
        tvDetailPrice.setText(CurrencyFormatter.formatVnd(currentProduct.getPrice()));
        tvDetailQuantity.setText(String.valueOf(currentProduct.getQuantity()));
        tvDetailLowStock.setVisibility(currentProduct.isLowStock() ? View.VISIBLE : View.GONE);
        tvDetailStockValue.setText(CurrencyFormatter.formatVnd(currentProduct.getStockValue()));

        String desc = currentProduct.getDescription();
        tvDetailDescription.setText(
                (desc == null || desc.trim().isEmpty())
                        ? getString(R.string.no_description)
                        : desc
        );
    }

    private void handleImportOne() {
        if (currentProduct == null) return;
        int newQuantity = currentProduct.getQuantity() + 1;
        dbHelper.updateQuantity(productId, newQuantity);
        loadProduct();
    }

    private void handleSellOne() {
        if (currentProduct == null) return;
        if (currentProduct.getQuantity() == 0) {
            Toast.makeText(this, R.string.msg_out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        }
        int newQuantity = currentProduct.getQuantity() - 1;
        dbHelper.updateQuantity(productId, newQuantity);
        loadProduct();
    }

    private void confirmDelete() {
        if (currentProduct == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa \"" + currentProduct.getName() + "\" khỏi kho?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteProduct(productId);
                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
