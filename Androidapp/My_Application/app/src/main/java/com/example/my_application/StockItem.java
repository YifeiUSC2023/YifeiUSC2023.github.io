package com.example.my_application;

public class StockItem {
    String symbol;
    String description;

    public StockItem(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    @Override
    public String toString() {
        return symbol + " | " + description;
    }
}