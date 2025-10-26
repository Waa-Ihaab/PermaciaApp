package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val creerCompte = findViewById<TextView>(R.id.creerCompte)
        creerCompte.setOnClickListener {
            val intent = Intent(this, Creatacc::class.java)
            startActivity(intent)
        }
    }
}

