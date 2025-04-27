package com.example.habit_compose


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habit_compose.ui.theme.HabitTrackerTheme
import com.example.habit_compose.ui.theme.WelcomeScreen

import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class WelcomeScreenActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val currentUser = FirebaseAuth.getInstance().currentUser

/*
        if (currentUser != null) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
*/


        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Your actual web client ID from Firebase Console
            .requestEmail()
            .build()

        // Initialize GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Register for activity result (Google Sign-In)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)  // Use the ID token to authenticate with Firebase
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace() // Log error for debugging
            }
        }

        // Set content for the Welcome Screen
        setContent {
            HabitTrackerTheme {
                WelcomeScreen(
                    onSignUpClick = {
                        startActivity(Intent(this, SignUpActivity::class.java))  // Navigate to SignUpActivity
                    },
                    onLoginClick = {
                        startActivity(Intent(this, LoginActivity::class.java))  // Navigate to LoginActivity
                    },
                    onGoogleSignInClick = {
                        val signInIntent = googleSignInClient.signInIntent  // Get Google sign-in intent
                        launcher.launch(signInIntent)  // Launch Google Sign-In flow
                    }
                )
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome, ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    // 🔥 ADD THIS to move to MainActivity after login
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Firebase Auth failed.", Toast.LENGTH_SHORT).show()
                    task.exception?.printStackTrace()
                }
            }
    }

}

