package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.model.Pharmacie
import com.google.gson.Gson

class PharmacieRepository(context: Context) {

    private val dbHelper = PharmacieDatabaseHelper(context)

    fun insertPharmacie(pharmacie: Pharmacie) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("id", pharmacie.id)
            put("nom", pharmacie.nom)
            put("adresse", pharmacie.adresse)
            put("codePostal", pharmacie.codePostal)
            put("ville", pharmacie.ville)
            put("telephone", pharmacie.telephone)
            put("latitude", pharmacie.latitude)
            put("longitude", pharmacie.longitude)
            put("type", pharmacie.type)
        }

        db.insert("pharmacie", null, values)
        db.close()
    }

    fun getAllPharmacies(): List<Pharmacie> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pharmacie", null)

        val list = mutableListOf<Pharmacie>()

        while (cursor.moveToNext()) {
            val pharmacie = Pharmacie(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nom")),
                cursor.getString(cursor.getColumnIndexOrThrow("adresse")),
                cursor.getString(cursor.getColumnIndexOrThrow("codePostal")),
                cursor.getString(cursor.getColumnIndexOrThrow("ville")),
                cursor.getString(cursor.getColumnIndexOrThrow("telephone")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
                cursor.getString(cursor.getColumnIndexOrThrow("type"))
            )
            list.add(pharmacie)
        }

        cursor.close()
        db.close()
        return list
    }

    fun loadFromJson(context: Context) {
        val json = context.assets.open("pharmacies.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val pharmacies = gson.fromJson(json, Array<Pharmacie>::class.java)

        pharmacies.forEach { insertPharmacie(it) }
    }

    fun hasData(): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM pharmacie", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }
}
