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

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

        // -------------------------------------------------------------
        // 🔹 Initialisation Firebase
        // -------------------------------------------------------------
        auth = FirebaseAuth.getInstance()

        // -------------------------------------------------------------
        // 🔹 Récupération des vues
        // -------------------------------------------------------------
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)
        val creerCompte = findViewById<TextView>(R.id.creerCompte)

        // -------------------------------------------------------------
        // 🔹 Voir / cacher mot de passe
        // -------------------------------------------------------------
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

        // -------------------------------------------------------------
        // 🔹 Mot de passe oublié
        // -------------------------------------------------------------
        forgotPassword.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Entrez votre email pour réinitialiser", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Email de réinitialisation envoyé ✅",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Erreur : ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // -------------------------------------------------------------
        // 🔹 Aller vers la page Créer un compte
        // -------------------------------------------------------------
        creerCompte.setOnClickListener {
            startActivity(Intent(this, Creatacc::class.java))
        }

        // -------------------------------------------------------------
        // 🔹 Connexion avec email + mot de passe
        // -------------------------------------------------------------
        btnConnexion.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("FIREBASE_LOGIN", "Tentative de connexion pour $email")

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    Log.d("FIREBASE_LOGIN", "Firebase signIn terminé")

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Bienvenue ✅", Toast.LENGTH_SHORT).show()
                        Log.d("FIREBASE_LOGIN", "Connexion réussie pour $email")
                        startActivity(Intent(this, Pagehome::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Erreur Firebase : ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("FIREBASE_LOGIN", "Erreur : ${task.exception?.message}")
                    }
                }
                .addOnFailureListener {
                    Log.e("FIREBASE_LOGIN", "🔥 Exception : ${it.localizedMessage}")
                }
        }

        // -------------------------------------------------------------
        // 🔹 Configuration Google Sign-In
        // -------------------------------------------------------------
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    // -------------------------------------------------------------
    // 🔹 Résultat du Sign-In Google
    // -------------------------------------------------------------
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        Toast.makeText(this, "Token Google nul ❌", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Erreur de connexion Google : ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    // 🔹 Connexion Firebase via Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Connexion Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Pagehome::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Erreur : ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
