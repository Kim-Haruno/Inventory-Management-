package com.example.inventoryapp;

public class Item {
    private int id;
    private String name;
    private int quantity;
    private String category;
    private int expectedQuantity;
    private String sku;
    private String barcode;
    private double costPrice;

    public Item(int id, String name, int quantity, String category, int expectedQuantity, String sku, String barcode, double costPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.expectedQuantity = expectedQuantity > 0 ? expectedQuantity : 40;
        this.sku = sku != null && !sku.trim().isEmpty() ? sku.trim() : "N/A";
        this.barcode = barcode != null && !barcode.trim().isEmpty() ? barcode.trim() : "N/A";
        this.costPrice = costPrice >= 0 ? costPrice : 0.0;
    }

    public Item(int id, String name, int quantity, String category, int expectedQuantity) {
        this(id, name, quantity, category, expectedQuantity, "N/A", "N/A", 0.0);
    }

    public Item(int id, String name, int quantity, String category) {
        this(id, name, quantity, category, 40, "N/A", "N/A", 0.0);
    }

    public Item(int id, String name, int quantity) {
        this(id, name, quantity, "General", 40, "N/A", "N/A", 0.0);
    }

    public boolean isRed() {
        return quantity < (expectedQuantity * 0.20);
    }

    public boolean isYellow() {
        return quantity < (expectedQuantity * 0.40) && !isRed();
    }

    public boolean isGreen() {
        return quantity >= (expectedQuantity * 0.40);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public int getExpectedQuantity() { return expectedQuantity; }
    public String getSku() { return sku; }
    public String getBarcode() { return barcode; }
    public double getCostPrice() { return costPrice; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCategory(String category) { this.category = category; }
    public void setExpectedQuantity(int expectedQuantity) { this.expectedQuantity = expectedQuantity; }
    public void setSku(String sku) { this.sku = sku; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
}
