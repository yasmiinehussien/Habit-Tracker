package com.example.habit_compose


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Composable
fun ProfileScreen() {
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
                    .border(4.dp, Color(0xFF0B640E), CircleShape)
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

            Column(
                modifier = Modifier
                    .height(150.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.align(Alignment.End)
                    ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
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
                    checkedTrackColor = Color(0xFF0F5D12),
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
                    checkedTrackColor = Color(0xFF0F5D12),
                    uncheckedTrackColor = Color(0xFFD0D0D0)
                )



            )
        }
        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF258629))
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