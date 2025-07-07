package com.example.ghiblit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ghiblit.database.Data;
import com.example.ghiblit.utils.SessionManager; // 🟢 Thêm dòng này

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister;
    CheckBox chkRemember;

    Data dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // luôn hiển thị màn hình login

        dbHelper = new Data(this);

        // Thêm dữ liệu mẫu chỉ 1 lần duy nhất
        SharedPreferences firstPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstRun = firstPrefs.getBoolean("first_run", true);
        if (isFirstRun) {
            dbHelper.addSampleData();
            SharedPreferences.Editor firstEditor = firstPrefs.edit();
            firstEditor.putBoolean("first_run", false);
            firstEditor.apply();
        }

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        chkRemember = findViewById(R.id.chkRemember);
        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // tự động điền nếu đã lưu
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);

        edtUsername.setText(savedUsername);
        edtPassword.setText(savedPassword);
        chkRemember.setChecked(isRemembered);

        btnLogin.setOnClickListener(v -> handleLogin());

        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE Username = ? AND Password = ?",
                new String[]{username, password}
        );

        if (cursor.moveToFirst()) {
            int uid = cursor.getInt(cursor.getColumnIndexOrThrow("UID")); // Lấy UID
            cursor.close();

            // Lưu UID vào SessionManager
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.saveUserId(uid);

            Toast.makeText(this, "Đăng nhập thành công! Chào " + username, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (chkRemember.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("remember", true);
            } else {
                editor.clear(); // không ghi nhớ
            }
            editor.apply();

            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("user_name", username); // vẫn giữ nếu bạn cần hiển thị tên
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}
