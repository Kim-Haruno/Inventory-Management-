package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

public class SalesActivity extends AppCompatActivity {
    View layoutSalesRoot;
    CardView cardReport;
    TextView tvSalesTitle, tvSalesReport;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        layoutSalesRoot = findViewById(R.id.layoutSalesRoot);
        cardReport = findViewById(R.id.cardReport);
        tvSalesTitle = findViewById(R.id.tvSalesTitle);
        tvSalesReport = findViewById(R.id.tvSalesReport);
        dbHelper = new DBHelper(this);

        generateReport();
    }

    private void generateReport() {
        ArrayList<Item> allItems = dbHelper.getAllItems();

        int totalProducts = allItems.size();
        int totalUnits = 0;
        int redCount = 0;
        int yellowCount = 0;
        int healthyCount = 0;

        for (Item item : allItems) {
            totalUnits += item.getQuantity();
            if (item.isRed()) {
                redCount++;
            } else if (item.isYellow()) {
                yellowCount++;
            } else {
                healthyCount++;
            }
        }

        int totalLowStock = redCount + yellowCount;
        String currentDate = new SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale.getDefault()).format(new Date());

        StringBuilder report = new StringBuilder();
        report.append("Daily Summary\n\n");
        report.append("Date: ").append(currentDate).append("\n\n");
        report.append("Inventory Stats:\n");
        report.append("- Number of Products : ").append(totalProducts).append("\n");
        report.append("- Number of units : ").append(totalUnits).append("\n\n");
        report.append("Status:\n");
        report.append("- Healthy Items : ").append(healthyCount).append("\n");
        report.append("- Low Stock items : ").append(totalLowStock).append("\n\n");

        if (totalProducts == 0) {
            layoutSalesRoot.setBackgroundColor(0xFFEFF6FF);
            cardReport.setCardBackgroundColor(0xFFFFFFFF);
            tvSalesTitle.setTextColor(0xFF1E3A8A);
            tvSalesReport.setTextColor(0xFF0F172A);
            report.append("INFO: No products found in inventory.");
        } else if (redCount > 0) {
            layoutSalesRoot.setBackgroundColor(0xFFFEF2F2);
            cardReport.setCardBackgroundColor(0xFFFFF5F5);
            tvSalesTitle.setTextColor(0xFF991B1B);
            tvSalesReport.setTextColor(0xFF7F1D1D);
            report.append("RED - CRITICAL: ").append(redCount).append(" product s are below 20% of expected target, Action is needed. Please restock.");
        } else if (yellowCount > 0) {
            layoutSalesRoot.setBackgroundColor(0xFFFEFCE8);
            cardReport.setCardBackgroundColor(0xFFFFFBEB);
            tvSalesTitle.setTextColor(0xFF854D0E);
            tvSalesReport.setTextColor(0xFF713F12);
            report.append("YELLOW - WARNING: ").append(yellowCount).append(" products are below 40% of expected target. Please restock soon.");
        } else {
            layoutSalesRoot.setBackgroundColor(0xFFF0FDF4);
            cardReport.setCardBackgroundColor(0xFFF6FEF8);
            tvSalesTitle.setTextColor(0xFF166534);
            tvSalesReport.setTextColor(0xFF14532D);
            report.append("GREEN - HEALTHY: All products are in healthy stock levels (at or above 40% of target).");
        }

        tvSalesReport.setText(report.toString());
    }
}
