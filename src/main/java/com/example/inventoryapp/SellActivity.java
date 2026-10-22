package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class SellActivity extends AppCompatActivity {
    EditText etSearchSell;
    RecyclerView recyclerViewSell;
    DBHelper dbHelper;
    SellAdapter sellAdapter;
    ArrayList<Item> itemList;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        dbHelper = new DBHelper(this);
        etSearchSell = findViewById(R.id.etSearchSell);
        recyclerViewSell = findViewById(R.id.recyclerViewSell);
        bottomNav = findViewById(R.id.bottomNav);
        recyclerViewSell.setLayoutManager(new LinearLayoutManager(this));

        loadItems();

        etSearchSell.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        bottomNav.setSelectedItemId(R.id.nav_sell);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_sell) return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            }
            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
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
        loadItems();
    }

    public void loadItems() {
        itemList = dbHelper.getAllItems();
        sellAdapter = new SellAdapter(this, itemList, dbHelper, this);
        recyclerViewSell.setAdapter(sellAdapter);
    }

    private void filterItems(String query) {
        ArrayList<Item> filtered;
        if (query.isEmpty()) {
            filtered = dbHelper.getAllItems();
        } else {
            filtered = dbHelper.searchItems(query);
        }
        sellAdapter = new SellAdapter(this, filtered, dbHelper, this);
        recyclerViewSell.setAdapter(sellAdapter);
    }
}
