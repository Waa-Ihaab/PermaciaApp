package com.example.myapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PharmacieDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "pharmacie_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE pharmacie (
                id INTEGER PRIMARY KEY,
                nom TEXT,
                adresse TEXT,
                codePostal TEXT,
                ville TEXT,
                telephone TEXT,
                latitude REAL,
                longitude REAL,
                type TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pharmacie")
        onCreate(db)
    }
}
