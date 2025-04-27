package com.example.habit_compose


import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val context = LocalContext.current

    var imageUri by rememberSaveable { mutableStateOf("") }

    val painter = rememberImagePainter(
        if (imageUri.isEmpty())
            R.drawable.person
        else
            imageUri
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image and Edit Photo Button (same as before)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(150.dp)
                    .border(4.dp, Color(0xFF8B5EDE), CircleShape)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "profile photo",
                    modifier = Modifier
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            val username = getUsername()

            Text(
                text = ", $username",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )


            Column(
                modifier = Modifier
                    .height(150.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.align(Alignment.End)
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5EDC))
                ) {
                    Text(
                        text = "Edit Photo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification Icon",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Notification",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.weight(1f))

            var isSwitchOn by remember { mutableStateOf(true) }

            Switch(
                checked = isSwitchOn,
                onCheckedChange = { isSwitchOn = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF7441BE),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )



            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.DomainVerification,
                contentDescription = "Vacation mode",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Vacation mode",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.weight(1f))

            var isSwitchOn by remember { mutableStateOf(false) }

            Switch(
                checked = isSwitchOn,
                onCheckedChange = { isSwitchOn = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF7831DC),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )



            )
        }
        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = {


                FirebaseAuth.getInstance().signOut()  // ✅ Sign out from Firebase

                // If you use Google Sign In too
                val googleSignInClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                )
                googleSignInClient.signOut().addOnCompleteListener {
                    // After signing out, go to WelcomeScreenActivity
                    val intent = Intent(context, WelcomeScreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B36A1))
        ) {
            Text(
                text = "Logout",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}