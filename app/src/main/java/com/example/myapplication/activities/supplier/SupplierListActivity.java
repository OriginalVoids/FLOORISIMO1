package com.example.myapplication.activities.supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SupplierListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SupplierAdapter adapter;
    private List<Supplier> supplierList;

    private final ActivityResultLauncher<Intent> createSupplierLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Supplier newSupplier = (Supplier) result.getData().getSerializableExtra("new_supplier");
                    if (newSupplier != null) {
                        supplierList.add(newSupplier);
                        adapter.notifyItemInserted(supplierList.size() - 1);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        recyclerView = findViewById(R.id.supplierRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        supplierList = new ArrayList<>();
        supplierList.add(new Supplier("Floors R Us", "Mark Spencer", 500, 15.50, "USA"));
        supplierList.add(new Supplier("Global Tiles", "Elena Rossi", 1200, 12.00, "Italy"));

        adapter = new SupplierAdapter(supplierList, new SupplierAdapter.OnSupplierActionListener() {
            @Override
            public void onSupplierView(Supplier supplier) {
                Intent intent = new Intent(SupplierListActivity.this, SupplierDetailsActivity.class);
                intent.putExtra("supplier", supplier);
                startActivity(intent);
            }

            @Override
            public void onSupplierOptions(Supplier supplier, View anchor) {
                showOptionsPopupMenu(supplier, anchor);
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton addSupplierFab = findViewById(R.id.addSupplierFab);
        addSupplierFab.setOnClickListener(v -> {
            Intent intent = new Intent(SupplierListActivity.this, CreateSupplierActivity.class);
            createSupplierLauncher.launch(intent);
        });
    }

    private void showOptionsPopupMenu(Supplier supplier, View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenu().add("Order More Floorings");
        popupMenu.getMenu().add("Remove Supplier");

        popupMenu.setOnMenuItemClickListener(item -> {
            String choice = item.getTitle().toString();
            if (choice.equals("Order More Floorings")) {
                Toast.makeText(this, "Opening order page for " + supplier.getCompanyName(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (choice.equals("Remove Supplier")) {
                int position = supplierList.indexOf(supplier);
                if (position != -1) {
                    supplierList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Removed: " + supplier.getCompanyName(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}
