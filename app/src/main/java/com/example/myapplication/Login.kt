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
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore
=======
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
<<<<<<< HEAD
    private val db = FirebaseFirestore.getInstance()
=======
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()

=======
        // -------------------------------------------------------------
        // 🔹 Initialisation Firebase
        // -------------------------------------------------------------
        auth = FirebaseAuth.getInstance()

        // -------------------------------------------------------------
        // 🔹 Récupération des vues
        // -------------------------------------------------------------
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val btnGoogle = findViewById<LinearLayout>(R.id.btnGoogle)
        val creerCompte = findViewById<TextView>(R.id.creerCompte)

<<<<<<< HEAD
        // 👁 Afficher/Cacher mot de passe
=======
        // -------------------------------------------------------------
        // 🔹 Voir / cacher mot de passe
        // -------------------------------------------------------------
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
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

<<<<<<< HEAD
        // Mot de passe oublié
        forgotPassword.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Entrez votre email", Toast.LENGTH_SHORT).show()
=======
        // -------------------------------------------------------------
        // 🔹 Mot de passe oublié
        // -------------------------------------------------------------
        forgotPassword.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Entrez votre email pour réinitialiser", Toast.LENGTH_SHORT)
                    .show()
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
<<<<<<< HEAD
                    Toast.makeText(this, "Email envoyé ✅", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Aller vers création de compte
=======
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
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
        creerCompte.setOnClickListener {
            startActivity(Intent(this, Creatacc::class.java))
        }

<<<<<<< HEAD
        // ✅ Connexion Email / Mot de passe
=======
        // -------------------------------------------------------------
        // 🔹 Connexion avec email + mot de passe
        // -------------------------------------------------------------
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
        btnConnexion.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

<<<<<<< HEAD
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

        // Google Sign-In
=======
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

    // -------------------------------------------------------------
    // 🔹 Résultat du Sign-In Google
    // -------------------------------------------------------------
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
<<<<<<< HEAD
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Erreur Google : ${e.message}", Toast.LENGTH_SHORT).show()
=======
                    val idToken = account.idToken
                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        Toast.makeText(this, "Token Google nul ❌", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Erreur de connexion Google : ${e.message}", Toast.LENGTH_SHORT)
                        .show()
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
                }
            }
        }

<<<<<<< HEAD
=======
    // 🔹 Connexion Firebase via Google
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
<<<<<<< HEAD
                    val user = auth.currentUser ?: return@addOnCompleteListener

                    saveUserToFirestore(
                        name = user.displayName ?: "Utilisateur Google",
                        email = user.email ?: "",
                        uid = user.uid,
                        image = user.photoUrl?.toString() ?: ""
                    )

=======
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
                    Toast.makeText(this, "Connexion Google réussie ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Pagehome::class.java))
                    finish()
                } else {
<<<<<<< HEAD
                    Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
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
=======
                    Toast.makeText(
                        this,
                        "Erreur : ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
>>>>>>> 2eaf410c69055c7c11e1dd6d123b3701ff9442b4
}
