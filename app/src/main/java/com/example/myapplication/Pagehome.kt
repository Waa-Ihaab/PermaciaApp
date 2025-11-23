package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class Pagehome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pagehome)

        val profileImage = findViewById<ImageView>(R.id.profileImage)


        //Go to profile
        profileImage.setOnClickListener {
            val intent = Intent(this, Monprofile::class.java)
            startActivity(intent)
        }


        val btnNext = findViewById<ImageView>(R.id.btnfv)

        // go to Fvpharmacie
        btnNext.setOnClickListener {
            val intent = Intent(this, Fvpharmacie::class.java)
            startActivity(intent)
        }

        val btnToutVoir = findViewById<TextView>(R.id.ttvoirBtn)

        // go to all pharmacie
        btnToutVoir.setOnClickListener {
            val intent = Intent(this, AllPharmacia::class.java)
            startActivity(intent)
        }

        val historyBtn = findViewById<ImageView>(R.id.historyBtn)

        // go to History
        historyBtn.setOnClickListener {
            val intent = Intent(this, HistorySearch::class.java)
            startActivity(intent)
        }


    }
}

