package com.example.ghiblit.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "GhibliSession";
    private static final String KEY_UID = "uid";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Lưu UID sau khi đăng nhập
    public void saveUserId(int uid) {
        editor.putInt(KEY_UID, uid);
        editor.apply();
    }

    // Lấy UID hiện tại
    public int getUserId() {
        return prefs.getInt(KEY_UID, -1); // Trả về -1 nếu chưa đăng nhập
    }

    // Xóa session (đăng xuất)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return getUserId() != -1;
    }
}
