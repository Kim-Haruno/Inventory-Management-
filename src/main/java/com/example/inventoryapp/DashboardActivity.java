package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvBusinessName, tvTotalItems, tvTotalValue;
    Button btnLogout, btnGoToInventory, btnGoToReports;
    DBHelper dbHelper;
    SessionManager sessionManager;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        btnLogout = findViewById(R.id.btnLogout);
        btnGoToInventory = findViewById(R.id.btnGoToInventory);
        btnGoToReports = findViewById(R.id.btnGoToReports);
        bottomNav = findViewById(R.id.bottomNav);

        tvBusinessName.setText(sessionManager.getBusinessName());
        tvWelcome.setVisibility(android.view.View.GONE);

        loadStats();

        btnGoToInventory.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryActivity.class)));
        btnGoToReports.setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class)));
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                return true;
            }
            if (id == R.id.nav_sell) {
                startActivity(new Intent(this, SellActivity.class));
                return true;
            }
            if (id == R.id.nav_reports) {
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        ArrayList<Item> items = dbHelper.getAllItems();
        int count = items.size();
        int lowStock = 0;
        for (Item item : items) {
            if (item.isRed() || item.isYellow()) lowStock++;
        }
        tvTotalItems.setText(String.valueOf(count));
        tvTotalValue.setText(String.valueOf(lowStock));
    }
}
