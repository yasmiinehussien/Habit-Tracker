package com.example.habit_compose.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.habit_compose.R
import com.example.habit_compose.home.getUsername
import com.example.habit_compose.ui.theme.AppTheme
import com.example.habit_compose.welcome.WelcomeScreenActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(onBack:()->Unit={}) {
    BackHandler { onBack() }
    val context = LocalContext.current

    // إنشاء ViewModel للبروفايل
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(context)
    )

    // جمع البيانات من ViewModel
    val profileImageUri by profileViewModel.profileImageUri.collectAsState()
    val isDarkModeEnabled by profileViewModel.isDarkModeEnabled.collectAsState()
    val isVacationModeEnabled by profileViewModel.isVacationModeEnabled.collectAsState()

    // تطبيق السمة المناسبة (الوضع الداكن أو الفاتح)
    AppTheme(darkTheme = isDarkModeEnabled) {
        // تعريف Launcher لاختيار الصورة من المعرض
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                // حفظ URI الصورة في DataStore
                profileViewModel.saveProfileImageUri(it.toString())
            }
        }

        // تحميل الصورة من URI أو استخدام الصورة الافتراضية
        val painter = rememberAsyncImagePainter(
            model = profileImageUri.ifEmpty { R.drawable.person }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // صورة البروفايل وزر التعديل
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(150.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
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

                Column(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Edit Photo",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // زر الإشعارات
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Notification",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                var isNotificationOn by remember { mutableStateOf(true) }

                Switch(
                    checked = isNotificationOn,
                    onCheckedChange = { isNotificationOn = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color(0xFFD0D0D0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // زر الوضع الداكن
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Dark Mode",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Dark Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { profileViewModel.toggleDarkMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color(0xFFD0D0D0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // زر وضع الإجازة
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DomainVerification,
                    contentDescription = "Vacation mode",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Vacation mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = isVacationModeEnabled,
                    onCheckedChange = { profileViewModel.toggleVacationMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color(0xFFD0D0D0)
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // زر تسجيل الخروج
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()  // تسجيل الخروج من Firebase

                    // إذا كنت تستخدم Google Sign In أيضًا
                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build()
                    )
                    googleSignInClient.signOut().addOnCompleteListener {
                        // بعد تسجيل الخروج، انتقل إلى WelcomeScreenActivity
                        val intent = Intent(context, WelcomeScreenActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Logout",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}