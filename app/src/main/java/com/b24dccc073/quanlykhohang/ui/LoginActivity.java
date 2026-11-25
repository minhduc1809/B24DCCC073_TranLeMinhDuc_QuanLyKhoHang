package com.b24dccc073.quanlykhohang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.b24dccc073.quanlykhohang.R;
import com.b24dccc073.quanlykhohang.db.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvLoginError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvLoginError = findViewById(R.id.tvLoginError);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_login));
            return;
        }

        if (dbHelper.checkLogin(username, password)) {
            tvLoginError.setVisibility(View.GONE);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            showError(getString(R.string.error_invalid_login));
        }
    }

    private void showError(String message) {
        tvLoginError.setText(message);
        tvLoginError.setVisibility(View.VISIBLE);
    }
}
