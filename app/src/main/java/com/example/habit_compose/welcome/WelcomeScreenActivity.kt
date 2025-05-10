package com.example.habit_compose.welcome


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.habit_compose.R
import com.example.habit_compose.auth.LoginActivity
import com.example.habit_compose.auth.SignUpActivity
import com.example.habit_compose.home.MainActivity
import com.example.habit_compose.ui.theme.HabitComposeTheme
import com.example.habit_compose.ui.theme.HabitComposeTheme

import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class WelcomeScreenActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications allowed ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create the Notification Channel HERE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "habit_channel",
                "Habit Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }



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
            HabitComposeTheme  {
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

