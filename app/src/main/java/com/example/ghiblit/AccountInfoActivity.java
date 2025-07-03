package com.example.ghiblit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ghiblit.database.Data;

public class AccountInfoActivity extends AppCompatActivity {

    EditText edtName, edtDOB, edtPhone, edtUsername;
    Button btnEdit, btnSave;
    Data dbHelper;
    String originalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        edtName = findViewById(R.id.edtName);
        edtDOB = findViewById(R.id.edtDOB);
        edtPhone = findViewById(R.id.edtPhone);
        edtUsername = findViewById(R.id.edtUsername);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        Button btnForgotPassword = findViewById(R.id.btnForgotPassword); // ✅

        dbHelper = new Data(this);

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        originalUsername = prefs.getString("username", null);

        if (originalUsername != null) {
            loadUserInfo(originalUsername);
        }

        setFieldsEnabled(false);

        btnEdit.setOnClickListener(v -> {
            setFieldsEnabled(true);
            btnEdit.setVisibility(Button.GONE);
            btnSave.setVisibility(Button.VISIBLE);
        });

        btnSave.setOnClickListener(v -> {
            updateUserInfo();
        });

        // ✅ Chuyển sang ForgotPasswordActivity
        btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(AccountInfoActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }


    private void setFieldsEnabled(boolean enabled) {
        edtName.setEnabled(enabled);
        edtDOB.setEnabled(enabled);
        edtPhone.setEnabled(enabled);
        edtUsername.setEnabled(enabled);
    }

    private void loadUserInfo(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE Username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            edtName.setText(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
            edtDOB.setText(cursor.getString(cursor.getColumnIndexOrThrow("DateOfBirth")));
            edtPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("Phone")));
            edtUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("Username")));
        }

        cursor.close();
        db.close();
    }

    private void updateUserInfo() {
        String name = edtName.getText().toString().trim();
        String dob = edtDOB.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String newUsername = edtUsername.getText().toString().trim();

        if (name.isEmpty() || dob.isEmpty() || phone.isEmpty() || newUsername.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Nếu đổi sang username mới, kiểm tra có bị trùng không
        if (!newUsername.equals(originalUsername)) {
            Cursor checkCursor = db.rawQuery("SELECT * FROM User WHERE Username = ?", new String[]{newUsername});
            if (checkCursor.moveToFirst()) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                checkCursor.close();
                db.close();
                return;
            }
            checkCursor.close();
        }

        // Cập nhật
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("DateOfBirth", dob);
        values.put("Phone", phone);
        values.put("Username", newUsername);

        int rows = db.update("User", values, "Username = ?", new String[]{originalUsername});
        db.close();

        if (rows > 0) {
            // Nếu đổi username thì lưu lại trong SharedPreferences
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            prefs.edit().putString("username", newUsername).apply();

            originalUsername = newUsername;

            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }

        // Đóng chỉnh sửa
        setFieldsEnabled(false);
        btnEdit.setVisibility(Button.VISIBLE);
        btnSave.setVisibility(Button.GONE);
    }
}
