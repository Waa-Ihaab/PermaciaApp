package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // UI Elements
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)
        val creerCompte = findViewById<TextView>(R.id.creerCompte)

        // 👁 Afficher / cacher mot de passe
        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye_on)
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            passwordField.setSelection(passwordField.text.length)
        }

        // 🔹 Mot de passe oublié
        forgotPassword.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Entrez votre email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Email envoyé ✅", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 🔹 Aller vers Création de compte
        creerCompte.setOnClickListener {
            startActivity(Intent(this, Creatacc::class.java))
        }

        // 🔹 Connexion Email / Mot de passe
        btnConnexion.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        if (user != null) {
                            saveUserToFirestore(
                                name = user.displayName ?: "Utilisateur",
                                email = user.email ?: "",
                                uid = user.uid,
                                image = user.photoUrl?.toString() ?: ""
                            )
                        }

                        Toast.makeText(this, "Connexion réussie ✅", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Pagehome::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
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

    // 🔹 Google Sign-In result
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Erreur Google : ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // 🔹 Connexion Firebase via Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser ?: return@addOnCompleteListener

                    saveUserToFirestore(
                        name = user.displayName ?: "Utilisateur Google",
                        email = user.email ?: "",
                        uid = user.uid,
                        image = user.photoUrl?.toString() ?: ""
                    )

                    Toast.makeText(this, "Connexion Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Pagehome::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun saveUserToFirestore(name: String, email: String, uid: String, image: String) {
        val userData = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid,
            "profileImage" to image
        )

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Utilisateur enregistré ✅")
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Erreur : ${it.message}")
            }
    }
}
