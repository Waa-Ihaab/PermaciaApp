package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.model.Pharmacie

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
            put("distance", pharmacie.distance)
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
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6)
            )
            list.add(pharmacie)
        }

        cursor.close()
        db.close()
        return list
    }

    fun insertInitialPharmacies() {
        val liste = listOf(
            Pharmacie(1, "Pharmacie Centrale", "12 rue de Paris", "93800", "Épinay-sur-Seine", "0148123456", 0.0),
            Pharmacie(2, "Pharmacie du Centre", "5 avenue de la République", "93800", "Épinay-sur-Seine", "0148567890", 0.0),
            Pharmacie(3, "Pharmacie des Arcades", "32 boulevard Foch", "93800", "Épinay-sur-Seine", "0148129988", 0.0)
        )

        for (pharma in liste) {
            insertPharmacie(pharma)
        }
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
