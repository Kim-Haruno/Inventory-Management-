package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {

    TextView tvReportTotal, tvReportLowStock, tvReportHealthy;
    RecyclerView recyclerViewReport;
    DBHelper dbHelper;
    BottomNavigationView bottomNav;
    Button btnViewSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        tvReportTotal = findViewById(R.id.tvReportTotal);
        tvReportLowStock = findViewById(R.id.tvReportLowStock);
        tvReportHealthy = findViewById(R.id.tvReportHealthy);
        recyclerViewReport = findViewById(R.id.recyclerViewReport);
        bottomNav = findViewById(R.id.bottomNav);
        btnViewSales = findViewById(R.id.btnViewSales);

        recyclerViewReport.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        loadReport();

        btnViewSales.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, SalesActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_reports);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_reports) return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            }
            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                return true;
            }
            if (id == R.id.nav_sell) {
                startActivity(new Intent(this, SellActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReport();
    }

    private void loadReport() {
        ArrayList<Item> items = dbHelper.getAllItems();
        int total = items.size();
        int low = 0;
        int healthy = 0;

        for (Item item : items) {
            if (item.isRed() || item.isYellow()) low++;
            else healthy++;
        }

        tvReportTotal.setText("Total Items: " + total);
        tvReportLowStock.setText("Low Stock Items: " + low);
        tvReportHealthy.setText("Healthy Stock Items: " + healthy);

        ItemAdapter adapter = new ItemAdapter(this, items, dbHelper, this::loadReport);
        recyclerViewReport.setAdapter(adapter);
    }
}
