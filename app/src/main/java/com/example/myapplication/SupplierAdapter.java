package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder> {

    private List<Supplier> supplierList;
    private OnSupplierActionListener listener;

    public interface OnSupplierActionListener {
        void onSupplierView(Supplier supplier);
        void onSupplierOptions(Supplier supplier, View anchor);
    }

    public SupplierAdapter(List<Supplier> supplierList, OnSupplierActionListener listener) {
        this.supplierList = supplierList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        Supplier supplier = supplierList.get(position);
        holder.companyNameTextView.setText(supplier.getCompanyName());
        holder.personalNameTextView.setText(supplier.getPersonalName());
        
        holder.viewButton.setOnClickListener(v -> listener.onSupplierView(supplier));
        holder.optionsButton.setOnClickListener(v -> listener.onSupplierOptions(supplier, v));
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    static class SupplierViewHolder extends RecyclerView.ViewHolder {
        TextView companyNameTextView;
        TextView personalNameTextView;
        ImageButton viewButton;
        ImageButton optionsButton;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            companyNameTextView = itemView.findViewById(R.id.supplierCompanyNameTextView);
            personalNameTextView = itemView.findViewById(R.id.supplierPersonalNameTextView);
            viewButton = itemView.findViewById(R.id.viewSupplierButton);
            optionsButton = itemView.findViewById(R.id.supplierOptionsButton);
        }
    }
}
