package com.example.myapplication.model

data class Pharmacie(
    val id: Int,
    val nom: String,
    val adresse: String,
    val codePostal: String,
    val ville: String,
    val telephone: String,
    val latitude: Double,
    val longitude: Double,
    val type: String
)
