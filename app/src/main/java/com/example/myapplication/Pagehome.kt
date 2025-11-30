package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore


class Pagehome : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagehome)


        
        // ✅ RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPharmacies)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // ✅ GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        // ✅ Photo Profil
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        profileImage.setOnClickListener {
            startActivity(Intent(this, Monprofile::class.java))
        }

        // ✅ Boutons navigation
        findViewById<ImageView>(R.id.btnfv).setOnClickListener {
            startActivity(Intent(this, Fvpharmacie::class.java))
        }

        findViewById<TextView>(R.id.ttvoirBtn).setOnClickListener {
            startActivity(Intent(this, AllPharmacia::class.java))
        }

        findViewById<ImageView>(R.id.historyBtn).setOnClickListener {
            startActivity(Intent(this, HistorySearch::class.java))
        }

        // ✅ Nom utilisateur
        // ✅ Nom utilisateur depuis Firestore
        val textViewUser = findViewById<TextView>(R.id.textView2)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Utilisateur"
                        textViewUser.text = name
                    } else {
                        textViewUser.text = "Utilisateur"
                    }
                }
                .addOnFailureListener {
                    textViewUser.text = "Utilisateur"
                }
        }

    }

    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profileImages/$uid/profile.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_avatar)
                    .into(profileImage)
            }
            .addOnFailureListener {
                profileImage.setImageResource(R.drawable.default_avatar)
                Log.e("PROFILE", "Image de profil introuvable")
            }
    }

    // ✅ Permissions GPS
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("GPS", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            } else {
                Toast.makeText(this, "Position non trouvée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
