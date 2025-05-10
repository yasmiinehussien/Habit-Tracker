package com.example.habit_compose.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habit_compose.R
import com.example.habit_compose.home.MainActivity
import com.example.habit_compose.ui.theme.CustomTextField

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.Identity.getSignInClient
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import androidx.activity.result.IntentSenderRequest

import com.example.habit_compose.ui.theme.HabitComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.identity.SignInCredential
import java.lang.Exception



class LoginActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential: SignInCredential? = Identity.getSignInClient(this)
                        .getSignInCredentialFromIntent(result.data)

                    val idToken = credential?.googleIdToken
                    val email = credential?.id

                    if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
                        handleGoogleSignIn(idToken, email)
                    } else {
                        Toast.makeText(this, "Failed to retrieve Google credentials.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Error during Google Sign-In: ${e.message}", e)
                    Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Google Sign-In was canceled.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun handleGoogleSignIn(idToken: String, email: String) {
        try {
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { fetchTask ->
                    if (fetchTask.isSuccessful) {
                        val signInMethods = fetchTask.result?.signInMethods ?: emptyList()
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                        if (signInMethods.isNotEmpty() || signInMethods.isEmpty()) {
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        val user = auth.currentUser
                                        if (user != null) {
                                            goToMain()
                                        }
                                    } else {
                                        Log.e("LoginActivity", "Firebase sign-in failed: ${signInTask.exception?.message}", signInTask.exception)
                                        Toast.makeText(
                                            this,
                                            "Sign-in failed: ${signInTask.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        Log.e("LoginActivity", "Failed to check account status: ${fetchTask.exception?.message}", fetchTask.exception)
                        Toast.makeText(
                            this,
                            "Failed to check account status: ${fetchTask.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Exception in handleGoogleSignIn: ${e.message}", e)
            Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        setContent {
            HabitComposeTheme {
                LoginScreen(activity = this) {
                    startGoogleSignIn()
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        try {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                        googleSignInLauncher.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Launching sign-in failed: ${e.message}", e)
                        Toast.makeText(this, "Couldn't start Google Sign-In.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginActivity", "Google Sign-In start failed: ${e.message}", e)
                    Toast.makeText(this, "Google Sign-In failed to start.", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("LoginActivity", "startGoogleSignIn crashed: ${e.message}", e)
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(activity: ComponentActivity, onGoogleClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.womanyoga2),
            contentDescription = "Top Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(activity, "Invalid email format.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    try {
                        auth.signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        if (user.isEmailVerified) {
                                            Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(activity, MainActivity::class.java).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            }
                                            activity.startActivity(intent)
                                            activity.finish()
                                        } else {
                                            Toast.makeText(activity, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                                            auth.signOut()
                                        }
                                    }
                                } else {
                                    val exception = task.exception
                                    val message = when (exception) {
                                        is FirebaseAuthInvalidUserException -> "No account found for this email. Please sign up first."
                                        is FirebaseAuthInvalidCredentialsException -> "Wrong email or password. Please try again."
                                        is FirebaseAuthException -> "Authentication error: ${exception.message}"
                                        else -> "Login failed. Please try again."
                                    }

                                    Log.d("FIREBASE_ERROR", "Exception Class: ${exception?.javaClass}")
                                    Log.d("FIREBASE_ERROR", "Exception Message: ${exception?.message}")
                                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                                }
                            }
                    } catch (e: Exception) {
                        isLoading = false
                        Log.e("LoginScreen", "Login crashed: ${e.message}", e)
                        Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(activity, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0AB2)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Login", color = Color.White)
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color(0xFF4A0AB2),
                strokeWidth = 4.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onGoogleClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4A0AB2))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Login with Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account?", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign up",
                color = Color(0xFF4A0AB2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    val intent = Intent(activity, SignUpActivity::class.java)
                    activity.startActivity(intent)
                },
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot Password?",
            color = Color(0xFF4A0AB2),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
                val intent = Intent(activity, ForgotPasswordActivity::class.java)
                activity.startActivity(intent)
            },
            textDecoration = TextDecoration.Underline
        )
    }
}