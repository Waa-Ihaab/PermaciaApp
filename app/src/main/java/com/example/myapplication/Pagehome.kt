package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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

        // 2. On ajoute le clic pour aller vers Fvpharmacie
        btnNext.setOnClickListener {
            val intent = Intent(this, Fvpharmacie::class.java)
            startActivity(intent)
        }
    }
}

