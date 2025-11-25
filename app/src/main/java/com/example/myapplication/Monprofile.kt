package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Monprofile : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    selectedImageUri = data.data
                    uploadImageToFirebase(selectedImageUri!!)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_monprofile)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val userName = findViewById<TextView>(R.id.userName)
        val mailUser = findViewById<TextView>(R.id.mailUser)
        val phoneUser = findViewById<TextView>(R.id.phoneUser)
        val profileImage = findViewById<ImageView>(R.id.profileImage)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        // Retour
        btnBack.setOnClickListener { finish() }

        // Clique sur l'image pour changer la photo
        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        if (user != null) {
            val uid = user.uid
            Log.d("PROFILE", "UID connecté : $uid")

            // Email depuis Firebase Auth
            mailUser.text = user.email ?: "Email inconnu"

            // Charger infos Firestore
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val name = document.getString("name")
                        val phone = document.getString("phone")
                        val imageUrl = document.getString("profileImage")

                        userName.text = name ?: "Utilisateur"
                        phoneUser.text = phone ?: "Cliquer pour ajouter"

                        // Charger image s'il y en a une
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(imageUrl).into(profileImage)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PROFILE", "Erreur Firestore", e)
                    Toast.makeText(this, "Erreur Firestore ❌ : ${e.message}", Toast.LENGTH_LONG).show()
                }

            // Cliquer sur numéro pour modifier
            phoneUser.setOnClickListener {

                val input = EditText(this)
                input.hint = "Entrer votre numéro"

                AlertDialog.Builder(this)
                    .setTitle("Modifier le numéro")
                    .setView(input)
                    .setPositiveButton("Enregistrer") { _, _ ->
                        val newPhone = input.text.toString()

                        if (newPhone.isNotEmpty()) {
                            db.collection("users").document(uid)
                                .update("phone", newPhone)
                                .addOnSuccessListener {
                                    phoneUser.text = newPhone
                                    Toast.makeText(this, "Numéro mis à jour ✅", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erreur lors de la mise à jour ❌", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Numéro vide ❌", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            }
        }
    }

    // ✅ Upload image vers Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri) {

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profileImages/$uid/profile.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("profileImage", imageUrl)

                    val profileImage = findViewById<ImageView>(R.id.profileImage)
                    Glide.with(this).load(imageUrl).into(profileImage)

                    Toast.makeText(this, "Photo mise à jour ✅", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur upload image ❌ : ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
