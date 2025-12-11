package com.miraflores.agenda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miraflores.agenda.R
import com.miraflores.agenda.model.Client

class ClientAdapter(
    private val clientList: ArrayList<Client>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvClientName)
        val tvPhone: TextView = view.findViewById(R.id.tvClientPhone)
        val tvEmail: TextView = view.findViewById(R.id.tvClientEmail)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clientList[position]
        holder.tvName.text = client.name
        holder.tvPhone.text = client.phone
        holder.tvEmail.text = client.email
        holder.btnDelete.setOnClickListener { onDeleteClick(client.id) }
    }

    override fun getItemCount() = clientList.size
}
