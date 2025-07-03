package com.example.ghiblit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ghiblit.database.Data;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtPhone, edtDOB, edtNewPassword;
    Button btnCheck, btnResetPassword;
    Data dbHelper;
    String foundUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtPhone = findViewById(R.id.edtPhone);
        edtDOB = findViewById(R.id.edtDOB);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnCheck = findViewById(R.id.btnCheck);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        dbHelper = new Data(this);

        btnCheck.setOnClickListener(v -> verifyUser());

        btnResetPassword.setOnClickListener(v -> updatePassword());
    }

    private void verifyUser() {
        String phone = edtPhone.getText().toString().trim();
        String dob = edtDOB.getText().toString().trim();

        if (phone.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ SĐT và ngày sinh", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Username FROM User WHERE Phone = ? AND DateOfBirth = ?",
                new String[]{phone, dob}
        );

        if (cursor.moveToFirst()) {
            foundUsername = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            Toast.makeText(this, "Tài khoản: " + foundUsername, Toast.LENGTH_SHORT).show();
            edtNewPassword.setVisibility(EditText.VISIBLE);
            btnResetPassword.setVisibility(Button.VISIBLE);
        } else {
            Toast.makeText(this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private void updatePassword() {
        String newPassword = edtNewPassword.getText().toString().trim();

        if (foundUsername == null) {
            Toast.makeText(this, "Bạn chưa xác minh tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Password", newPassword);

        int rows = db.update("User", values, "Username = ?", new String[]{foundUsername});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}
