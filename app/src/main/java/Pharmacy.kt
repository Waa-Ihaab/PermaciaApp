package com.example.myapplication.models

data class Pharmacy(
    val id: Int = 0,
    val nom: String = "",
    val adresse: String = "",
    val codePostal: String = "",
    val ville: String = "",
    val telephone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = ""
)
