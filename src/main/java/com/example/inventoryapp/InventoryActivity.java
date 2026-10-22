package com.example.inventoryapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    EditText etItemName, etItemQuantity, etItemCategory, etItemExpected, etItemSku, etItemBarcode, etItemCostPrice, etSearch;
    Button btnAddItem;
    RecyclerView recyclerView;
    DBHelper dbHelper;
    ItemAdapter itemAdapter;
    ArrayList<Item> itemList;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        dbHelper = new DBHelper(this);
        etItemName = findViewById(R.id.etItemName);
        etItemQuantity = findViewById(R.id.etItemQuantity);
        etItemCategory = findViewById(R.id.etItemCategory);
        etItemExpected = findViewById(R.id.etItemExpected);
        etItemSku = findViewById(R.id.etItemSku);
        etItemBarcode = findViewById(R.id.etItemBarcode);
        etItemCostPrice = findViewById(R.id.etItemCostPrice);
        etSearch = findViewById(R.id.etSearch);
        btnAddItem = findViewById(R.id.btnAddItem);
        recyclerView = findViewById(R.id.recyclerViewItems);
        bottomNav = findViewById(R.id.bottomNav);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();

        btnAddItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String qtyStr = etItemQuantity.getText().toString().trim();
            String category = etItemCategory.getText().toString().trim();
            String expectedStr = etItemExpected.getText().toString().trim();
            String sku = etItemSku.getText().toString().trim();
            String barcode = etItemBarcode.getText().toString().trim();
            String costStr = etItemCostPrice.getText().toString().trim();

            if (name.isEmpty() || qtyStr.isEmpty() || expectedStr.isEmpty()) {
                Toast.makeText(this, "Please fill in item name, quantity, and expected quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int expectedQty;
            try {
                expectedQty = Integer.parseInt(expectedStr);
                if (expectedQty <= 0) {
                    Toast.makeText(this, "Expected quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid expected quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            double costPrice = 0.0;
            if (!costStr.isEmpty()) {
                try {
                    costPrice = Double.parseDouble(costStr);
                } catch (NumberFormatException ignored) {}
            }

            if (category.isEmpty()) category = "General";

            dbHelper.addItem(name, qty, category, expectedQty, sku, barcode, costPrice);
            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();

            etItemName.setText("");
            etItemQuantity.setText("");
            etItemCategory.setText("");
            etItemExpected.setText("");
            etItemSku.setText("");
            etItemBarcode.setText("");
            etItemCostPrice.setText("");
            loadItems();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        bottomNav.setSelectedItemId(R.id.nav_inventory);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inventory) return true;
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardActivity.class));
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

    public void loadItems() {
        itemList = dbHelper.getAllItems();
        itemAdapter = new ItemAdapter(this, itemList, dbHelper, this);
        recyclerView.setAdapter(itemAdapter);
    }

    private void filterItems(String query) {
        ArrayList<Item> filtered;
        if (query.isEmpty()) {
            filtered = dbHelper.getAllItems();
        } else {
            filtered = dbHelper.searchItems(query);
        }
        itemAdapter = new ItemAdapter(this, filtered, dbHelper, this);
        recyclerView.setAdapter(itemAdapter);
    }
}
