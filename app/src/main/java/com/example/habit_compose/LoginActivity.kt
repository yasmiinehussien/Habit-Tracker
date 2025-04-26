package com.example.habit_compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habit_compose.ui.theme.CustomTextField
import com.example.habit_compose.ui.theme.Habit_composeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habit_composeTheme {
                LoginScreen(activity = this)
            }
        }
    }
}

@Composable
fun LoginScreen(activity: ComponentActivity) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.womanyoga), // Use same image as sign-up
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
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Login successful!", Toast.LENGTH_LONG).show()

                                // ✅ Move to MainActivity after login
                                val intent = Intent(activity, MainActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish() // Optional: close the login screen

                            } else {
                                val errorMessage = when (val exception = task.exception) {
                                    is FirebaseAuthInvalidUserException -> {
                                        "No account found for this email. Please sign up first."
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        if (exception.message?.contains("password is invalid") == true ||
                                            exception.message?.contains("The password is invalid") == true
                                        ) {
                                            "Wrong password. Please try again."
                                        } else if (exception.message?.contains("email") == true) {
                                            "Invalid email format. Please check and try again."
                                        } else {
                                            "Invalid credentials. Please double-check your info."
                                        }
                                    }
                                    else -> {
                                        "Login failed: ${task.exception?.message}"
                                    }
                                }
                                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(activity, "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0AB2)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Login", color = Color.White)
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