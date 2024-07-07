package com.example.my_application;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends ArrayAdapter<StockItem> {
    public StockAdapter(@NonNull Context context, @NonNull List<StockItem> objects) {
        super(context, android.R.layout.simple_dropdown_item_1line, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 使用默认实现，或者自定义视图逻辑
        return super.getView(position, convertView, parent);
    }


}