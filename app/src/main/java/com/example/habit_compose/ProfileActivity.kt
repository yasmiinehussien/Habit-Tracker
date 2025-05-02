package com.example.habit_compose

import android.app.Activity
import android.content.Context
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
import androidx.compose.material.icons.filled.DarkMode
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
import coil.compose.AsyncImage
import com.example.habit_compose.ui.theme.ThemeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.res.stringResource
import com.example.habit_compose.ui.theme.updateLocale

import androidx.compose.ui.res.stringResource
import java.util.Locale












@Composable
fun ProfileScreen(themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    var imageUri by rememberSaveable { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val displayName = currentUser?.displayName?.substringBefore(" ") ?: stringResource(R.string.username)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it.toString()
        }
    }

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val selectedLanguage by themeViewModel.selectedLanguage.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var isNotificationsOn by remember { mutableStateOf(true) }
    var isVacationModeOn by remember { mutableStateOf(false) }

    // Ensure that the language update is applied when it changes
    LaunchedEffect(selectedLanguage) {
        updateLocale(context, selectedLanguage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo + Username
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
                AsyncImage(
                    model = if (imageUri.isEmpty()) R.drawable.person else imageUri,
                    contentDescription = "profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clickable { launcher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5EDC))
                ) {
                    Text(
                        text = stringResource(R.string.edit_photo),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Notifications
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notification Icon", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.notification), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isNotificationsOn,
                onCheckedChange = { isNotificationsOn = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF7441BE),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Vacation Mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DomainVerification, contentDescription = "Vacation mode", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.vacation_mode_label), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isVacationModeOn,
                onCheckedChange = { isVacationModeOn = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF7831DC),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Dark Mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DarkMode, contentDescription = "Dark Mode Icon", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.dark_mode), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { themeViewModel.toggleTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF7441BE),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Language Selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                }
        ) {
            Icon(Icons.Default.Language, contentDescription = "Language Icon", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.language), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Box {
                Text(selectedLanguage, modifier = Modifier.padding(end = 8.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("English") },
                        onClick = {
                            themeViewModel.setLanguage("en")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Arabic") },
                        onClick = {
                            themeViewModel.setLanguage("ar")
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                val googleSignInClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
                )
                googleSignInClient.signOut().addOnCompleteListener {
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
            Text(stringResource(R.string.logout), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val fakeViewModel = ThemeViewModel()
    ProfileScreen(themeViewModel = fakeViewModel)
}