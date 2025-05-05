package com.example.habit_compose

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.habit_compose.ui.theme.CustomTextField
import com.example.habit_compose.ui.theme.HabitTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HabitTrackerTheme  {
                LoginScreen(activity = this)
            }
        }
    }
}

@Composable
fun LoginScreen(activity: ComponentActivity) {
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
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                activity.startActivity(intent)
                                activity.finish()

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
            onClick = {
                // TODO: Google Sign-In Logic
            },
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
