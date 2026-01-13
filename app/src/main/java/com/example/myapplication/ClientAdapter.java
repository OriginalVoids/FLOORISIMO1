package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Client> clientList;
    private OnClientActionListener listener;

    public interface OnClientActionListener {
        void onClientView(Client client);
        void onClientOptions(Client client, View anchor);
    }

    public ClientAdapter(List<Client> clientList, OnClientActionListener listener) {
        this.clientList = clientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.nameTextView.setText(client.getName());
        
        holder.viewButton.setOnClickListener(v -> listener.onClientView(client));
        holder.optionsButton.setOnClickListener(v -> listener.onClientOptions(client, v));
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageButton viewButton;
        ImageButton optionsButton;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.clientNameTextView);
            viewButton = itemView.findViewById(R.id.viewClientButton);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
}
