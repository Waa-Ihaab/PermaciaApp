package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔹 Initialiser Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 🔹 Récupération des vues
        val creerCompte = findViewById<TextView>(R.id.creerCompte)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)

        // 🔹 Redirection vers la page de création de compte
        creerCompte.setOnClickListener {
            val intent = Intent(this, Creatacc::class.java)
            startActivity(intent)
        }

        // -------------------------------------------------------------
        // 🔹 Configuration du Google Sign-In
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
                    Toast.makeText(this, "Connexion Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PageHome::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
