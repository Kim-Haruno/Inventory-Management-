package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemChangeListener {
        void onItemChanged();
    }

    private Context context;
    private ArrayList<Item> itemList;
    private DBHelper dbHelper;
    private InventoryActivity activity;
    private OnItemChangeListener listener;

    public ItemAdapter(Context context, ArrayList<Item> itemList, DBHelper dbHelper, OnItemChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public ItemAdapter(Context context, ArrayList<Item> itemList, DBHelper dbHelper, InventoryActivity activity) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
        this.activity = activity;
        if (activity != null) {
            this.listener = activity::loadItems;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        
        String skuBc = "SKU: " + item.getSku() + " | Barcode: " + item.getBarcode();
        holder.tvSkuBarcode.setText(skuBc);
        
        String costStr = String.format(Locale.getDefault(), "Cost: R%.2f", item.getCostPrice());
        holder.tvCostPrice.setText(costStr);

        holder.tvQuantity.setText("Qty: " + item.getQuantity() + " / " + item.getExpectedQuantity());

        if (item.isRed()) {
            holder.tvQuantity.setTextColor(0xFFD32F2F);
        } else if (item.isYellow()) {
            holder.tvQuantity.setTextColor(0xFFD97706);
        } else {
            holder.tvQuantity.setTextColor(0xFF10B981);
        }

        holder.btnEdit.setOnClickListener(v -> showEditDialog(item));

        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteItem(item.getId());
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
            
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                pos = holder.getAdapterPosition();
            }
            if (pos != RecyclerView.NO_POSITION && pos < itemList.size()) {
                itemList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, itemList.size());
            } else {
                itemList.remove(item);
                notifyDataSetChanged();
            }

            if (listener != null) {
                listener.onItemChanged();
            } else if (activity != null) {
                activity.loadItems();
            }
        });
    }

    @Override
    public int getItemCount() { return itemList.size(); }

    private void showEditDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_item, null);
        EditText etName = view.findViewById(R.id.etEditName);
        EditText etQty = view.findViewById(R.id.etEditQuantity);
        EditText etCat = view.findViewById(R.id.etEditCategory);
        EditText etExpected = view.findViewById(R.id.etEditExpected);
        EditText etSku = view.findViewById(R.id.etEditSku);
        EditText etBarcode = view.findViewById(R.id.etEditBarcode);
        EditText etCostPrice = view.findViewById(R.id.etEditCostPrice);

        etName.setText(item.getName());
        etQty.setText(String.valueOf(item.getQuantity()));
        etCat.setText(item.getCategory());
        etExpected.setText(String.valueOf(item.getExpectedQuantity()));
        etSku.setText(item.getSku());
        etBarcode.setText(item.getBarcode());
        etCostPrice.setText(String.valueOf(item.getCostPrice()));

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newQtyStr = etQty.getText().toString().trim();
            String newCat = etCat.getText().toString().trim();
            String newExpStr = etExpected.getText().toString().trim();
            String newSku = etSku.getText().toString().trim();
            String newBarcode = etBarcode.getText().toString().trim();
            String newCostStr = etCostPrice.getText().toString().trim();

            if (newName.isEmpty() || newQtyStr.isEmpty() || newExpStr.isEmpty()) {
                Toast.makeText(context, "Cannot leave empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newCat.isEmpty()) newCat = "General";

            int newQty;
            try {
                newQty = Integer.parseInt(newQtyStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int newExp;
            try {
                newExp = Integer.parseInt(newExpStr);
                if (newExp <= 0) {
                    Toast.makeText(context, "Expected quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid expected quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            double newCost = 0.0;
            if (!newCostStr.isEmpty()) {
                try {
                    newCost = Double.parseDouble(newCostStr);
                } catch (NumberFormatException ignored) {}
            }

            dbHelper.updateItem(item.getId(), newName, newQty, newCat, newExp, newSku, newBarcode, newCost);
            Toast.makeText(context, "Item Updated", Toast.LENGTH_SHORT).show();

            Item updated = dbHelper.getItemById(item.getId());
            if (updated != null) {
                int idx = itemList.indexOf(item);
                if (idx != -1) {
                    itemList.set(idx, updated);
                } else {
                    item.setName(updated.getName());
                    item.setQuantity(updated.getQuantity());
                    item.setCategory(updated.getCategory());
                    item.setExpectedQuantity(updated.getExpectedQuantity());
                    item.setSku(updated.getSku());
                    item.setBarcode(updated.getBarcode());
                    item.setCostPrice(updated.getCostPrice());
                }
            }

            notifyDataSetChanged();

            if (listener != null) {
                listener.onItemChanged();
            } else if (activity != null) {
                activity.loadItems();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvCategory, tvSkuBarcode, tvCostPrice;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvSkuBarcode = itemView.findViewById(R.id.tvItemSkuBarcode);
            tvCostPrice = itemView.findViewById(R.id.tvItemCostPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
