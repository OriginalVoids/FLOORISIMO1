package com.example.myapplication.activities.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
import com.example.myapplication.activities.supplier.SupplierListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private List<Client> clientList;

    private final ActivityResultLauncher<Intent> createClientLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Client newClient = (Client) result.getData().getSerializableExtra("new_client");
                    if (newClient != null) {
                        clientList.add(newClient);
                        adapter.notifyItemInserted(clientList.size() - 1);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerBackground), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data
        clientList = new ArrayList<>();
        clientList.add(new Client("John Doe", Arrays.asList("Foundation Cracks", "Wall Moisture"), "Minimalist Modern", 2, 5));
        clientList.add(new Client("Jane Smith", Arrays.asList("Uneven Flooring"), "Industrial Loft", 3, 12));

        adapter = new ClientAdapter(clientList, new ClientAdapter.OnClientActionListener() {
            @Override
            public void onClientView(Client client) {
                Intent intent = new Intent(ClientListActivity.this, ClientDetailsActivity.class);
                intent.putExtra("client", client);
                startActivity(intent);
            }

            @Override
            public void onClientOptions(Client client, View anchor) {
                showOptionsPopupMenu(client, anchor);
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton addClientFab = findViewById(R.id.addClientFab);
        addClientFab.setOnClickListener(v -> {
            Intent intent = new Intent(ClientListActivity.this, CreateClientActivity.class);
            createClientLauncher.launch(intent);
        });

        ImageButton btnGoToSuppliers = findViewById(R.id.btnGoToSuppliers);
        btnGoToSuppliers.setOnClickListener(v -> {
            Intent intent = new Intent(ClientListActivity.this, SupplierListActivity.class);
            startActivity(intent);
        });
    }

    private void showOptionsPopupMenu(Client client, View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenu().add("Edit Info");
        popupMenu.getMenu().add("Order New Floorings");
        popupMenu.getMenu().add("Pause Subscription");
        popupMenu.getMenu().add("Delete Client");

        popupMenu.setOnMenuItemClickListener(item -> {
            String choice = item.getTitle().toString();
            switch (choice) {
                case "Edit Info":
                    Toast.makeText(this, "Editing info for: " + client.getName(), Toast.LENGTH_SHORT).show();
                    return true;
                case "Order New Floorings":
                    Toast.makeText(this, "Ordering floorings for: " + client.getName(), Toast.LENGTH_SHORT).show();
                    return true;
                case "Pause Subscription":
                    Toast.makeText(this, "Subscription paused for: " + client.getName(), Toast.LENGTH_SHORT).show();
                    return true;
                case "Delete Client":
                    int position = clientList.indexOf(client);
                    if (position != -1) {
                        clientList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Deleted: " + client.getName(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }
}
