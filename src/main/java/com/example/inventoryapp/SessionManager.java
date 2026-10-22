package com.example.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "InventorySession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BUSINESS = "business";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void login(String email, String businessName) {
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BUSINESS, businessName);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getString(KEY_EMAIL, null) != null;
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "User");
    }

    public String getBusinessName() {
        return prefs.getString(KEY_BUSINESS, "My Business");
    }
}
