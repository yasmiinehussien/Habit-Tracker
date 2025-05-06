package com.example.habit_compose.auth


import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*

import androidx.activity.result.ActivityResultLauncher
import com.example.habit_compose.R
import com.example.habit_compose.ui.theme.HabitTrackerTheme


class SignUpActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                        Toast.makeText(this, "Signed up with Google. Verification email sent!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Google sign-in failed: ${task2.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            HabitTrackerTheme {
                SignUpScreen(this, auth, googleSignInClient, googleSignInLauncher)
            }
        }
    }
}

@Composable
fun SignUpScreen(
    activity: ComponentActivity,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
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
                .height(130.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Sign Up", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        CustomField(email, { email = it; errorMessage = null }, "Email")
        CustomField(password, { password = it; errorMessage = null }, "Password", isPassword = true)
        CustomField(confirmPassword, { confirmPassword = it; errorMessage = null }, "Confirm Password", isPassword = true)

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "The email address is badly formatted"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill all fields"
                } else {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                auth.currentUser?.sendEmailVerification()
                                Toast.makeText(activity, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show()

                                val intent = Intent(activity, LoginActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()
                            } else {
                                errorMessage = "Error: ${task.exception?.message}"
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0AB2)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }
        if (isLoading) {
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(color = Color(0xFF4A0AB2))
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()

                    val newGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

                    val signInIntent = newGoogleSignInClient.signInIntent
                    signInIntent.putExtra("prompt", "select_account")

                    googleSignInLauncher.launch(signInIntent)
                }
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
            Text("Sign Up with Google", color = Color.Black)
        }




        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account?", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Log in",
                color = Color(0xFF4A0AB2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    activity.startActivity(Intent(activity, LoginActivity::class.java))
                },
                textDecoration = TextDecoration.Underline
            )
        }
    }

}



@Composable
fun CustomField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4A0AB2),
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.LightGray,
            focusedLabelColor = Color(0xFF4A0AB2),
            unfocusedLabelColor = Color.LightGray,
            focusedPlaceholderColor = Color.LightGray,
            unfocusedPlaceholderColor = Color.LightGray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}