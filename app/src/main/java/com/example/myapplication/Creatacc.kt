package com.example.myapplication

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
=======
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
import com.google.firebase.database.FirebaseDatabase
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4

class Creatacc : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
<<<<<<< HEAD
    private val db = FirebaseFirestore.getInstance()
=======
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creatacc)

        auth = FirebaseAuth.getInstance()

        val nameField = findViewById<EditText>(R.id.nameField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)

<<<<<<< HEAD
=======
        // 🔹 Création ou connexion selon l’état du compte
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
        btnCreateAccount.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

<<<<<<< HEAD
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser!!
                        saveUserToFirestore(name, email, user.uid)

                        Toast.makeText(this, "Compte créé ✅", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Pagehome::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
=======
            // 🔹 On tente d'abord de se connecter (pour vérifier si le compte existe)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        // ✅ Utilisateur déjà enregistré → connexion directe
                        Toast.makeText(this, "Bienvenue à nouveau, ${name} 👋", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Pagehome::class.java))
                        finish()
                    } else {
                        // 🔹 Si connexion échoue, on essaie de créer le compte
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    val user = auth.currentUser
                                    val uid = user?.uid ?: return@addOnCompleteListener

                                    val database = FirebaseDatabase.getInstance()
                                    val userRef = database.getReference("users").child(uid)

                                    val userData = mapOf(
                                        "name" to name,
                                        "email" to email,
                                        "uid" to uid
                                    )

                                    userRef.setValue(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Compte créé avec succès ✅", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, Pagehome::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Erreur lors de l'enregistrement des données", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // 🔹 Si on arrive ici, l’utilisateur n’existe pas OU mauvais mot de passe
                                    val errorMsg = createTask.exception?.message ?: "Erreur inconnue"
                                    if (errorMsg.contains("email address is already in use", ignoreCase = true)) {
                                        Toast.makeText(this, "⚠️ Utilisateur déjà existant", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Utilisateur non trouvé ❌", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
                    }
                }
        }

<<<<<<< HEAD
=======
        // 🔹 Configuration Google Sign-In
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener {
<<<<<<< HEAD
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

=======
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    // 🔹 Résultat du Sign-In Google
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
<<<<<<< HEAD
                Toast.makeText(this, "Erreur Google", Toast.LENGTH_SHORT).show()
=======
                Toast.makeText(this, "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
            }
        }
    }

<<<<<<< HEAD
=======
    // 🔹 Connexion Firebase avec Google
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
<<<<<<< HEAD

                    val user = auth.currentUser!!
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

    private fun saveUserToFirestore(name: String, email: String, uid: String) {
        val userData = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid
        )

        db.collection("users")
            .document(uid)
            .set(userData)
    }
=======
                    val user = auth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val database = FirebaseDatabase.getInstance()
                    val userRef = database.getReference("users").child(uid)

                    val userData = mapOf(
                        "name" to (user.displayName ?: "Utilisateur Google"),
                        "email" to (user.email ?: ""),
                        "uid" to uid
                    )

                    userRef.setValue(userData)

                    Toast.makeText(this, "Connexion Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Pagehome::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
}
