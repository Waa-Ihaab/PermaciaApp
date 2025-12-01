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
import com.example.myapplication.adapter.PharmacieAdapter
import com.example.myapplication.database.PharmacieRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.*

class Pagehome : AppCompatActivity() {

    private val LOCATION_PERMISSION_CODE = 1001

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var repo: PharmacieRepository
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagehome)

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPharmacies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Base de données SQLite
        repo = PharmacieRepository(this)

        // Charger JSON une seule fois
        if (!repo.hasData()) {
            repo.loadFromJson(this)
        }

        // GPS pour afficher les pharmacies proches
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        // Navigation
        setupNavigation()

        // Affichage nom utilisateur
        loadUserName()
    }

    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    // ------------------------- LOAD USER NAME -------------------------
    private fun loadUserName() {
        val textViewUser = findViewById<TextView>(R.id.textView2)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                textViewUser.text = doc.getString("name") ?: "Utilisateur"
            }
            .addOnFailureListener {
                textViewUser.text = "Utilisateur"
            }
    }

    // ------------------------- PROFILE IMAGE -------------------------
    private fun loadProfileImage() {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val storageRef = FirebaseStorage.getInstance()
            .reference.child("profileImages/${user.uid}/profile.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_avatar)
                    .into(profileImage)
            }
            .addOnFailureListener {
                profileImage.setImageResource(R.drawable.default_avatar)
            }
    }

    // ------------------------- PERMISSIONS -------------------------
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
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

    // ------------------------- GET USER LOCATION -------------------------
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Toast.makeText(this, "Position non trouvée", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                loadNearestPharmacies(location)
            }
    }

    // ------------------------- SORT BY DISTANCE -------------------------
    private fun loadNearestPharmacies(location: Location) {
        val allPharmacies = repo.getAllPharmacies()

        val pharmaciesSorted = allPharmacies.map {
            it to distance(
                location.latitude,
                location.longitude,
                it.latitude,
                it.longitude
            )
        }.sortedBy { it.second }

        // Prendre 10 pharmacies les plus proches
        val nearest = pharmaciesSorted.take(10).map { it.first }

        recyclerView.adapter = PharmacieAdapter(nearest)

        Log.d("PHARMA", "Pharmacies proches affichées : ${nearest.size}")
    }

    // ------------------------- HAVERSINE -------------------------
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * asin(sqrt(a))
        return R * c
    }

    // ------------------------- NAVIGATION -------------------------
    private fun setupNavigation() {
        findViewById<ImageView>(R.id.profileImage).setOnClickListener {
            startActivity(Intent(this, Monprofile::class.java))
        }

        findViewById<ImageView>(R.id.btnfv).setOnClickListener {
            startActivity(Intent(this, Fvpharmacie::class.java))
        }

        findViewById<TextView>(R.id.ttvoirBtn).setOnClickListener {
            startActivity(Intent(this, AllPharmacia::class.java))
        }

        findViewById<ImageView>(R.id.historyBtn).setOnClickListener {
            startActivity(Intent(this, HistorySearch::class.java))
        }
    }

    // ------------------------- PERMISSION RESULT -------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            Toast.makeText(this, "Permission GPS refusée", Toast.LENGTH_SHORT).show()
        }
    }
}
