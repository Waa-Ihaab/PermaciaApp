package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Pharmacie

class PharmacieAdapter(private val list: List<Pharmacie>) :
    RecyclerView.Adapter<PharmacieAdapter.PharmacieViewHolder>() {

    class PharmacieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.pharmacyName)
        val address: TextView = itemView.findViewById(R.id.pharmacyAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pharmacy, parent, false)
        return PharmacieViewHolder(view)
    }

    override fun onBindViewHolder(holder: PharmacieViewHolder, position: Int) {
        val pharmacy = list[position]

        holder.name.text = pharmacy.nom
        holder.address.text = pharmacy.adresse + ", " + pharmacy.ville
    }

    override fun getItemCount(): Int = list.size
}
