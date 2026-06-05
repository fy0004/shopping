package com.example.shoppingsystem.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 管理登录状态和用户会话的 SharedPreferences 工具类。
 */
public class SessionManager {

    private static final String PREF_NAME = "shopping_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_NICKNAME = "user_nickname";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_ADMIN_LOGGED_IN = "is_admin_logged_in";
    private static final String KEY_ADMIN_ID = "admin_id";

    private final SharedPreferences prefs;

    private static SessionManager instance;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    // ========== 用户登录 ==========

    public void saveUserSession(long userId, String phone, String nickname, String role) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_USER_PHONE, phone)
                .putString(KEY_USER_NICKNAME, nickname)
                .putString(KEY_USER_ROLE, role)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public String getUserNickname() {
        return prefs.getString(KEY_USER_NICKNAME, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "USER");
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getUserRole());
    }

    public void updateNickname(String nickname) {
        prefs.edit().putString(KEY_USER_NICKNAME, nickname).apply();
    }

    // ========== 管理员登录（独立于普通用户） ==========

    public void saveAdminSession(long adminId, String phone, String nickname) {
        prefs.edit()
                .putBoolean(KEY_IS_ADMIN_LOGGED_IN, true)
                .putLong(KEY_ADMIN_ID, adminId)
                .putString(KEY_USER_PHONE, phone)
                .putString(KEY_USER_NICKNAME, nickname)
                .putString(KEY_USER_ROLE, "ADMIN")
                .apply();
    }

    public boolean isAdminLoggedIn() {
        return prefs.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false);
    }

    public long getAdminId() {
        return prefs.getLong(KEY_ADMIN_ID, -1);
    }

    // ========== 退出登录 ==========

    public void logout() {
        prefs.edit().clear().apply();
    }

    public void logoutAdmin() {
        prefs.edit()
                .remove(KEY_IS_ADMIN_LOGGED_IN)
                .remove(KEY_ADMIN_ID)
                .apply();
    }
}
