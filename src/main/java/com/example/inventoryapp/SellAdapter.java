package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> itemList;
    private DBHelper dbHelper;
    private SellActivity activity;

    public SellAdapter(Context context, ArrayList<Item> itemList, DBHelper dbHelper, SellActivity activity) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sell_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvSkuBarcode.setText("SKU: " + item.getSku() + " | Barcode: " + item.getBarcode());
        holder.tvAvailableQty.setText("Available Stock: " + item.getQuantity() + " units");

        if (item.getQuantity() <= 0) {
            holder.tvAvailableQty.setText("Out of Stock");
            holder.tvAvailableQty.setTextColor(0xFFD32F2F);
            holder.btnSell.setEnabled(false);
            holder.btnSell.setAlpha(0.5f);
        } else {
            holder.tvAvailableQty.setTextColor(0xFF1E3A8A);
            holder.btnSell.setEnabled(true);
            holder.btnSell.setAlpha(1.0f);
        }

        holder.btnSell.setOnClickListener(v -> showSellDialog(item));
    }

    @Override
    public int getItemCount() { return itemList.size(); }

    private void showSellDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sell " + item.getName());

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        TextView tvInfo = new TextView(context);
        tvInfo.setText("Available Stock: " + item.getQuantity() + " units\nEnter quantity sold:");
        tvInfo.setTextSize(14);
        tvInfo.setTextColor(0xFF0F172A);
        layout.addView(tvInfo);

        EditText etSoldQty = new EditText(context);
        etSoldQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSoldQty.setHint("Quantity Sold");
        etSoldQty.setPadding(16, 16, 16, 16);
        layout.addView(etSoldQty);

        builder.setView(layout);
        builder.setPositiveButton("Record Sale", (dialog, which) -> {
            String qtyStr = etSoldQty.getText().toString().trim();
            if (qtyStr.isEmpty()) {
                Toast.makeText(context, "Please enter quantity sold", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int soldQty = Integer.parseInt(qtyStr);
                if (soldQty <= 0) {
                    Toast.makeText(context, "Quantity sold must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (soldQty > item.getQuantity()) {
                    Toast.makeText(context, "Cannot sell more than available stock (" + item.getQuantity() + ")", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean success = dbHelper.recordSale(item.getId(), soldQty);
                if (success) {
                    Toast.makeText(context, "Sold " + soldQty + " units of " + item.getName() + "!", Toast.LENGTH_SHORT).show();
                    if (activity != null) activity.loadItems();
                    else notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to record sale", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvSkuBarcode, tvAvailableQty;
        Button btnSell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSellItemName);
            tvCategory = itemView.findViewById(R.id.tvSellCategory);
            tvSkuBarcode = itemView.findViewById(R.id.tvSellSkuBarcode);
            tvAvailableQty = itemView.findViewById(R.id.tvSellAvailableQty);
            btnSell = itemView.findViewById(R.id.btnSellItem);
        }
    }
}
