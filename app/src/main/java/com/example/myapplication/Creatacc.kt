package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class Creatacc : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creatacc)

        // 🔹 Initialiser Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 🔹 Récupération des vues
        val email = findViewById<EditText>(R.id.emailField)
        val password = findViewById<EditText>(R.id.passwordField)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)
        val seConnecter = findViewById<TextView>(R.id.SeConnecter)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)

        // -------------------------------------------------------------
        // 🔹 Création de compte classique (Email / Mot de passe)
        // -------------------------------------------------------------
        btnCreate.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Compte créé avec succès ✅", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        // -------------------------------------------------------------
        // 🔹 Configuration Google Sign-In
        // -------------------------------------------------------------
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ton client_id dans strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 🔹 Quand on clique sur "Continuer avec Google"
        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        // 🔹 Redirection vers la page de connexion
        seConnecter.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    // -------------------------------------------------------------
    // 🔹 Résultat du Sign-In Google
    // -------------------------------------------------------------
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -------------------------------------------------------------
    // 🔹 Connexion à Firebase avec le compte Google
    // -------------------------------------------------------------
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Connexion via Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
