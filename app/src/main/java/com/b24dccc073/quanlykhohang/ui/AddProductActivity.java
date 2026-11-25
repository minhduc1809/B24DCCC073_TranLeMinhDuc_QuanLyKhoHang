package com.b24dccc073.quanlykhohang.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.b24dccc073.quanlykhohang.R;
import com.b24dccc073.quanlykhohang.db.DatabaseHelper;
import com.b24dccc073.quanlykhohang.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spnCategory;
    private SwitchCompat swActive;
    private EditText edtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        ImageButton btnHeaderBack = findViewById(R.id.btnHeaderBack);
        edtName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtProductPrice);
        edtQuantity = findViewById(R.id.edtProductQuantity);
        spnCategory = findViewById(R.id.spnCategory);
        swActive = findViewById(R.id.swActive);
        edtDescription = findViewById(R.id.edtDescription);
        Button btnSaveProduct = findViewById(R.id.btnSaveProduct);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);

        btnHeaderBack.setOnClickListener(v -> finish());
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên sản phẩm");
            edtName.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceText);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Vui lòng nhập giá bán hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Vui lòng nhập số lượng hợp lệ");
            edtQuantity.requestFocus();
            return;
        }

        String category = spnCategory.getSelectedItem().toString();
        boolean active = swActive.isChecked();

        Product newProduct = new Product(name, price, quantity, category, active, description);
        long insertedId = dbHelper.insertProduct(newProduct);

        if (insertedId > 0) {
            Toast.makeText(this, "Đã lưu sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Không thể lưu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
