package com.example.habit_compose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habit_compose.ui.theme.Habit_composeTheme
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Habit_composeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    ForgotPasswordScreen(context = this)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(context: Activity) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val primaryColor = Color(0xFF4A0AB2)
    val lightGreyColor = Color.LightGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top image
        Image(
            painter = painterResource(id = R.drawable.womanyoga),
            contentDescription = "Yoga Illustration",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text("Email", color = primaryColor) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = lightGreyColor,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = lightGreyColor
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Password Button
        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Check your email to reset your password.", Toast.LENGTH_LONG).show()
                                context.finish()
                            } else {
                                errorMessage = task.exception?.message ?: "Something went wrong"
                            }
                        }
                } else {
                    errorMessage = "Please enter your email"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Send Reset Email", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Want to go back to login?",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.width(4.dp))

        ClickableText(
            text = AnnotatedString("Click Here."),
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                context.finish()
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4A0AB2),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}
