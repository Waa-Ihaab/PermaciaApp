package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class Creatacc : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    // Utilisation de Firestore comme dans votre version HEAD
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creatacc)

        auth = FirebaseAuth.getInstance()

        val nameField = findViewById<EditText>(R.id.nameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)

        // 🔹 Logique de création de compte
        btnCreateAccount.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser!!
                        // Sauvegarde dans Firestore
                        saveUserToFirestore(name, email, user.uid)

                        Toast.makeText(this, "Compte créé ✅", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Pagehome::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, "Erreur: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 🔹 Configuration Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener {
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

    // 🔹 Résultat du Sign-In Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Erreur Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Authentification Firebase avec le token Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    // Sauvegarde ou mise à jour dans Firestore
                    saveUserToFirestore(
                        user.displayName ?: "Utilisateur Google",
                        user.email ?: "",
                        user.uid
                    )

                    startActivity(Intent(this, Pagehome::class.java))
                    finish()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // 🔹 Fonction utilitaire pour sauvegarder dans Firestore
    private fun saveUserToFirestore(name: String, email: String, uid: String) {
        val userData = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid
        )

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnFailureListener {
                Toast.makeText(this, "Erreur de sauvegarde des données", Toast.LENGTH_SHORT).show()
            }
    }
}